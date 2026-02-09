package dev.itsrealperson.permadeath.util;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.api.NetworkManagerAPI;
import dev.itsrealperson.permadeath.api.storage.GlobalPlayer;
import dev.itsrealperson.permadeath.util.log.PDCLog;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * Gestiona la comunicación entre servidores vía Redis.
 * Solo se activa si Network.Mode es NETWORK.
 */
public class NetworkManager implements NetworkManagerAPI {

    private final Main plugin;
    private JedisPool jedisPool;
    private String serverId;
    private String channel;
    private boolean enabled;
    
    private final Map<UUID, GlobalPlayer> globalPlayers = new ConcurrentHashMap<>();
    private final Map<String, List<BiConsumer<String, String>>> customListeners = new HashMap<>();

    public NetworkManager(Main plugin) {
        this.plugin = plugin;
        this.enabled = plugin.getConfig().getString("Network.Mode", "STANDALONE").equalsIgnoreCase("NETWORK");
    }

    public void init() {
        if (!enabled) return;

        this.serverId = plugin.getConfig().getString("Network.ServerID", "unknown");
        this.channel = plugin.getConfig().getString("Network.Redis.Channel", "permadeath_sync");

        String host = plugin.getConfig().getString("Network.Redis.Host", "localhost");
        int port = plugin.getConfig().getInt("Network.Redis.Port", 6379);
        String password = plugin.getConfig().getString("Network.Redis.Password", "");

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(8);

        if (password.isEmpty()) {
            this.jedisPool = new JedisPool(poolConfig, host, port);
        } else {
            this.jedisPool = new JedisPool(poolConfig, host, port, 2000, password);
        }

        startSubscription();
        PDCLog.getInstance().log("[RED] Sistema de red inicializado (Servidor: " + serverId + ")", false);
    }

    private void startSubscription() {
        new Thread(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.subscribe(new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        handleIncomingMessage(message);
                    }
                }, channel);
            } catch (Exception e) {
                PDCLog.getInstance().log("[ERROR RED] Fallo en la suscripción de Redis: " + e.getMessage(), true);
            }
        }, "Permadeath-Redis-Sub").start();
    }

    private void handleIncomingMessage(String message) {
        // Formato: ORIGIN_ID:TYPE:DATA
        String[] split = message.split(":", 3);
        if (split.length < 3) return;

        String origin = split[0];
        if (origin.equals(serverId)) return; // Ignorar mensajes propios

        String type = split[1];
        String data = split[2];

        Bukkit.getScheduler().runTask(plugin, () -> {
            switch (type) {
                case "CHAT" -> Bukkit.broadcastMessage(TextUtils.format("&8[&b" + origin + "&8] &f" + data));
                case "DAY_CHANGE" -> {
                    long newDay = Long.parseLong(data);
                    PDCLog.getInstance().log("[RED] Sincronización de día recibida: " + newDay, false);
                    dev.itsrealperson.permadeath.data.DateManager.getInstance().setDayNetwork(newDay);
                }
                case "PANIC" -> {
                    Main.PANIC_MODE = Boolean.parseBoolean(data);
                    Bukkit.broadcastMessage(TextUtils.format("&4&l[RED] &cModo de pánico actualizado por " + origin));
                }
                case "VOTE_ACTION" -> {
                    // El Bot de Discord ha enviado un comando tras una votación
                    PDCLog.getInstance().log("[RED] Ejecutando acción de votación: " + data, false);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), data);
                }
                case "DISCORD_RELAY" -> {
                    // Data: TYPE|JSON_DATA
                    String[] parts = data.split("\\|", 2);
                    if (parts.length == 2) {
                        dev.itsrealperson.permadeath.discord.DiscordManager.getInstance().handleNetworkRelay(origin, parts[0], new org.json.JSONObject(parts[1]));
                    }
                }
                case "PLAYER_JOIN" -> {
                    // Data: UUID;Name;World
                    String[] pData = data.split(";");
                    UUID uuid = UUID.fromString(pData[0]);
                    GlobalPlayer gp = new GlobalPlayer(uuid, pData[1], origin, pData[2]);
                    globalPlayers.put(uuid, gp);
                    
                    // Inyectar en TAB local
                    for (org.bukkit.entity.Player p : Bukkit.getOnlinePlayers()) {
                        PacketManager.injectPlayerToList(p, gp.getUuid(), gp.getName());
                    }
                    
                    PDCLog.getInstance().log("[RED] Jugador global conectado: " + gp.getName() + " (" + origin + ")", false);
                }
                case "PLAYER_QUIT" -> {
                    UUID uuid = UUID.fromString(data);
                    GlobalPlayer removed = globalPlayers.remove(uuid);
                    if (removed != null) {
                        for (org.bukkit.entity.Player p : Bukkit.getOnlinePlayers()) {
                            PacketManager.removePlayerFromList(p, uuid);
                        }
                        PDCLog.getInstance().log("[RED] Jugador global desconectado: " + removed.getName(), false);
                    }
                }
                case "SYNC_REQUEST" -> {
                    // Un nuevo servidor pide datos. Enviamos nuestros jugadores actuales.
                    for (org.bukkit.entity.Player p : Bukkit.getOnlinePlayers()) {
                        sendMessage("PLAYER_JOIN", p.getUniqueId() + ";" + p.getName() + ";" + p.getWorld().getName());
                    }
                }
                default -> {
                    // Manejar listeners personalizados de Addons
                    if (customListeners.containsKey(type)) {
                        customListeners.get(type).forEach(listener -> listener.accept(origin, data));
                    }
                }
            }
        });
    }

    @Override
    public void sendCustomMessage(String type, String data) {
        sendMessage(type, data);
    }

    @Override
    public void registerNetworkListener(String type, BiConsumer<String, String> listener) {
        customListeners.computeIfAbsent(type, k -> new ArrayList<>()).add(listener);
    }

    @Override
    public String getServerId() {
        return serverId;
    }

    @Override
    public Collection<GlobalPlayer> getGlobalPlayers() {
        return globalPlayers.values();
    }

    public void sendMessage(String type, String data) {
        if (!enabled || jedisPool == null) return;

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish(channel, serverId + ":" + type + ":" + data);
        } catch (Exception e) {
            PDCLog.getInstance().log("[ERROR RED] No se pudo enviar mensaje: " + e.getMessage(), true);
        }
    }

    public void shutdown() {
        if (jedisPool != null) {
            jedisPool.close();
        }
    }

    public boolean isNetworkActive() {
        return enabled;
    }
}

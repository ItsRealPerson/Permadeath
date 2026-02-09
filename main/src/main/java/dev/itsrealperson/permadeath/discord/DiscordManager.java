package dev.itsrealperson.permadeath.discord;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.util.TextUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.json.JSONObject;

import java.awt.*;
import java.io.File;
import java.time.LocalDate;

/**
 * Gestor de Discord v1.5 (Universal Hybrid Mode).
 * Soporta JDA local (Standalone/Maestro) y Relevo vía Redis (Esclavo).
 */
public class DiscordManager {

    private static DiscordManager instance;
    private final Main plugin;
    private final FileConfiguration config;
    
    private JDA jda;
    private boolean enabled;
    private boolean isMaster;

    public DiscordManager() {
        this.plugin = Main.getInstance();
        File file = new File(plugin.getDataFolder(), "discord.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
        
        if (!file.exists()) {
            plugin.saveResource("discord.yml", false);
        }
        
        this.enabled = config.getBoolean("Enable", false);
        
        if (enabled) {
            String token = config.getString("Token", "");
            if (!token.isEmpty()) {
                initJDA(token);
                this.isMaster = true;
            } else {
                Bukkit.getLogger().info("[Discord] No se detectó token. Funcionando en modo ESCLAVO (vía Redis).");
                this.isMaster = false;
            }
        }
    }

    private void initJDA(String token) {
        try {
            this.jda = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                    .setActivity(Activity.watching(config.getString("Status", "Permadeath v1.5")))
                    .build();
            this.jda.awaitReady();
            Bukkit.getLogger().info("[Discord] Bot iniciado correctamente en modo MAESTRO.");
            
            sendSimpleEmbed("Anuncios", "⚙ Plugin iniciado", "El sistema de Permadeath está en línea.", Color.GREEN);
        } catch (Exception e) {
            Bukkit.getLogger().severe("[Discord] Error al iniciar JDA: " + e.getMessage());
        }
    }

    public static DiscordManager getInstance() {
        if (instance == null) instance = new DiscordManager();
        return instance;
    }

    /**
     * Procesa un mensaje que viene desde la red (Redis).
     */
    public void handleNetworkRelay(String origin, String type, JSONObject data) {
        if (!isMaster || jda == null) return;

        // Si somos el maestro, publicamos lo que mandan los esclavos
        switch (type) {
            case "PLAYER_BAN" -> {
                // Simular un OfflinePlayer para el método de ban
                sendBanEmbed(data.getString("playerName"), data.getString("uuid"), data.getBoolean("isAFK"), data.getString("server"), data.optString("cause", "Desconocida"));
            }
            case "DEATH_TRAIN" -> sendSimpleEmbed("Anuncios", "🔥 Death Train", data.getString("message") + " (Server: " + origin + ")", Color.RED);
            case "DAY_CHANGE" -> sendSimpleEmbed("Anuncios", "☀ Cambio de Día", "El mundo ha avanzado al día " + data.getLong("day"), Color.YELLOW);
        }
    }

    private void sendRelay(String type, JSONObject data) {
        if (!enabled) return;
        
        if (plugin.getNetworkManager().isNetworkActive()) {
            // Mandar por Redis para que el Maestro lo publique
            plugin.getNetworkManager().sendCustomMessage("DISCORD_RELAY", type + "|" + data.toString());
        }
        
        // Si somos el maestro, también lo publicamos nosotros mismos
        if (isMaster && jda != null) {
            processLocalEvent(type, data);
        }
    }

    private void processLocalEvent(String type, JSONObject data) {
        switch (type) {
            case "PLAYER_BAN" -> sendBanEmbed(data.getString("playerName"), data.getString("uuid"), data.getBoolean("isAFK"), data.getString("server"), data.optString("cause", "Desconocida"));
            case "DEATH_TRAIN" -> sendSimpleEmbed("Anuncios", "🔥 Death Train", data.getString("message"), Color.RED);
            case "DAY_CHANGE" -> sendSimpleEmbed("Anuncios", "☀ Cambio de Día", "El mundo ha avanzado al día " + data.getLong("day"), Color.YELLOW);
            case "SERVER_START" -> sendSimpleEmbed("Anuncios", "✅ Servidor Conectado", "El servidor **" + data.getString("server") + "** se ha unido a la red.", Color.CYAN);
        }
    }

    // --- MÉTODOS DE EVENTOS ---

    public void banPlayer(OfflinePlayer player, boolean isAFK) {
        JSONObject json = new JSONObject();
        json.put("playerName", player.getName());
        json.put("uuid", player.getUniqueId().toString());
        json.put("isAFK", isAFK);
        json.put("server", plugin.getNetworkManager().getServerId());
        
        // Causa de muerte (si no es AFK)
        if (!isAFK) {
            var pdm = new dev.itsrealperson.permadeath.data.PlayerDataManager(player.getName(), plugin);
            json.put("cause", pdm.getBanCause());
        }

        sendRelay("PLAYER_BAN", json);
    }

    public void onDeathTrain(String msg) {
        JSONObject json = new JSONObject();
        json.put("message", TextUtils.stripColor(msg));
        sendRelay("DEATH_TRAIN", json);
    }

    public void onDayChange() {
        JSONObject json = new JSONObject();
        json.put("day", plugin.getDay());
        sendRelay("DAY_CHANGE", json);
    }

    public void onPluginEnable() {
        JSONObject json = new JSONObject();
        json.put("server", plugin.getNetworkManager().getServerId());
        sendRelay("SERVER_START", json);
    }

    public void onDisable() {
        if (isMaster && jda != null) {
            sendSimpleEmbed("Anuncios", "❌ Plugin desactivado", "El servidor maestro se está apagando.", Color.RED);
            jda.shutdown();
        }
    }

    // --- UTILIDADES DE EMBEDS ---

    private void sendSimpleEmbed(String channelKey, String title, String description, Color color) {
        if (jda == null) return;
        TextChannel channel = jda.getTextChannelById(config.getString("Channels." + channelKey, ""));
        if (channel == null) return;

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(title)
                .setDescription(description)
                .setColor(color)
                .setFooter("Permadeath v1.5 Network");

        channel.sendMessageEmbeds(eb.build()).queue();
    }

    private void sendBanEmbed(String name, String uuid, boolean isAFK, String server, String cause) {
        if (jda == null) return;
        TextChannel channel = jda.getTextChannelById(config.getString("Channels.DeathChannel", ""));
        if (channel == null) return;

        String date = String.format("%02d/%02d/%02d", LocalDate.now().getDayOfMonth(), LocalDate.now().getMonthValue(), LocalDate.now().getYear());

        EmbedBuilder eb = new EmbedBuilder()
                .setAuthor("Permadeath Network", null, "https://mineskin.eu/headhelm/" + name + "/100.png")
                .setTitle("☠ ¡JUGADOR ELIMINADO!")
                .setDescription("**" + name + "** ha sido permabaneado en el servidor **" + server + "**.")
                .addField("📅 Fecha", date, true)
                .addField("💀 Razón", isAFK ? "AFK (Inactividad)" : cause, true)
                .setThumbnail("https://mineskin.eu/headhelm/" + name + "/100.png")
                .setColor(Color.RED)
                .setFooter("ID: " + uuid);

        channel.sendMessageEmbeds(eb.build()).queue(m -> m.addReaction(Emoji.fromFormatted("☠")).queue());
    }
}

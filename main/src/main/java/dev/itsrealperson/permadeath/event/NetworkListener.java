package dev.itsrealperson.permadeath.event;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.util.TextUtils;
import dev.itsrealperson.permadeath.api.event.PermadeathDayChangeEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Escucha eventos locales para sincronizarlos con la red Redis.
 */
public class NetworkListener implements Listener {

    private final Main plugin;
    public static boolean IGNORE_NEXT_DAY_CHANGE = false;

    public NetworkListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        if (!plugin.getNetworkManager().isNetworkActive()) return;

        String message = PlainTextComponentSerializer.plainText().serialize(event.message());
        String playerName = event.getPlayer().getName();
        
        // Enviar a la red: "PlayerName: Mensaje"
        plugin.getNetworkManager().sendCustomMessage("CHAT", playerName + ": " + message);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDayChange(PermadeathDayChangeEvent event) {
        if (!plugin.getNetworkManager().isNetworkActive()) return;
        
        if (IGNORE_NEXT_DAY_CHANGE) {
            IGNORE_NEXT_DAY_CHANGE = false;
            return;
        }

        plugin.getNetworkManager().sendCustomMessage("DAY_CHANGE", String.valueOf(event.getNewDay()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        if (!plugin.getNetworkManager().isNetworkActive()) return;
        
        var p = event.getPlayer();
        plugin.getNetworkManager().sendCustomMessage("PLAYER_JOIN", p.getUniqueId() + ";" + p.getName() + ";" + p.getWorld().getName());
        
        // Enviar caché actual al jugador que entra
        for (var gp : plugin.getNetworkManager().getGlobalPlayers()) {
            dev.itsrealperson.permadeath.util.PacketManager.injectPlayerToList(p, gp.getUuid(), gp.getName());
        }

        // Pedir sincronización a otros servidores si somos el primer jugador local
        if (org.bukkit.Bukkit.getOnlinePlayers().size() == 1) {
            plugin.getNetworkManager().sendCustomMessage("SYNC_REQUEST", "");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        if (!plugin.getNetworkManager().isNetworkActive()) return;
        
        plugin.getNetworkManager().sendCustomMessage("PLAYER_QUIT", event.getPlayer().getUniqueId().toString());
    }
}

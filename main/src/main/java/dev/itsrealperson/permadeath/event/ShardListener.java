package dev.itsrealperson.permadeath.event;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.util.ProxyTransfer;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class ShardListener implements Listener {

    private final Main plugin;

    public ShardListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        // Optimización: Solo comprobar si cambia de bloque
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && 
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        Location to = event.getTo();
        // Comprobar si el jugador ha salido del Shard actual
        if (!plugin.getShardManager().isLocationInCurrentShard(to)) {
            String targetServer = plugin.getShardManager().getServerForLocation(to);
            
            if (targetServer != null && !targetServer.equals(plugin.getShardManager().getCurrentProxyName())) {
                ProxyTransfer.transferPlayer(event.getPlayer(), targetServer);
            } else {
                // Si sale de los límites y no hay servidor, rebote (World Border invisible)
                event.setCancelled(true);
                event.getPlayer().sendMessage("§cHas alcanzado el límite de este mundo.");
            }
        }
    }
}

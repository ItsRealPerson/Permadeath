package tech.sebazcrc.permadeath.nms.v1_21_R3.entity;

import org.bukkit.Location;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import org.bukkit.util.Vector;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.TeleportUtils;

public class CustomGhast {

    public static Ghast spawn(Location loc, Plugin plugin) {
        Ghast ghast = (Ghast) loc.getWorld().spawnEntity(loc, EntityType.GHAST, CreatureSpawnEvent.SpawnReason.CUSTOM);

        ghast.setCustomName("§6Ghast del Fin");
        ghast.setCustomNameVisible(false);

        if (ghast.getAttribute(Attribute.MAX_HEALTH) != null) {
            ghast.getAttribute(Attribute.MAX_HEALTH).setBaseValue(100.0);
            ghast.setHealth(100.0);
        }

        // IA: Mantener al Ghast enfocado y en movimiento
        Runnable ghastTask = new Runnable() {
            @Override
            public void run() {
                if (ghast.isDead() || !ghast.isValid()) {
                    return;
                }

                Player target = null;
                double minDistance = 64.0;

                for (Player p : ghast.getWorld().getPlayers()) {
                    GameMode gm = p.getGameMode();
                    if (gm != GameMode.SURVIVAL && gm != GameMode.ADVENTURE) continue;
                    double dist = p.getLocation().distance(ghast.getLocation());
                    if (dist < minDistance) {
                        target = p;
                        minDistance = dist;
                    }
                }

                if (target != null) {
                    ghast.setTarget(target);
                    
                    // Forzar movimiento si tiene pasajeros o la IA nativa falla
                    if (!ghast.getPassengers().isEmpty() || ghast.getLocation().distanceSquared(target.getLocation()) > 20 * 20) {
                        TeleportUtils.lookAt(ghast, target.getLocation());
                        Vector dir = target.getLocation().toVector().subtract(ghast.getLocation().toVector()).normalize();
                        ghast.setVelocity(dir.multiply(0.35));
                    }
                }
            }
        };

        try {
            ghast.getScheduler().runAtFixedRate(plugin, t -> ghastTask.run(), null, 20L, 20L);
        } catch (NoSuchMethodError e) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (ghast.isDead() || !ghast.isValid()) { this.cancel(); return; }
                    ghastTask.run();
                }
            }.runTaskTimer(plugin, 20L, 40L);
        }

        return ghast;
    }
}



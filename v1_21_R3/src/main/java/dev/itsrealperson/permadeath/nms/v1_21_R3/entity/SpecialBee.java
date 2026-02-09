package dev.itsrealperson.permadeath.nms.v1_21_R3.entity;

import org.bukkit.Location;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Bee;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import org.bukkit.util.Vector;
import dev.itsrealperson.permadeath.nms.v1_21_R3.utils.TeleportUtils;

public class SpecialBee {

    public static Bee spawn(Location loc, Plugin plugin) {
        Bee bee = (Bee) loc.getWorld().spawnEntity(loc, EntityType.BEE, CreatureSpawnEvent.SpawnReason.CUSTOM);

        bee.setCustomName("§eAngry Bee");
        bee.setCustomNameVisible(true);

        // Stats
        if (bee.getAttribute(Attribute.MAX_HEALTH) != null) {
            bee.getAttribute(Attribute.MAX_HEALTH).setBaseValue(40.0);
            bee.setHealth(40.0);
        }

        // Tarea para mantener la ira y el objetivo
        Runnable beeTask = new Runnable() {
            @Override
            public void run() {
                if (bee.isDead() || !bee.isValid()) {
                    return;
                }

                // Siempre furiosa
                bee.setAnger(Integer.MAX_VALUE);

                Player target = null;
                double minDistance = 25.0;

                for (Player p : bee.getWorld().getPlayers()) {
                    GameMode gm = p.getGameMode();
                    if (gm != GameMode.SURVIVAL && gm != GameMode.ADVENTURE) continue;
                    double dist = p.getLocation().distance(bee.getLocation());
                    if (dist < minDistance) {
                        target = p;
                        minDistance = dist;
                    }
                }

                if (target != null) {
                    bee.setTarget(target);

                    // Forzar movimiento si tiene pasajeros o la IA nativa falla (las abejas son lentas con peso)
                    if (!bee.getPassengers().isEmpty() || bee.getLocation().distanceSquared(target.getLocation()) > 10 * 10) {
                        TeleportUtils.lookAt(bee, target.getLocation());
                        Vector dir = target.getLocation().toVector().subtract(bee.getLocation().toVector()).normalize();
                        bee.setVelocity(dir.multiply(0.4));
                    }
                }
            }
        };

        try {
            bee.getScheduler().runAtFixedRate(plugin, t -> beeTask.run(), null, 20L, 20L);
        } catch (NoSuchMethodError e) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (bee.isDead() || !bee.isValid()) { this.cancel(); return; }
                    beeTask.run();
                }
            }.runTaskTimer(plugin, 20L, 20L);
        }

        return bee;
    }
}



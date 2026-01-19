package tech.sebazcrc.permadeath.nms.v1_21_R3.entity;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Bee;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class SpecialBee {

    public static Bee spawn(Location loc, Plugin plugin) {
        Bee bee = (Bee) loc.getWorld().spawnEntity(loc, EntityType.BEE, CreatureSpawnEvent.SpawnReason.CUSTOM);

        bee.setCustomName("§e§lAngry Bee");
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

                if (bee.getTarget() == null) {
                    Player target = null;
                    double minDistance = 20.0;

                    for (Player p : bee.getWorld().getPlayers()) {
                        if (p.getGameMode().getValue() == 1 || p.getGameMode().getValue() == 3) continue;
                        double dist = p.getLocation().distance(bee.getLocation());
                        if (dist < minDistance) {
                            target = p;
                            minDistance = dist;
                        }
                    }

                    if (target != null) {
                        bee.setTarget(target);
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













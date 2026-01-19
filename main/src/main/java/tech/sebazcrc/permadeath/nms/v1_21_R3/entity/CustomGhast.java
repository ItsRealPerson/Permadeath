package tech.sebazcrc.permadeath.nms.v1_21_R3.entity;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class CustomGhast {

    public static Ghast spawn(Location loc, Plugin plugin) {
        Ghast ghast = (Ghast) loc.getWorld().spawnEntity(loc, EntityType.GHAST, CreatureSpawnEvent.SpawnReason.CUSTOM);

        ghast.setCustomName("§6Ender Ghast");
        ghast.setCustomNameVisible(false);

        if (ghast.getAttribute(Attribute.MAX_HEALTH) != null) {
            ghast.getAttribute(Attribute.MAX_HEALTH).setBaseValue(100.0);
            ghast.setHealth(100.0);
        }

        // IA: Mantener al Ghast enfocado
        Runnable ghastTask = new Runnable() {
            @Override
            public void run() {
                if (ghast.isDead() || !ghast.isValid()) {
                    return;
                }

                if (ghast.getTarget() == null) {
                    double minDistance = 64.0;
                    Player target = null;

                    for (Player p : ghast.getWorld().getPlayers()) {
                        if (p.getGameMode().getValue() == 1 || p.getGameMode().getValue() == 3) continue;
                        double dist = p.getLocation().distance(ghast.getLocation());
                        if (dist < minDistance) {
                            target = p;
                            minDistance = dist;
                        }
                    }

                    if (target != null) {
                        ghast.setTarget(target);
                    }
                }
            }
        };

        try {
            ghast.getScheduler().runAtFixedRate(plugin, t -> ghastTask.run(), null, 20L, 40L);
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





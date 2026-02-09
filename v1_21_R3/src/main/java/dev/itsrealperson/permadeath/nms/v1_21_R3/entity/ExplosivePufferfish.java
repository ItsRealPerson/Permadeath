package dev.itsrealperson.permadeath.nms.v1_21_R3.entity;

import org.bukkit.plugin.Plugin;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.PufferFish;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import dev.itsrealperson.permadeath.nms.v1_21_R3.utils.MobUtils;
import dev.itsrealperson.permadeath.nms.v1_21_R3.utils.TeleportUtils;

public class ExplosivePufferfish {
    public static PufferFish spawn(Location loc, Plugin plugin) {
        PufferFish fish = (PufferFish) loc.getWorld().spawnEntity(loc, EntityType.PUFFERFISH, CreatureSpawnEvent.SpawnReason.CUSTOM);
        fish.setCustomName("§cSea Mine");
        fish.setPuffState(2); // Siempre inflado

        Runnable fishTask = new Runnable() {
            @Override
            public void run() {
                if (!fish.isValid()) { return; }
                Player p = MobUtils.getNearestPlayer(fish, 10);

                if (p != null) {
                    TeleportUtils.moveTowards(fish, p.getLocation(), 0.3, 0); // Perseguir lento

                    if (fish.getLocation().distanceSquared(p.getLocation()) < 4.0) {
                        fish.getWorld().createExplosion(fish.getLocation(), 6.0f, false, false);
                        fish.remove();
                    }
                }
            }
        };

        try {
            fish.getScheduler().runAtFixedRate(plugin, t -> fishTask.run(), null, 10L, 10L);
        } catch (NoSuchMethodError e) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!fish.isValid()) { this.cancel(); return; }
                    fishTask.run();
                }
            }.runTaskTimer(plugin, 10, 10);
        }
        return fish;
    }
}




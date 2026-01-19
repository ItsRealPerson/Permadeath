package tech.sebazcrc.permadeath.nms.v1_21_R3.entity.minions;

import org.bukkit.plugin.Plugin;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.EffectUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.MobUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.ParticleUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.TeleportUtils;

public class SculkParasite {

    public static Silverfish spawn(Location loc, Plugin plugin) {
        Silverfish fish = (Silverfish) loc.getWorld().spawnEntity(loc, EntityType.SILVERFISH, CreatureSpawnEvent.SpawnReason.CUSTOM);
        fish.setCustomName("§3§lSculk Parasite");

        EffectUtils.setMaxHealth(fish, 20.0);
        EffectUtils.setMovementSpeed(fish, 0.4);

        Runnable fishTask = new Runnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (!fish.isValid()) { return; }
                ticks++;

                if (ticks % 60 == 0) {
                    Player target = MobUtils.getNearestPlayer(fish, 15);
                    if (target != null) {
                        fish.getWorld().playSound(fish.getLocation(), Sound.ENTITY_WARDEN_SNIFF, 1.0f, 2.0f);
                        ParticleUtils.drawLine(fish.getLocation(), target.getEyeLocation(), Particle.SCULK_SOUL, 1);
                        fish.setTarget(target);
                    }
                }

                if (fish.getTarget() != null) {
                    TeleportUtils.moveTowards(fish, fish.getTarget().getLocation(), 0.5, 0.2);
                }
            }
        };

        try {
            fish.getScheduler().runAtFixedRate(plugin, t -> fishTask.run(), null, 10L, 5L);
        } catch (NoSuchMethodError e) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!fish.isValid()) { this.cancel(); return; }
                    fishTask.run();
                }
            }.runTaskTimer(plugin, 10, 5);
        }

        return fish;
    }
}






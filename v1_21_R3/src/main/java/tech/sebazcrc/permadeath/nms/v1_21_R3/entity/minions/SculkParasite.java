package tech.sebazcrc.permadeath.nms.v1_21_R3.entity.minions;

import org.bukkit.plugin.Plugin;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.EffectUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.MobUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.ParticleUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.TeleportUtils;

public class SculkParasite {

    public static Silverfish spawn(Location loc, Plugin plugin) {
        Silverfish fish = (Silverfish) loc.getWorld().spawnEntity(loc, EntityType.SILVERFISH, CreatureSpawnEvent.SpawnReason.CUSTOM);
        fish.setCustomName("§3Parásito de Sculk");
        fish.setCustomNameVisible(true);

        EffectUtils.setMaxHealth(fish, 300.0);
        EffectUtils.addPotionEffect(fish, new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
        EffectUtils.addPotionEffect(fish, new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 10));

        Runnable parasiteTask = new Runnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (!fish.isValid()) return;
                ticks++;

                // Partículas de Sculk al moverse
                if (ticks % 5 == 0) {
                    fish.getWorld().spawnParticle(Particle.SCULK_CHARGE_POP, fish.getLocation().add(0, 0.2, 0), 3, 0.1, 0.1, 0.1, 0.05);
                }

                // --- Lógica de Olfato y Sigilo ---
                Player target = MobUtils.getNearestPlayer(fish, 45); 

                if (target != null) {
                    double distSq = fish.getLocation().distanceSquared(target.getLocation());
                    boolean isSneaking = target.isSneaking();
                    double detectionRangeSq = isSneaking ? 8 * 8 : 24 * 24;

                    if (distSq > detectionRangeSq) {
                        if (fish.getTarget() != null) fish.setTarget(null);
                        return;
                    }

                    fish.setTarget(target);
                    TeleportUtils.moveTowards(fish, target.getLocation(), 0.65, 0.1);
                    
                    // Inyectar parásito si toca al jugador
                    if (distSq < 2.5) {
                        target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 3));
                        target.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 300, 4));
                        target.playSound(target.getLocation(), Sound.ENTITY_SILVERFISH_HURT, 1.0f, 0.5f);
                    }
                }
            }
        };

        try {
            fish.getScheduler().runAtFixedRate(plugin, t -> parasiteTask.run(), null, 10L, 5L);
        } catch (NoSuchMethodError e) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!fish.isValid()) { this.cancel(); return; }
                    parasiteTask.run();
                }
            }.runTaskTimer(plugin, 10, 5);
        }

        return fish;
    }
}




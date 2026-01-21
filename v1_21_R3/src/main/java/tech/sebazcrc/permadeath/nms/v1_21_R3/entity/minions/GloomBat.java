package tech.sebazcrc.permadeath.nms.v1_21_R3.entity.minions;

import org.bukkit.plugin.Plugin;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.EffectUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.MobUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.ParticleUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.TeleportUtils;

public class GloomBat {

    public static Bat spawn(Location loc, Plugin plugin) {
        Bat bat = (Bat) loc.getWorld().spawnEntity(loc, EntityType.BAT, CreatureSpawnEvent.SpawnReason.CUSTOM);
        bat.setCustomName("§8Explorador SombrÃ­o");
        bat.setCustomNameVisible(true);

        EffectUtils.setMaxHealth(bat, 250.0); // Extremadamente difÃ­cil de matar para un murciÃ©lago
        EffectUtils.addPotionEffect(bat, new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 2));

        Runnable batTask = new Runnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (!bat.isValid()) { return; }
                ticks++;

                // Vuelo errÃ¡tico con partÃ­culas
                bat.getWorld().spawnParticle(Particle.SCULK_CHARGE_POP, bat.getLocation(), 1, 0.1, 0.1, 0.1, 0.01);

                Player target = MobUtils.getNearestPlayer(bat, 30);

                if (target != null) {
                    // Si encuentra jugador:
                    if (ticks % 30 == 0) { // MÃ¡s frecuente
                        // 1. Olfateo audible
                        bat.getWorld().playSound(bat.getLocation(), Sound.ENTITY_WARDEN_SNIFF, 1.0f, 2.0f);
                        // 2. Chillar (Sonar)
                        bat.getWorld().playSound(bat.getLocation(), Sound.ENTITY_WARDEN_LISTENING_ANGRY, 1.2f, 1.8f);

                        // 3. Aplicar Efectos Debilitantes
                        EffectUtils.addPotionEffect(target, new PotionEffect(PotionEffectType.DARKNESS, 140, 0));
                        EffectUtils.addPotionEffect(target, new PotionEffect(PotionEffectType.MINING_FATIGUE, 200, 2));
                        
                        // Drenar comida
                        target.setFoodLevel(Math.max(0, target.getFoodLevel() - 1));

                        // 4. SeÃ±alar visualmente
                        bat.getWorld().spawnParticle(Particle.SONIC_BOOM, bat.getLocation(), 1, 0, 0, 0, 0);
                    }

                    // PersecuciÃ³n aÃ©rea forzada
                    TeleportUtils.lookAt(bat, target.getEyeLocation());
                    TeleportUtils.moveTowards(bat, target.getEyeLocation().add(0, 3, 0), 0.5, 0);
                }
            }
        };

        try {
            bat.getScheduler().runAtFixedRate(plugin, t -> batTask.run(), null, 10L, 5L);
        } catch (NoSuchMethodError e) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!bat.isValid()) { this.cancel(); return; }
                    batTask.run();
                }
            }.runTaskTimer(plugin, 10, 5);
        }

        return bat;
    }
}




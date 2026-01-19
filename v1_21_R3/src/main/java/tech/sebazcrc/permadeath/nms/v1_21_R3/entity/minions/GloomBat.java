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
        bat.setCustomName("§8§lGloom Scout");
        bat.setCustomNameVisible(true);

        EffectUtils.setMaxHealth(bat, 20.0); // Difícil de matar para un murciélago

        Runnable batTask = new Runnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (!bat.isValid()) { return; }
                ticks++;

                // Vuelo errático con partículas
                bat.getWorld().spawnParticle(Particle.SCULK_CHARGE_POP, bat.getLocation(), 1, 0.1, 0.1, 0.1, 0.01);

                Player target = MobUtils.getNearestPlayer(bat, 15);

                if (target != null) {
                    // Si encuentra jugador:
                    if (ticks % 40 == 0) {
                        // 1. Olfateo audible
                        bat.getWorld().playSound(bat.getLocation(), Sound.ENTITY_WARDEN_SNIFF, 0.8f, 2.0f);
                        // 2. Chillar (Sonar)
                        bat.getWorld().playSound(bat.getLocation(), Sound.ENTITY_WARDEN_LISTENING_ANGRY, 1.0f, 1.8f);

                        // 3. Aplicar Darkness
                        EffectUtils.addPotionEffect(target, new PotionEffect(PotionEffectType.DARKNESS, 100, 0));

                        // 4. Señalar visualmente
                        ParticleUtils.drawLine(bat.getLocation(), target.getEyeLocation(), Particle.SCULK_SOUL, 1);
                    }

                    // Intentar seguir al jugador (los murciélagos tienen IA caótica, forzamos un poco)
                    TeleportUtils.moveTowards(bat, target.getEyeLocation().add(0, 2, 0), 0.3, 0);
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



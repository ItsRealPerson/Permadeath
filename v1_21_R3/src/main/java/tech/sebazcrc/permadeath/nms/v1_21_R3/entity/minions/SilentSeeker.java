package tech.sebazcrc.permadeath.nms.v1_21_R3.entity.minions;

import org.bukkit.plugin.Plugin;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.EffectUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.MobUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.TeleportUtils;

public class SilentSeeker {

    public static Creeper spawn(Location loc, Plugin plugin) {
        Creeper creeper = (Creeper) loc.getWorld().spawnEntity(loc, EntityType.CREEPER, CreatureSpawnEvent.SpawnReason.CUSTOM);
        creeper.setCustomName("§1Buscador Silencioso");
        creeper.setMaxFuseTicks(15); // ExplosiÃ³n rÃ¡pida
        creeper.setExplosionRadius(0); // No rompe bloques, pero...

        // Hacemos que sea "Casi" invisible (transparente) y silencioso
        EffectUtils.addPotionEffect(creeper, new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
        creeper.setSilent(true); // Sin sonido de pasos vanilla

        // MÃ¡s salud para aguantar hasta llegar
        EffectUtils.setMaxHealth(creeper, 400.0);
        EffectUtils.addPotionEffect(creeper, new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 2));

        Runnable seekerTask = new Runnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (!creeper.isValid()) { return; }
                ticks++;

                // Solo partÃ­culas para verlo
                creeper.getWorld().spawnParticle(Particle.SCULK_SOUL, creeper.getLocation().add(0, 0.5, 0), 1, 0.1, 0.1, 0.1, 0.01);

                // 1. DetecciÃ³n tipo Warden (Rango ampliado, ignora paredes si estÃ¡s cerca)
                Player target = MobUtils.getNearestPlayer(creeper, 30);

                if (target != null) {
                    creeper.setTarget(target);
                    double distSq = creeper.getLocation().distanceSquared(target.getLocation());
                    
                    // Seguimiento constante
                    TeleportUtils.lookAt(creeper, target.getLocation());
                    TeleportUtils.moveTowards(creeper, target.getLocation(), 0.55, 0.2);

                    if (distSq < 144) { // < 12 bloques (Te "oye")
                        if (ticks % 15 == 0) {
                            creeper.getWorld().playSound(creeper.getLocation(), Sound.ENTITY_WARDEN_HEARTBEAT, 1.2f, 1.8f);
                            creeper.getWorld().spawnParticle(Particle.SONIC_BOOM, creeper.getLocation().add(0, 1, 0), 1, 0, 0, 0, 0);
                        }
                    }

                    // ExplosiÃ³n sÃ³nica personalizada (Letal)
                    if (distSq < 4) {
                        // DetonaciÃ³n manual para efecto sÃ³nico
                        creeper.getWorld().playSound(creeper.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 2.0f, 1.5f);
                        creeper.getWorld().spawnParticle(Particle.SONIC_BOOM, creeper.getLocation(), 5);
                        target.damage(45.0, creeper); // 22.5 corazones
                        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 0));
                        target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 4));
                        creeper.remove();
                    }
                } else if (ticks % 60 == 0) {
                    // Olfatear si no hay target
                    creeper.getWorld().playSound(creeper.getLocation(), Sound.ENTITY_WARDEN_SNIFF, 0.5f, 2.0f);
                }
            }
        };

        try {
            creeper.getScheduler().runAtFixedRate(plugin, t -> seekerTask.run(), null, 10L, 5L);
        } catch (NoSuchMethodError e) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!creeper.isValid()) { this.cancel(); return; }
                    seekerTask.run();
                }
            }.runTaskTimer(plugin, 10, 5);
        }

        return creeper;
    }
}




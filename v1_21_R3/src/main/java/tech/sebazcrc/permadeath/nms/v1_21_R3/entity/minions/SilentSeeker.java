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
        creeper.setCustomName("§1§lSilent Seeker");
        creeper.setMaxFuseTicks(15); // Explosión rápida
        creeper.setExplosionRadius(0); // No rompe bloques, pero...

        // Hacemos que sea "Casi" invisible (transparente) y silencioso
        EffectUtils.addPotionEffect(creeper, new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
        creeper.setSilent(true); // Sin sonido de pasos vanilla

        // Más salud para aguantar hasta llegar
        EffectUtils.setMaxHealth(creeper, 40.0);

        Runnable seekerTask = new Runnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (!creeper.isValid()) { return; }
                ticks++;

                // Solo partículas para verlo
                creeper.getWorld().spawnParticle(Particle.SCULK_SOUL, creeper.getLocation().add(0, 0.5, 0), 1, 0.1, 0.1, 0.1, 0.01);

                Player target = MobUtils.getNearestPlayer(creeper, 15);

                // Mecánica de "Latido" al acercarse
                if (target != null) {
                    creeper.setTarget(target);
                    double distSq = creeper.getLocation().distanceSquared(target.getLocation());

                    if (distSq < 100) { // < 10 bloques
                        if (ticks % 20 == 0) { // Latido cada segundo
                            creeper.getWorld().playSound(creeper.getLocation(), Sound.ENTITY_WARDEN_HEARTBEAT, 1.0f, 1.5f);
                            // Efecto visual de latido
                            creeper.getWorld().spawnParticle(Particle.SONIC_BOOM, creeper.getLocation(), 1, 0, 0, 0, 0);
                        }
                    }

                    // Explosión sónica personalizada
                    if (distSq < 4) {
                        // Detonación manual para efecto sónico
                        creeper.getWorld().playSound(creeper.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 2.0f, 1.5f);
                        creeper.getWorld().spawnParticle(Particle.SONIC_BOOM, creeper.getLocation(), 5);
                        target.damage(18.0, creeper); // Daño directo alto
                        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0));
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



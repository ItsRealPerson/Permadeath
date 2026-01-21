package tech.sebazcrc.permadeath.nms.v1_21_R3.entity.minions;

import org.bukkit.plugin.Plugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Stray; // Stray para el look de ropa rota
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.EffectUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.InventoryUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.MobUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.ParticleUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.TeleportUtils;

public class EchoArcher {

    public static Stray spawn(Location loc, Plugin plugin) {
        Stray stray = (Stray) loc.getWorld().spawnEntity(loc, EntityType.STRAY, CreatureSpawnEvent.SpawnReason.CUSTOM);
        stray.setCustomName("§b§lArquero del Eco");

        InventoryUtils.setMainHand(stray, Material.BOW);
        InventoryUtils.clearDropChances(stray);
        EffectUtils.setMaxHealth(stray, 180.0);

        Runnable archerTask = new Runnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (!stray.isValid()) { return; }
                ticks++;

                // Rastro de partículas sónicas
                if (ticks % 10 == 0) {
                    stray.getWorld().spawnParticle(Particle.SONIC_BOOM, stray.getLocation().add(0, 1, 0), 0, 0, 0, 0);
                }

                Player target = MobUtils.getNearestPlayer(stray, 40);
                if (target != null) {
                    stray.setTarget(target);

                    // Ataque Sonic Boom (Cada 4 segundos / 80 ticks)
                    if (ticks % 80 == 0) {
                        performSonicAttack(stray, target);
                    }

                    // Olfateo visual
                    if (ticks % 160 == 0) {
                        stray.getWorld().playSound(stray.getLocation(), Sound.ENTITY_WARDEN_SNIFF, 1.0f, 1.2f);
                        ParticleUtils.drawLine(stray.getEyeLocation(), target.getEyeLocation(), Particle.TRIAL_SPAWNER_DETECTION, 2);
                    }
                }
            }

            private void performSonicAttack(Stray source, Player target) {
                source.getWorld().playSound(source.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 2.0f, 1.5f);
                
                org.bukkit.util.Vector direction = target.getEyeLocation().toVector().subtract(source.getEyeLocation().toVector()).normalize();
                Location start = source.getEyeLocation();

                // Proyectil sónico (alcance 20 bloques)
                for (int i = 1; i < 20; i++) {
                    Location point = start.clone().add(direction.clone().multiply(i));
                    if (!point.getBlock().getType().isAir() && point.getBlock().getType().isSolid()) break;

                    source.getWorld().spawnParticle(Particle.SONIC_BOOM, point, 1, 0, 0, 0, 0);

                    // Dañar entidades cercanas al punto (EXCLUYENDO AL ARQUERO)
                    point.getWorld().getNearbyEntities(point, 1.5, 1.5, 1.5).forEach(entity -> {
                        if (entity instanceof LivingEntity liv && !entity.equals(source)) {
                            liv.damage(20.0, source); // 10 corazones
                            liv.setVelocity(direction.clone().multiply(1.2).setY(0.5));
                        }
                    });
                }
            }
        };

        try {
            stray.getScheduler().runAtFixedRate(plugin, t -> archerTask.run(), null, 10L, 5L);
        } catch (NoSuchMethodError e) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!stray.isValid()) { this.cancel(); return; }
                    archerTask.run();
                }
            }.runTaskTimer(plugin, 10, 5);
        }

        return stray;
    }
}



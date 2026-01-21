package tech.sebazcrc.permadeath.nms.v1_21_R3.entity;

import org.bukkit.plugin.Plugin;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Breeze;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.EffectUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.MobUtils;

public class ArcaneBreeze {

    public static Breeze spawn(Location loc, Plugin plugin) {
        Breeze breeze = (Breeze) loc.getWorld().spawnEntity(loc, EntityType.BREEZE, CreatureSpawnEvent.SpawnReason.CUSTOM);

        breeze.setCustomName("§5Arcane Breeze");
        breeze.setCustomNameVisible(true);

        EffectUtils.setMaxHealth(breeze, 80.0);
        EffectUtils.setAttackDamage(breeze, 15.0); // DaÃ±o de contacto
        EffectUtils.setFollowRange(breeze, 40.0);

        // IA Manual
        Runnable breezeTask = new Runnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (!breeze.isValid()) { return; }
                ticks++;

                // PartÃ­culas constantes
                breeze.getWorld().spawnParticle(Particle.TRIAL_SPAWNER_DETECTION, breeze.getLocation().add(0, 1, 0), 5, 0.5, 0.5, 0.5, 0.05);

                Player target = MobUtils.getNearestPlayer(breeze, 30);
                if (target != null) {
                    breeze.setTarget(target);
                    
                    // Efecto de empuje sÃ³nico si estÃ¡ muy cerca
                    if (breeze.getLocation().distanceSquared(target.getLocation()) < 9) {
                        if (ticks % 20 == 0) {
                            target.setVelocity(target.getLocation().toVector().subtract(breeze.getLocation().toVector()).normalize().multiply(2.0).setY(0.5));
                            target.getWorld().playSound(target.getLocation(), Sound.ENTITY_BREEZE_WIND_BURST, 1.5f, 1.2f);
                            target.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.INSTANT_DAMAGE, 1, 1));
                        }
                    }
                }
            }
        };

        try {
            breeze.getScheduler().runAtFixedRate(plugin, t -> breezeTask.run(), null, 10L, 5L);
        } catch (NoSuchMethodError e) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!breeze.isValid()) { this.cancel(); return; }
                    breezeTask.run();
                }
            }.runTaskTimer(plugin, 10, 5);
        }

        return breeze;
    }
}




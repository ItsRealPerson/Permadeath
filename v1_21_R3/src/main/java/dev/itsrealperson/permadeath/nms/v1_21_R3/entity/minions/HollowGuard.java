package dev.itsrealperson.permadeath.nms.v1_21_R3.entity.minions;

import org.bukkit.plugin.Plugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Husk; // Husk porque no se quema al sol (útil si salen de la cueva)
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import dev.itsrealperson.permadeath.nms.v1_21_R3.utils.EffectUtils;
import dev.itsrealperson.permadeath.nms.v1_21_R3.utils.InventoryUtils;
import dev.itsrealperson.permadeath.nms.v1_21_R3.utils.MobUtils;
import dev.itsrealperson.permadeath.nms.v1_21_R3.utils.ParticleUtils;
import dev.itsrealperson.permadeath.nms.v1_21_R3.utils.TeleportUtils;

public class HollowGuard {

    public static Husk spawn(Location loc, Plugin plugin) {
        Husk guard = (Husk) loc.getWorld().spawnEntity(loc, EntityType.HUSK, CreatureSpawnEvent.SpawnReason.CUSTOM);
        guard.setCustomName("§3Guardián del Vacío");
        guard.setCustomNameVisible(true);

        // Armadura Oscura (Netherite)
        InventoryUtils.equipArmor(guard,
                new ItemStack(Material.NETHERITE_HELMET),
                new ItemStack(Material.NETHERITE_CHESTPLATE),
                null, null);
        InventoryUtils.clearDropChances(guard);

        EffectUtils.setMaxHealth(guard, 1000.0);
        EffectUtils.setAttackDamage(guard, 60.0);
        EffectUtils.setKnockbackResistance(guard, 1.0);
        EffectUtils.setMovementSpeed(guard, 0.25);

        EffectUtils.addPotionEffect(guard, new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 10)); // Fuerza XI
        EffectUtils.addPotionEffect(guard, new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 3)); // Resistencia IV

        Runnable guardTask = new Runnable() {
            int ticks = 0;
            boolean sniffing = false;

            @Override
            public void run() {
                if (!guard.isValid()) { return; }
                ticks++;

                // --- Lógica de Olfato y Sigilo ---
                Player target = MobUtils.getNearestPlayer(guard, 45);

                if (target != null) {
                    double distSq = guard.getLocation().distanceSquared(target.getLocation());
                    boolean isSneaking = target.isSneaking();
                    double detectionRangeSq = isSneaking ? 8 * 8 : 24 * 24;

                    if (distSq > detectionRangeSq) {
                        if (guard.getTarget() != null) guard.setTarget(null);
                        return;
                    }

                    // Mecánica de Olfateo (Cada 4 segundos)
                    if (ticks % 80 == 0) {
                        sniffing = true;
                        guard.setVelocity(new org.bukkit.util.Vector(0, 0, 0));
                        guard.getWorld().playSound(guard.getLocation(), Sound.ENTITY_WARDEN_SNIFF, 1.2f, 0.7f);
                        TeleportUtils.lookAt(guard, target.getEyeLocation());
                        guard.getWorld().playSound(guard.getLocation(), Sound.ENTITY_WARDEN_AGITATED, 1.2f, 0.8f);
                        EffectUtils.addPotionEffect(guard, new PotionEffect(PotionEffectType.SPEED, 100, 3));
                        sniffing = false;
                    }

                    if (!sniffing) {
                        guard.setTarget(target);
                        TeleportUtils.moveTowards(guard, target.getLocation(), 0.45, 0.2);
                        
                        if (distSq < 4.0) {
                            target.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 100, 0));
                            target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 2));
                        }
                        
                        ParticleUtils.trailEntity(guard, Particle.ASH);
                    }
                }
            }
        };

        try {
            guard.getScheduler().runAtFixedRate(plugin, t -> guardTask.run(), null, 10L, 5L);
        } catch (NoSuchMethodError e) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!guard.isValid()) { this.cancel(); return; }
                    guardTask.run();
                }
            }.runTaskTimer(plugin, 10, 5);
        }

        return guard;
    }
}




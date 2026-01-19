package tech.sebazcrc.permadeath.nms.v1_21_R3.entity.minions;

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

import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.EffectUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.InventoryUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.MobUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.ParticleUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.TeleportUtils;

public class HollowGuard {

    public static Husk spawn(Location loc, Plugin plugin) {
        Husk guard = (Husk) loc.getWorld().spawnEntity(loc, EntityType.HUSK, CreatureSpawnEvent.SpawnReason.CUSTOM);
        guard.setCustomName("§3§lHollow Guard");
        guard.setCustomNameVisible(true);

        // Armadura Oscura (Netherite)
        InventoryUtils.equipArmor(guard,
                new ItemStack(Material.NETHERITE_HELMET),
                new ItemStack(Material.NETHERITE_CHESTPLATE),
                null, null);
        InventoryUtils.clearDropChances(guard);

        EffectUtils.setMaxHealth(guard, 80.0);
        EffectUtils.setAttackDamage(guard, 15.0);
        EffectUtils.setKnockbackResistance(guard, 0.8);
        EffectUtils.setMovementSpeed(guard, 0.2); // Lento por defecto

        Runnable guardTask = new Runnable() {
            int ticks = 0;
            boolean sniffing = false;

            @Override
            public void run() {
                if (!guard.isValid()) { return; }
                ticks++;

                Player target = MobUtils.getNearestPlayer(guard, 20);

                // Mecánica de Olfateo (Cada 5 segundos)
                if (ticks % 100 == 0) {
                    sniffing = true;
                    // Se detiene para oler
                    guard.setVelocity(new org.bukkit.util.Vector(0, 0, 0));
                    guard.getWorld().playSound(guard.getLocation(), Sound.ENTITY_WARDEN_SNIFF, 1.0f, 0.8f);

                    if (target != null) {
                        TeleportUtils.lookAt(guard, target.getEyeLocation());
                        ParticleUtils.drawLine(guard.getEyeLocation(), target.getEyeLocation(), Particle.SCULK_CHARGE_POP, 1);

                        // Si te huele, se enfada y corre
                        guard.getWorld().playSound(guard.getLocation(), Sound.ENTITY_WARDEN_AGITATED, 1.0f, 1.0f);
                        EffectUtils.addPotionEffect(guard, new PotionEffect(PotionEffectType.SPEED, 60, 2)); // Speed III por 3s
                    }
                    sniffing = false;
                }

                if (!sniffing && target != null) {
                    guard.setTarget(target);
                    // Efecto de oscuridad al golpear (debe implementarse en Listener, aquí es visual)
                    ParticleUtils.trailEntity(guard, Particle.ASH);
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














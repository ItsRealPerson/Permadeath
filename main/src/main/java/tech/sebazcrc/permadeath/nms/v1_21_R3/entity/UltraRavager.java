package tech.sebazcrc.permadeath.nms.v1_21_R3.entity;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Ravager;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.EffectUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.MobUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.TeleportUtils;

public class UltraRavager {

    public static Ravager spawn(Location loc, Plugin plugin) {
        Ravager ravager = (Ravager) loc.getWorld().spawnEntity(loc, EntityType.RAVAGER, CreatureSpawnEvent.SpawnReason.CUSTOM);

        ravager.setRemoveWhenFarAway(true);
        ravager.setCustomName("§4§lUltra Ravager");
        ravager.setCustomNameVisible(true);

        // Atributos masivos
        EffectUtils.setMaxHealth(ravager, 300.0);
        EffectUtils.setAttackDamage(ravager, 25.0);
        EffectUtils.setKnockbackResistance(ravager, 1.0);
        EffectUtils.setMovementSpeed(ravager, 0.4);

        // IA Manual
        Runnable ravagerTask = new Runnable() {
            @Override
            public void run() {
                if (ravager.isDead() || !ravager.isValid()) {
                    return;
                }

                Player target = MobUtils.getNearestPlayer(ravager, 40.0);

                if (target != null) {
                    ravager.setTarget(target);

                    if (ravager.getLocation().distanceSquared(target.getLocation()) > 25.0) {
                        TeleportUtils.moveTowards(ravager, target.getLocation(), 0.6, 0.2);
                    }

                    if (ravager.getLocation().distanceSquared(target.getLocation()) < 6.25) {
                        target.damage(10.0, ravager);
                        target.setVelocity(target.getLocation().toVector().subtract(ravager.getLocation().toVector()).normalize().multiply(1.5).setY(0.5));
                        ravager.getWorld().playSound(ravager.getLocation(), Sound.ENTITY_RAVAGER_ATTACK, 1.0f, 0.5f);
                    }
                }
            }
        };

        try {
            ravager.getScheduler().runAtFixedRate(plugin, t -> ravagerTask.run(), null, 20L, 10L);
        } catch (NoSuchMethodError e) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (ravager.isDead() || !ravager.isValid()) { this.cancel(); return; }
                    ravagerTask.run();
                }
            }.runTaskTimer(plugin, 20L, 10L);
        }

        return ravager;
    }
}




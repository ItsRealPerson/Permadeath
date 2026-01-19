package tech.sebazcrc.permadeath.nms.v1_21_R3.entity;

import org.bukkit.plugin.Plugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.EffectUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.MobUtils;

public class PaleParagon {
    public static IronGolem spawn(Location loc, Plugin plugin) {
        IronGolem golem = (IronGolem) loc.getWorld().spawnEntity(loc, EntityType.IRON_GOLEM, CreatureSpawnEvent.SpawnReason.CUSTOM);
        golem.setCustomName("§f§lPale Paragon");
        golem.setPlayerCreated(false);

        EffectUtils.setMaxHealth(golem, 250.0);
        EffectUtils.setKnockbackResistance(golem, 1.0);

        // IA Manual
        Runnable golemTask = new Runnable() {
            int cooldown = 0;
            @Override
            public void run() {
                if (!golem.isValid()) { return; }
                if (cooldown > 0) cooldown--;

                Player target = MobUtils.getNearestPlayer(golem, 25);
                if (target != null) {
                    golem.setTarget(target);

                    // Habilidad: Lanzar Hielo (si está lejos)
                    double dist = golem.getLocation().distanceSquared(target.getLocation());
                    if (dist > 16 && cooldown <= 0) {
                        launchIceBlock(golem, target);
                        cooldown = 100; // 5s
                    }
                }
            }

            private void launchIceBlock(IronGolem source, Player target) {
                Location spawnLoc = source.getLocation().add(0, 2.5, 0);
                FallingBlock ice = source.getWorld().spawnFallingBlock(spawnLoc, Material.PACKED_ICE.createBlockData());
                ice.setDropItem(false);
                ice.setHurtEntities(true); // Hace daño al caer/golpear

                Vector velocity = target.getLocation().toVector().subtract(spawnLoc.toVector()).normalize().multiply(1.2).setY(0.4);
                ice.setVelocity(velocity);

                source.getWorld().playSound(source.getLocation(), Sound.ENTITY_SNOW_GOLEM_SHOOT, 1.5f, 0.1f);
            }
        };

        try {
            golem.getScheduler().runAtFixedRate(plugin, t -> golemTask.run(), null, 20L, 5L);
        } catch (NoSuchMethodError e) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!golem.isValid()) { this.cancel(); return; }
                    golemTask.run();
                }
            }.runTaskTimer(plugin, 20, 5);
        }
        return golem;
    }
}






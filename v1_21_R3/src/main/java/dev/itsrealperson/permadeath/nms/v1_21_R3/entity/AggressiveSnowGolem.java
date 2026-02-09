package dev.itsrealperson.permadeath.nms.v1_21_R3.entity;

import org.bukkit.plugin.Plugin;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowman;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import dev.itsrealperson.permadeath.nms.v1_21_R3.utils.EffectUtils;
import dev.itsrealperson.permadeath.nms.v1_21_R3.utils.MobUtils;
import dev.itsrealperson.permadeath.nms.v1_21_R3.utils.TeleportUtils;

public class AggressiveSnowGolem {
    public static Snowman spawn(Location loc, Plugin plugin) {
        Snowman snowman = (Snowman) loc.getWorld().spawnEntity(loc, EntityType.SNOW_GOLEM, CreatureSpawnEvent.SpawnReason.CUSTOM);
        snowman.setCustomName("§bFrost Turret");
        snowman.setDerp(true); // Sin calabaza, cara graciosa

        EffectUtils.setMaxHealth(snowman, 50.0);

        // Listener para que las bolas de nieve hagan daño
        plugin.getServer().getPluginManager().registerEvents(new org.bukkit.event.Listener() {
            @org.bukkit.event.EventHandler
            public void onHit(org.bukkit.event.entity.EntityDamageByEntityEvent e) {
                if (e.getDamager() instanceof org.bukkit.entity.Snowball ball) {
                    if (ball.getShooter() != null && ball.getShooter().equals(snowman)) {
                        e.setDamage(8.0); // 4 corazones de daño por bola
                    }
                }
            }
        }, plugin);

        // IA para atacar jugadores (normalmente solo atacan monstruos)
        Runnable snowmanTask = new Runnable() {
            @Override
            public void run() {
                if (!snowman.isValid()) { return; }
                Player p = MobUtils.getNearestPlayer(snowman, 20);
                if (p != null) {
                    snowman.setTarget(p);
                    TeleportUtils.lookAt(snowman, p.getEyeLocation());

                    if (Math.random() > 0.5) snowman.launchProjectile(org.bukkit.entity.Snowball.class);
                }
            }
        };

        try {
            snowman.getScheduler().runAtFixedRate(plugin, t -> snowmanTask.run(), null, 10L, 20L);
        } catch (NoSuchMethodError e) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!snowman.isValid()) { this.cancel(); return; }
                    snowmanTask.run();
                }
            }.runTaskTimer(plugin, 10, 20);
        }

        return snowman;
    }
}




package tech.sebazcrc.permadeath.nms.v1_21_R3.entity.minions;

import org.bukkit.plugin.Plugin;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.EffectUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.MobUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.ParticleUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.TeleportUtils;

public class SculkParasite {

    public static Silverfish spawn(Location loc, Plugin plugin) {
        Silverfish fish = (Silverfish) loc.getWorld().spawnEntity(loc, EntityType.SILVERFISH, CreatureSpawnEvent.SpawnReason.CUSTOM);
        fish.setCustomName("§3ParÃ¡sito de Sculk");
        fish.setRemoveWhenFarAway(false);
        fish.setPersistent(true);

        EffectUtils.setMaxHealth(fish, 300.0);
        EffectUtils.setMovementSpeed(fish, 0.6);
        EffectUtils.addPotionEffect(fish, new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 8)); // Fuerza IX
        EffectUtils.addPotionEffect(fish, new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 3)); // Resistencia IV

        Runnable fishTask = new Runnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (!fish.isValid()) { return; }
                ticks++;

                if (ticks % 30 == 0) {
                    Player target = MobUtils.getNearestPlayer(fish, 25);
                    if (target != null) {
                        fish.getWorld().playSound(fish.getLocation(), Sound.ENTITY_WARDEN_SNIFF, 1.0f, 2.0f);
                        fish.setTarget(target);
                    }
                }

                if (fish.getTarget() != null) {
                    Player target = (Player) fish.getTarget();
                    TeleportUtils.lookAt(fish, target.getLocation());
                    TeleportUtils.moveTowards(fish, target.getLocation(), 0.6, 0.3);
                    
                    if (fish.getLocation().distanceSquared(target.getLocation()) < 2.0) {
                        target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 3));
                        target.damage(10.0, fish); // DaÃ±o extra por contacto
                    }

                    if (ticks % 10 == 0) {
                        fish.getWorld().spawnParticle(Particle.SCULK_CHARGE_POP, fish.getLocation(), 1, 0.1, 0.1, 0.1, 0.05);
                    }
                }
            }
        };

        try {
            fish.getScheduler().runAtFixedRate(plugin, t -> fishTask.run(), null, 10L, 5L);
        } catch (NoSuchMethodError e) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!fish.isValid()) { this.cancel(); return; }
                    fishTask.run();
                }
            }.runTaskTimer(plugin, 10, 5);
        }

        return fish;
    }
}




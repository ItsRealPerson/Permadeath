package tech.sebazcrc.permadeath.nms.v1_21_R3.entity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.EffectUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.MobUtils;

import java.util.Random;

public class CustomGiant implements Listener {

    private static NamespacedKey giantKey;
    private static volatile boolean listenerRegistered = false;

    public static void init(Plugin plugin) {
        if (!listenerRegistered) {
            synchronized (CustomGiant.class) {
                if (!listenerRegistered) {
                    giantKey = new NamespacedKey(plugin, "custom_giant");
                    Bukkit.getPluginManager().registerEvents(new CustomGiant(), plugin);
                    listenerRegistered = true;
                }
            }
        }
    }

    public static Giant spawn(Location loc, Plugin plugin) {
        if (!listenerRegistered) {
            init(plugin);
        }

        Giant giant = (Giant) loc.getWorld().spawnEntity(loc, EntityType.GIANT, CreatureSpawnEvent.SpawnReason.CUSTOM);

        giant.setCustomName(null);
        giant.setCustomNameVisible(false);
        giant.setAI(true);
        if (giantKey != null) {
            giant.getPersistentDataContainer().set(giantKey, PersistentDataType.BYTE, (byte) 1);
        }

        EffectUtils.setMaxHealth(giant, 720.0);
        EffectUtils.setAttackDamage(giant, 2000.0);

        // IA Manual
        Runnable giantTask = new Runnable() {
            private int strollTicks = 0;

            @Override
            public void run() {
                if (giant.isDead() || !giant.isValid()) {
                    return;
                }

                Player target = MobUtils.getNearestPlayer(giant, 35.0);

                if (target != null) {
                    giant.getPathfinder().moveTo(target.getLocation(), 1.0);

                    if (giant.getLocation().distanceSquared(target.getLocation()) < 25.0) {
                        target.damage(500.0, giant);
                        giant.getWorld().playSound(giant.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1.0f, 0.5f);
                        target.setVelocity(target.getLocation().toVector().subtract(giant.getLocation().toVector()).normalize().multiply(1.5).setY(0.8));
                    }
                } else {
                    if (--strollTicks <= 0) {
                        Location randomLoc = giant.getLocation().add(new Random().nextInt(20) - 10, 0, new Random().nextInt(20) - 10);
                        giant.getPathfinder().moveTo(randomLoc, 1.0);
                        strollTicks = 100 + new Random().nextInt(100);
                        
                        if (new Random().nextInt(10) < 3) {
                            giant.getWorld().playSound(giant.getLocation(), Sound.ENTITY_ZOMBIE_AMBIENT, 1.5f, 0.5f);
                        }
                    }
                }
            }
        };

        // Soporte Folia / Paper
        try {
            giant.getScheduler().runAtFixedRate(plugin, t -> giantTask.run(), null, 20L, 10L);
        } catch (NoSuchMethodError e) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (giant.isDead() || !giant.isValid()) { this.cancel(); return; }
                    giantTask.run();
                }
            }.runTaskTimer(plugin, 20L, 10L);
        }

        return giant;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntityType() == EntityType.GIANT && isCustomGiant(e.getEntity())) {
            e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.ENTITY_ZOMBIE_HURT, 1.5f, 0.5f);
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if (e.getEntityType() == EntityType.GIANT && isCustomGiant(e.getEntity())) {
            e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.ENTITY_ZOMBIE_DEATH, 1.5f, 0.5f);
        }
    }

    private static boolean isCustomGiant(org.bukkit.entity.Entity entity) {
        if (giantKey != null && entity.getPersistentDataContainer().has(giantKey, PersistentDataType.BYTE)) {
            return true;
        }
        return false;
    }
}

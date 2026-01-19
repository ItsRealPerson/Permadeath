package tech.sebazcrc.permadeath.nms.v1_21_R3.entity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.EffectUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.MobUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.TeleportUtils;

import java.util.Random;

public class CustomCreeper implements Listener {

    private static NamespacedKey typeKey;
    private static final Random random = new Random();

    public enum CreeperType {
        ENDER, QUANTUM, ENDER_QUANTUM
    }

    public static void init(Plugin plugin) {
        typeKey = new NamespacedKey(plugin, "creeper_type");
        Bukkit.getPluginManager().registerEvents(new CustomCreeper(), plugin);
    }

    public static Creeper spawn(Location loc, Plugin plugin, CreeperType type) {
        if (typeKey == null) init(plugin);

        // En Folia, spawnEntity es seguro si estamos en el hilo de la región, 
        // lo cual es cierto para el Region Scheduler Thread #0 del log.
        Creeper creeper = (Creeper) loc.getWorld().spawnEntity(loc, EntityType.CREEPER, CreatureSpawnEvent.SpawnReason.CUSTOM);

        creeper.getPersistentDataContainer().set(typeKey, PersistentDataType.STRING, type.name());

        if (type == CreeperType.ENDER_QUANTUM) {
            creeper.setCustomName("§6§lEnder Quantum Creeper");
            creeper.setExplosionRadius(8);
            EffectUtils.setMaxHealth(creeper, 100.0);
            creeper.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
        } else if (type == CreeperType.QUANTUM) {
            creeper.setCustomName("§6§lQuantum Creeper");
            creeper.setExplosionRadius(8);
            EffectUtils.setMaxHealth(creeper, 100.0);
        } else {
            creeper.setCustomName("§d§lEnder Creeper");
            creeper.setExplosionRadius(6);
            EffectUtils.setMaxHealth(creeper, 50.0);
        }

        creeper.setCustomNameVisible(true);
        creeper.setPowered(true);
        creeper.setMaxFuseTicks(20);
        creeper.setRemoveWhenFarAway(true);

        EffectUtils.setMovementSpeed(creeper, 0.35);
        EffectUtils.setFollowRange(creeper, 64.0);

        // IA Manual usando EntityScheduler (Folia safe)
        try {
            creeper.getScheduler().runAtFixedRate(plugin, t -> {
                if (creeper.isDead() || !creeper.isValid()) {
                    t.cancel();
                    return;
                }

                Player target = MobUtils.getNearestPlayer(creeper, 25.0);
                if (target != null) {
                    TeleportUtils.lookAt(creeper, target.getLocation());
                    TeleportUtils.moveTowards(creeper, target.getLocation(), 0.4, 0.2);

                    if (creeper.getLocation().distanceSquared(target.getLocation()) < 9.0) {
                        creeper.ignite();
                    }
                }
            }, null, 20L, 5L);
        } catch (NoSuchMethodError e) {
            // Fallback para Spigot/Paper normal
            new org.bukkit.scheduler.BukkitRunnable() {
                @Override
                public void run() {
                    if (creeper.isDead() || !creeper.isValid()) {
                        this.cancel();
                        return;
                    }
                    Player target = MobUtils.getNearestPlayer(creeper, 25.0);
                    if (target != null) {
                        TeleportUtils.lookAt(creeper, target.getLocation());
                        TeleportUtils.moveTowards(creeper, target.getLocation(), 0.4, 0.2);
                        if (creeper.getLocation().distanceSquared(target.getLocation()) < 9.0) {
                            creeper.ignite();
                        }
                    }
                }
            }.runTaskTimer(plugin, 20L, 5L);
        }

        return creeper;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Creeper creeper)) return;
        if (typeKey == null) return;

        String storedType = creeper.getPersistentDataContainer().get(typeKey, PersistentDataType.STRING);
        if (storedType == null) return;

        if (CreeperType.ENDER.name().equals(storedType) || CreeperType.ENDER_QUANTUM.name().equals(storedType)) {
            if (random.nextInt(10) < 4) {
                teleportRandomly(creeper);
            }
        }
    }

    private static void teleportRandomly(Creeper creeper) {
        double d0 = creeper.getLocation().getX() + (random.nextDouble() - 0.5) * 64.0;
        double d1 = creeper.getLocation().getY() + (double)(random.nextInt(64) - 32);
        double d2 = creeper.getLocation().getZ() + (random.nextDouble() - 0.5) * 64.0;
        
        Location loc = new Location(creeper.getWorld(), d0, d1, d2);
        if (loc.getBlock().getType().isAir()) {
            creeper.teleportAsync(loc).thenAccept(success -> {
                if (success) {
                    creeper.getWorld().playSound(creeper.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
                }
            });
        }
    }
}

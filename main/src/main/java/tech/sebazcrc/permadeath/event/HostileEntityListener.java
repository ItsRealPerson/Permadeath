package tech.sebazcrc.permadeath.event;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import tech.sebazcrc.permadeath.Main;
import tech.sebazcrc.permadeath.util.Utils;
import tech.sebazcrc.permadeath.util.VersionManager;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.MobUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.TeleportUtils;

public class HostileEntityListener implements Listener {
    private final Main instance;
    private final NamespacedKey hostileKey;

    public HostileEntityListener(Main instance) {
        this.instance = instance;
        this.hostileKey = new NamespacedKey(instance, "hostile_ticking");
        initialize();
    }

    public void initialize() {
        if (instance.getDay() >= 20) {
            for (World w : Bukkit.getWorlds()) {
                for (LivingEntity entity : w.getLivingEntities()) {
                    EntityType type = entity.getType();

                    if (!Utils.isHostileMob(type) && type != EntityType.ENDERMAN && type != EntityType.PLAYER) {
                        if (Main.isRunningFolia()) {
                            entity.getScheduler().run(instance, t -> injectHostileBehavior(entity), null);
                        } else {
                            injectHostileBehavior(entity);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {
        if (e.isCancelled()) return;

        if (instance.getDay() >= 20 && !Utils.isHostileMob(e.getEntityType()) && e.getEntityType() != EntityType.ARMOR_STAND && e.getEntityType() != EntityType.ENDERMAN && e.getEntityType() != EntityType.PLAYER) {
            injectHostileBehavior(e.getEntity());
        }
    }

    private void injectHostileBehavior(LivingEntity entity) {
        // Usamos Metadata (temporal) para asegurar que la tarea se inicie en cada reinicio del servidor
        if (entity.hasMetadata("pdc_hostile_active")) return;
        entity.setMetadata("pdc_hostile_active", new org.bukkit.metadata.FixedMetadataValue(instance, true));
        
        // Marcamos persistente solo para identificar que DEBE ser hostil, aunque la lógica activa sea temporal
        if (!entity.getPersistentDataContainer().has(hostileKey, PersistentDataType.BYTE)) {
            entity.getPersistentDataContainer().set(hostileKey, PersistentDataType.BYTE, (byte) 1);
        }
        
        instance.getNmsAccessor().injectHostilePathfinders(entity);
        if (entity.getAttribute(Attribute.ATTACK_DAMAGE) == null) {
            instance.getNmsAccessor().registerAttribute(Attribute.ATTACK_DAMAGE, 8.0D, entity);
        }

        // Tarea de IA manual para asegurar agresividad
        
        Runnable aiTask = () -> {
            if (entity.isDead() || !entity.isValid()) return;
            
            Player target = MobUtils.getNearestPlayer(entity, 20.0);
            
            // Si el objetivo está muy lejos (> 30 bloques) o desconectado, dejar de seguir
            if (target == null || target.getLocation().distanceSquared(entity.getLocation()) > 900) {
                if (entity instanceof Mob mob) {
                    mob.setTarget(null);
                }
                return;
            }

            if (entity instanceof Mob mob) {
                mob.setTarget(target);
            }
            
            // Forzamos movimiento y ataque para TODOS los mobs pacíficos que ahora son hostiles
            // Esto compensa la falta de MeleeAttackGoal y atributos de daño en animales
            TeleportUtils.lookAt(entity, target.getLocation());
            
            // Usar navegación si es posible, sino empuje simple
            if (entity instanceof Mob) {
                ((Mob) entity).getPathfinder().moveTo(target, 1.25);
            } else {
                TeleportUtils.moveTowards(entity, target.getLocation(), 0.35, 0.2);
            }
            
            if (entity.getLocation().distanceSquared(target.getLocation()) < 2.5) {
                // Usamos damage directo en lugar de attack() porque attack() requiere atributos que estos mobs no tienen
                target.damage(8.0, entity);
                // Efecto visual de golpe
                entity.swingMainHand();
            }
        };

        if (Main.isRunningFolia()) {
            entity.getScheduler().runAtFixedRate(instance, t -> aiTask.run(), null, 20L, 10L);
        } else {
            new org.bukkit.scheduler.BukkitRunnable() {
                @Override
                public void run() {
                    if (entity.isDead() || !entity.isValid()) { this.cancel(); return; }
                    aiTask.run();
                }
            }.runTaskTimer(instance, 20L, 10L);
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        if (instance.getDay() < 20 || e.isNewChunk()) return;

        for (Entity entity : e.getChunk().getEntities()) {
            if (!entity.isValid() || entity.isDead()) continue;
            if (!(entity instanceof LivingEntity) || entity instanceof Player) continue;

            if (entity instanceof Villager && instance.getDay() >= 60) {
                entity.getWorld().spawn(entity.getLocation(), Vindicator.class);
                entity.remove();
                return;
            }

            injectHostileBehavior((LivingEntity) entity);
        }
    }
}


















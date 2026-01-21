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
        if (entity.getPersistentDataContainer().has(hostileKey, PersistentDataType.BYTE)) return;
        
        instance.getNmsAccessor().injectHostilePathfinders(entity);
        if (entity.getAttribute(Attribute.ATTACK_DAMAGE) == null) {
            instance.getNmsAccessor().registerAttribute(Attribute.ATTACK_DAMAGE, 8.0D, entity);
        }

        // Tarea de IA manual para asegurar agresividad
        entity.getPersistentDataContainer().set(hostileKey, PersistentDataType.BYTE, (byte) 1);
        
        Runnable aiTask = () -> {
            if (entity.isDead() || !entity.isValid()) return;
            
            Player target = MobUtils.getNearestPlayer(entity, 20.0);
            if (target != null) {
                if (entity instanceof Mob mob) {
                    mob.setTarget(target);
                }
                
                // Si es un mob que normalmente no es hostil (como Piglins zombis neutrales), forzamos movimiento
                if (entity instanceof PigZombie || entity instanceof Bee || entity instanceof IronGolem) {
                    TeleportUtils.lookAt(entity, target.getLocation());
                    TeleportUtils.moveTowards(entity, target.getLocation(), 0.35, 0.2);
                    
                    if (entity.getLocation().distanceSquared(target.getLocation()) < 4.0) {
                        entity.attack(target);
                    }
                }
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


















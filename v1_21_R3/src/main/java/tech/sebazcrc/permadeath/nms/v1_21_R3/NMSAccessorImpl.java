package tech.sebazcrc.permadeath.nms.v1_21_R3;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import tech.sebazcrc.permadeath.api.interfaces.NMSAccessor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;
import org.bukkit.Bukkit;

public class NMSAccessorImpl implements NMSAccessor {

    private static Class<?> craftEntityClass;
    private static Class<?> entityInsentientClass;
    private static Class<?> goalSelectorClass;
    private static Class<?> goalClass;
    private static Class<?> meleeAttackGoalClass;
    private static Class<?> nearestAttackableTargetGoalClass;
    private static Class<?> nmsPlayerClass;
    private static Class<?> pathfinderMobClass;

    public NMSAccessorImpl() {
        try {
            String version = Bukkit.getServer().getClass().getPackage().getName();
            String nmsPackage = "net.minecraft.world.entity";
            
            craftEntityClass = Class.forName("org.bukkit.craftbukkit.entity.CraftEntity");
            entityInsentientClass = Class.forName(nmsPackage + ".Mob");
            pathfinderMobClass = Class.forName(nmsPackage + ".PathfinderMob");
            goalSelectorClass = Class.forName(nmsPackage + ".ai.goal.GoalSelector");
            goalClass = Class.forName(nmsPackage + ".ai.goal.Goal");
            meleeAttackGoalClass = Class.forName(nmsPackage + ".ai.goal.MeleeAttackGoal");
            nearestAttackableTargetGoalClass = Class.forName(nmsPackage + ".ai.goal.target.NearestAttackableTargetGoal");
            nmsPlayerClass = Class.forName("net.minecraft.world.entity.player.Player");
        } catch (Exception e) {
            // Fallback para versiones con paquete versionado si es necesario
            try {
                String v = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
                craftEntityClass = Class.forName("org.bukkit.craftbukkit." + v + ".entity.CraftEntity");
            } catch (Exception ignored) {}
        }
    }

    @Override
    public void setMaxHealth(LivingEntity entity, Double d, boolean setHealth) {
        org.bukkit.attribute.AttributeInstance attr = entity.getAttribute(Attribute.MAX_HEALTH);
        if (attr != null) {
            attr.setBaseValue(d);
            if (setHealth) {
                entity.setHealth(d);
            }
        }
    }

    @Override
    public double getMaxHealth(LivingEntity entity) {
        org.bukkit.attribute.AttributeInstance attr = entity.getAttribute(Attribute.MAX_HEALTH);
        return attr != null ? attr.getValue() : 20.0;
    }

    @Override
    public void registerAttribute(Attribute a, double value, LivingEntity who) {
        org.bukkit.attribute.AttributeInstance attr = who.getAttribute(a);
        if (attr != null) {
            attr.setBaseValue(value);
        }
    }

    @Override
    public void registerHostileMobs() {
    }

    @Override
    public void injectHostilePathfinders(LivingEntity entity) {
        try {
            Object nmsEntity = craftEntityClass.getMethod("getHandle").invoke(entity);
            if (entityInsentientClass.isInstance(nmsEntity)) {
                
                // En 1.21.x (Mojang mappings), los campos suelen llamarse goalSelector y targetSelector
                Field goalSelectorField = entityInsentientClass.getField("goalSelector");
                Field targetSelectorField = entityInsentientClass.getField("targetSelector");
                
                Object goalSelector = goalSelectorField.get(nmsEntity);
                Object targetSelector = targetSelectorField.get(nmsEntity);

                // Solo inyectar MeleeAttackGoal si tiene daño de ataque
                if (pathfinderMobClass.isInstance(nmsEntity) && entity.getAttribute(Attribute.ATTACK_DAMAGE) != null) {
                    // Añadir MeleeAttackGoal(mob, speed, pauseWhenMobIdle)
                    Object meleeGoal = meleeAttackGoalClass.getConstructor(pathfinderMobClass, double.class, boolean.class)
                            .newInstance(nmsEntity, 1.0D, true);
                    
                    Method addGoal = goalSelectorClass.getMethod("addGoal", int.class, goalClass);
                    addGoal.invoke(goalSelector, 2, meleeGoal);
                }

                // Añadir NearestAttackableTargetGoal(mob, targetClass, checkVisibility)
                Object targetGoal = nearestAttackableTargetGoalClass.getConstructor(entityInsentientClass, Class.class, boolean.class)
                        .newInstance(nmsEntity, nmsPlayerClass, true);
                
                Method addTargetGoal = goalSelectorClass.getMethod("addGoal", int.class, goalClass);
                addTargetGoal.invoke(targetSelector, 2, targetGoal);
            }
        } catch (Exception e) {
            // Si falla la reflexión, la IA manual que implementamos antes servirá de respaldo
        }
    }

    @Override
    public void drown(Player p, double amount) {
        p.damage(amount);
    }

    @Override
    public void clearEntityPathfinders(Object goalSelector, Object targetSelector) {
        try {
            Method removeAllGoals = goalSelectorClass.getMethod("removeAllGoals", java.util.function.Predicate.class);
            removeAllGoals.invoke(goalSelector, (java.util.function.Predicate<Object>) o -> true);
            removeAllGoals.invoke(targetSelector, (java.util.function.Predicate<Object>) o -> true);
        } catch (Exception ignored) {}
    }
}

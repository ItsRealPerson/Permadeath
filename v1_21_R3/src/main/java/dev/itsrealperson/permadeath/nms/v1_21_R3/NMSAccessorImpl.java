package dev.itsrealperson.permadeath.nms.v1_21_R3;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import dev.itsrealperson.permadeath.api.interfaces.NMSAccessor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public class NMSAccessorImpl implements NMSAccessor {

    private Class<?> craftEntityClass;
    private Class<?> entityInsentientClass;
    private Class<?> goalSelectorClass;
    private Class<?> goalClass;
    private Class<?> meleeAttackGoalClass;
    private Class<?> nearestAttackableTargetGoalClass;
    private Class<?> nmsPlayerClass;
    private Class<?> pathfinderMobClass;

    public NMSAccessorImpl() {
        try {
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
            Bukkit.getLogger().severe("[Permadeath-NMS] Error al inicializar Reflexión para 1.21.8: " + e.getMessage());
        }
    }

    @Override
    public dev.itsrealperson.permadeath.api.entity.PermadeathEntity getCustomEntity(org.bukkit.entity.Entity entity) {
        return null;
    }

    @Override
    public void setMaxHealth(LivingEntity entity, Double d, boolean setHealth) {
        org.bukkit.attribute.AttributeInstance attr = entity.getAttribute(Attribute.MAX_HEALTH);
        if (attr != null) {
            attr.setBaseValue(d);
            if (setHealth) {
                if (d > 1024.0) {
                    try {
                        Object nmsEntity = craftEntityClass.getMethod("getHandle").invoke(entity);
                        Method setHealthMethod = nmsEntity.getClass().getMethod("setHealth", float.class);
                        setHealthMethod.invoke(nmsEntity, d.floatValue());
                    } catch (Exception e) {
                        entity.setHealth(Math.min(d, 1024.0));
                    }
                } else {
                    entity.setHealth(d);
                }
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
        if (attr != null) attr.setBaseValue(value);
    }

    @Override
    public void registerHostileMobs() {}

    @Override
    public void injectHostilePathfinders(LivingEntity entity) {
        try {
            Object nmsEntity = craftEntityClass.getMethod("getHandle").invoke(entity);
            if (entityInsentientClass.isInstance(nmsEntity)) {
                Field goalSelectorField = entityInsentientClass.getDeclaredField("goalSelector");
                Field targetSelectorField = entityInsentientClass.getDeclaredField("targetSelector");
                goalSelectorField.setAccessible(true);
                targetSelectorField.setAccessible(true);
                
                Object goalSelector = goalSelectorField.get(nmsEntity);
                Object targetSelector = targetSelectorField.get(nmsEntity);

                if (pathfinderMobClass.isInstance(nmsEntity) && entity.getAttribute(Attribute.ATTACK_DAMAGE) != null) {
                    Object meleeGoal = meleeAttackGoalClass.getConstructor(pathfinderMobClass, double.class, boolean.class)
                            .newInstance(nmsEntity, 1.0D, true);
                    
                    Method addGoal = goalSelectorClass.getMethod("addGoal", int.class, goalClass);
                    addGoal.invoke(goalSelector, 2, meleeGoal);
                }

                Object targetGoal = nearestAttackableTargetGoalClass.getConstructor(entityInsentientClass, Class.class, boolean.class)
                        .newInstance(nmsEntity, nmsPlayerClass, true);
                
                Method addTargetGoal = goalSelectorClass.getMethod("addGoal", int.class, goalClass);
                addTargetGoal.invoke(targetSelector, 2, targetGoal);
            }
        } catch (Exception ignored) {}
    }

    @Override
    public void drown(Player p, double amount) { p.damage(amount); }

    @Override
    public void clearEntityPathfinders(LivingEntity entity) {
        try {
            Object nmsEntity = craftEntityClass.getMethod("getHandle").invoke(entity);
            Field goalSelectorField = entityInsentientClass.getDeclaredField("goalSelector");
            Field targetSelectorField = entityInsentientClass.getDeclaredField("targetSelector");
            goalSelectorField.setAccessible(true);
            targetSelectorField.setAccessible(true);
            
            Object goalSelector = goalSelectorField.get(nmsEntity);
            Object targetSelector = targetSelectorField.get(nmsEntity);
            clearEntityPathfinders(goalSelector, targetSelector);
        } catch (Exception ignored) {}
    }

    // Método legacy restaurado para compatibilidad interna
    public void clearEntityPathfinders(Object goalSelector, Object targetSelector) {
        try {
            Method removeAllGoals = goalSelectorClass.getMethod("removeAllGoals", java.util.function.Predicate.class);
            removeAllGoals.invoke(goalSelector, (java.util.function.Predicate<Object>) o -> true);
            removeAllGoals.invoke(targetSelector, (java.util.function.Predicate<Object>) o -> true);
        } catch (Exception ignored) {}
    }

    @Override
    public void addGoalSelector(LivingEntity entity, int priority, String goalId, Map<String, String> args) {
        try {
            Object nmsEntity = craftEntityClass.getMethod("getHandle").invoke(entity);
            Field goalSelectorField = entityInsentientClass.getDeclaredField("goalSelector");
            goalSelectorField.setAccessible(true);
            Object goalSelector = goalSelectorField.get(nmsEntity);
            Object goal = null;

            if (goalId.equalsIgnoreCase("meleeattack") || goalId.equalsIgnoreCase("melee_attack")) {
                var constructor = meleeAttackGoalClass.getDeclaredConstructor(pathfinderMobClass, double.class, boolean.class);
                constructor.setAccessible(true);
                goal = constructor.newInstance(nmsEntity, Double.parseDouble(args.getOrDefault("speed", "1.0")), true);
            } else if (goalId.equalsIgnoreCase("float")) {
                Class<?> floatGoalClass = Class.forName("net.minecraft.world.entity.ai.goal.FloatGoal");
                var constructor = floatGoalClass.getDeclaredConstructor(entityInsentientClass);
                constructor.setAccessible(true);
                goal = constructor.newInstance(nmsEntity);
            }

            if (goal != null) {
                Method addGoal = goalSelectorClass.getMethod("addGoal", int.class, goalClass);
                addGoal.invoke(goalSelector, priority, goal);
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("[Permadeath-NMS] Error al añadir Goal " + goalId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void addTargetSelector(LivingEntity entity, int priority, String targetId, Map<String, String> args) {
        try {
            Object nmsEntity = craftEntityClass.getMethod("getHandle").invoke(entity);
            Field targetSelectorField = entityInsentientClass.getDeclaredField("targetSelector");
            targetSelectorField.setAccessible(true);
            Object targetSelector = targetSelectorField.get(nmsEntity);
            Object goal = null;

            if (targetId.equalsIgnoreCase("players") || targetId.equalsIgnoreCase("nearest_player") || targetId.equalsIgnoreCase("nearestplayer")) {
                var constructor = nearestAttackableTargetGoalClass.getDeclaredConstructor(entityInsentientClass, Class.class, boolean.class);
                constructor.setAccessible(true);
                goal = constructor.newInstance(nmsEntity, nmsPlayerClass, true);
            } else if (targetId.equalsIgnoreCase("wallhack")) {
                var constructor = nearestAttackableTargetGoalClass.getDeclaredConstructor(entityInsentientClass, Class.class, boolean.class);
                constructor.setAccessible(true);
                goal = constructor.newInstance(nmsEntity, nmsPlayerClass, false);
            } else if (targetId.equalsIgnoreCase("hurtbytarget") || targetId.equalsIgnoreCase("hurt_by_target")) {
                Class<?> hurtByTargetGoalClass = Class.forName("net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal");
                var constructor = hurtByTargetGoalClass.getDeclaredConstructor(pathfinderMobClass, Class[].class);
                constructor.setAccessible(true);
                goal = constructor.newInstance(nmsEntity, new Class[0]);
            }

            if (goal != null) {
                Method addGoal = goalSelectorClass.getMethod("addGoal", int.class, goalClass);
                addGoal.invoke(targetSelector, priority, goal);
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("[Permadeath-NMS] Error al añadir Target " + targetId + ": " + e.getMessage());
        }
    }
}

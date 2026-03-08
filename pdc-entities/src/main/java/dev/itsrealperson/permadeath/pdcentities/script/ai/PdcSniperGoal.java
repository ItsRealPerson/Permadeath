package dev.itsrealperson.permadeath.pdcentities.script.ai;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import java.lang.reflect.Method;
import java.util.EnumSet;
import org.jetbrains.annotations.NotNull;

public class PdcSniperGoal implements Goal<Mob> {
    private final Mob mob;
    private final int interval;
    private int ticksUntilNextAttack = 0;
    private final GoalKey<Mob> key;
    private Method performRangedAttackMethod;

    public PdcSniperGoal(Mob mob, int interval) {
        this.mob = mob;
        this.interval = interval;
        this.key = GoalKey.of(Mob.class, new NamespacedKey("permadeath", "sniper_" + mob.getUniqueId()));
        
        try {
            Class<?> nmsMobClass = Class.forName("net.minecraft.world.entity.monster.RangedAttackMob");
            performRangedAttackMethod = nmsMobClass.getMethod("performRangedAttack", Class.forName("net.minecraft.world.entity.LivingEntity"), float.class);
        } catch (Exception ignored) {}
    }

    @Override
    public boolean shouldActivate() {
        return mob.getTarget() != null && performRangedAttackMethod != null;
    }

    @Override
    public void start() {
        ticksUntilNextAttack = interval;
        dev.itsrealperson.permadeath.pdcentities.PdcEntitiesAddon.debug("§d[AI] Sniper iniciado para " + mob.getName());
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null) return;

        // Intentar rotar al mob hacia el objetivo usando NMS
        try {
            Object nmsMob = mob.getClass().getMethod("getHandle").invoke(mob);
            Object lookControl = nmsMob.getClass().getMethod("getLookControl").invoke(nmsMob);
            Object nmsTarget = target.getClass().getMethod("getHandle").invoke(target);
            lookControl.getClass().getMethod("setLookAt", nmsTarget.getClass().getSuperclass(), float.class, float.class)
                .invoke(lookControl, nmsTarget, 30.0F, 30.0F);
        } catch (Exception ignored) {}
        
        if (--ticksUntilNextAttack <= 0) {
            try {
                Object nmsMob = mob.getClass().getMethod("getHandle").invoke(mob);
                Object nmsTarget = target.getClass().getMethod("getHandle").invoke(target);
                performRangedAttackMethod.invoke(nmsMob, nmsTarget, 1.5F);
            } catch (Exception ignored) {}
            ticksUntilNextAttack = interval;
        }
    }

    @Override
    public @NotNull GoalKey<Mob> getKey() { return key; }

    @Override
    public @NotNull EnumSet<GoalType> getTypes() { return EnumSet.of(GoalType.LOOK); }
}

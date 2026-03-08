package dev.itsrealperson.permadeath.pdcentities.script.ai;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import java.util.EnumSet;
import java.util.Random;
import org.jetbrains.annotations.NotNull;

public class PdcStrafeGoal implements Goal<Mob> {
    private final Mob mob;
    private final double speed;
    private int strafeDir = 1;
    private int strafeTicks = 0;
    private final Random random = new Random();
    private final GoalKey<Mob> key;

    public PdcStrafeGoal(Mob mob, double speed) {
        this.mob = mob;
        this.speed = speed;
        this.key = GoalKey.of(Mob.class, new NamespacedKey("permadeath", "strafe_" + mob.getUniqueId()));
    }

    @Override
    public boolean shouldActivate() {
        return mob.getTarget() != null && mob.getLocation().distanceSquared(mob.getTarget().getLocation()) < 400.0D;
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null) return;

        strafeTicks++;
        if (strafeTicks >= 40) {
            if (random.nextBoolean()) strafeDir *= -1;
            strafeTicks = 0;
        }

        double distSqr = mob.getLocation().distanceSquared(target.getLocation());
        if (distSqr < 64.0D) {
            mob.getPathfinder().moveTo(mob.getLocation().add(mob.getLocation().toVector().subtract(target.getLocation().toVector())), speed);
        } else {
            // Strafe manual vía velocidad lateral
            org.bukkit.util.Vector lateral = target.getLocation().getDirection().crossProduct(new org.bukkit.util.Vector(0, 1, 0)).normalize().multiply(speed * strafeDir * 0.2);
            mob.setVelocity(mob.getVelocity().add(lateral));
        }
    }

    @Override
    public @NotNull GoalKey<Mob> getKey() { return key; }

    @Override
    public @NotNull EnumSet<GoalType> getTypes() { return EnumSet.of(GoalType.MOVE, GoalType.LOOK); }
}

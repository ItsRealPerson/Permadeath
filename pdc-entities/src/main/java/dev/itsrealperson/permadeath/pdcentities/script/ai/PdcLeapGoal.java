package dev.itsrealperson.permadeath.pdcentities.script.ai;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.util.Vector;
import java.util.EnumSet;
import java.util.Random;
import org.jetbrains.annotations.NotNull;

public class PdcLeapGoal implements Goal<Mob> {
    private final Mob mob;
    private final float speed;
    private final Random random = new Random();
    private final GoalKey<Mob> key;

    public PdcLeapGoal(Mob mob, float speed) {
        this.mob = mob;
        this.speed = speed;
        this.key = GoalKey.of(Mob.class, new NamespacedKey("permadeath", "leap_" + mob.getUniqueId()));
    }

    @Override
    public boolean shouldActivate() {
        LivingEntity target = mob.getTarget();
        if (target == null) return false;
        double distSqr = mob.getLocation().distanceSquared(target.getLocation());
        return distSqr >= 36.0D && distSqr <= 144.0D && mob.isOnGround() && random.nextInt(10) == 0;
    }

    @Override
    public void start() {
        LivingEntity target = mob.getTarget();
        if (target == null) return;

        Vector dir = target.getLocation().toVector().subtract(mob.getLocation().toVector());
        double dY = Math.min(target.getLocation().getY() - mob.getLocation().getY(), 1.0);
        
        Vector v = dir.normalize().multiply(speed);
        v.setY(0.4 + (dY * 0.1));
        
        mob.setVelocity(v);
    }

    @Override
    public @NotNull GoalKey<Mob> getKey() { return key; }

    @Override
    public @NotNull EnumSet<GoalType> getTypes() { return EnumSet.of(GoalType.MOVE, GoalType.JUMP); }
}

package dev.itsrealperson.permadeath.pdcentities.script.ai;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import java.util.EnumSet;
import org.jetbrains.annotations.NotNull;

public class PdcStalkGoal implements Goal<Mob> {
    private final Mob mob;
    private final double stalkDistance;
    private final double speed;
    private final GoalKey<Mob> key;

    public PdcStalkGoal(Mob mob, double distance, double speed) {
        this.mob = mob;
        this.stalkDistance = distance;
        this.speed = speed;
        this.key = GoalKey.of(Mob.class, new NamespacedKey("permadeath", "stalk_" + mob.getUniqueId()));
    }

    @Override
    public boolean shouldActivate() {
        return mob.getTarget() != null;
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null) return;

        double distSqr = mob.getLocation().distanceSquared(target.getLocation());
        
        if (distSqr > (stalkDistance * stalkDistance)) {
            mob.getPathfinder().moveTo(target, speed);
        } else if (distSqr < ((stalkDistance - 2) * (stalkDistance - 2))) {
            mob.getPathfinder().moveTo(mob.getLocation().add(mob.getLocation().toVector().subtract(target.getLocation().toVector())), speed);
        } else {
            mob.getPathfinder().stopPathfinding();
        }
    }

    @Override
    public @NotNull GoalKey<Mob> getKey() { return key; }

    @Override
    public @NotNull EnumSet<GoalType> getTypes() { return EnumSet.of(GoalType.MOVE, GoalType.LOOK); }
}

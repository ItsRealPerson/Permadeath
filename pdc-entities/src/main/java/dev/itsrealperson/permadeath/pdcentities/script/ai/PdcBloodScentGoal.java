package dev.itsrealperson.permadeath.pdcentities.script.ai;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import java.util.EnumSet;
import java.util.Comparator;
import org.jetbrains.annotations.NotNull;

public class PdcBloodScentGoal implements Goal<Mob> {
    private final Mob mob;
    private final double range;
    private final GoalKey<Mob> key;

    public PdcBloodScentGoal(Mob mob, double range) {
        this.mob = mob;
        this.range = range;
        this.key = GoalKey.of(Mob.class, new NamespacedKey("permadeath", "blood_" + mob.getUniqueId()));
    }

    @Override
    public boolean shouldActivate() {
        if (mob.getTarget() != null) return false;

        boolean found = mob.getWorld().getPlayers().stream()
                .filter(p -> p.getLocation().distanceSquared(mob.getLocation()) < (range * range))
                .anyMatch(p -> p.getHealth() < 10.0);
        
        if (found) {
            dev.itsrealperson.permadeath.pdcentities.PdcEntitiesAddon.debug("§d[AI] BloodScent detectado para " + mob.getType());
        }
        
        return found;
    }

    @Override
    public void start() {
        Player nearestBlood = mob.getWorld().getPlayers().stream()
                .filter(p -> p.getLocation().distanceSquared(mob.getLocation()) < (range * range))
                .filter(p -> p.getHealth() < 10.0)
                .min(Comparator.comparingDouble(p -> p.getLocation().distanceSquared(mob.getLocation())))
                .orElse(null);

        if (nearestBlood != null) {
            mob.setTarget(nearestBlood);
        }
    }

    @Override
    public @NotNull GoalKey<Mob> getKey() { return key; }

    @Override
    public @NotNull EnumSet<GoalType> getTypes() { return EnumSet.of(GoalType.TARGET); }
}

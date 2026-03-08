package dev.itsrealperson.permadeath.pdcentities.script.ai;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.util.Vector;
import java.util.EnumSet;
import java.util.Random;
import org.jetbrains.annotations.NotNull;

public class PdcShadowStepGoal implements Goal<Mob> {
    private final Mob mob;
    private final Random random = new Random();
    private final GoalKey<Mob> key;

    public PdcShadowStepGoal(Mob mob) {
        this.mob = mob;
        this.key = GoalKey.of(Mob.class, new NamespacedKey("permadeath", "shadow_" + mob.getUniqueId()));
    }

    @Override
    public boolean shouldActivate() {
        LivingEntity target = mob.getTarget();
        return target != null && mob.getLocation().distanceSquared(target.getLocation()) > 100.0D && random.nextInt(50) == 0;
    }

    @Override
    public void start() {
        LivingEntity target = mob.getTarget();
        if (target == null) return;

        dev.itsrealperson.permadeath.pdcentities.PdcEntitiesAddon.debug("§d[AI] ShadowStep activado para " + mob.getName());

        Vector dir = target.getLocation().getDirection().multiply(-2);
        Location behind = target.getLocation().add(dir.getX(), 0, dir.getZ());
        
        if (behind.getBlock().getType().isAir()) {
            mob.teleportAsync(behind);
            behind.getWorld().playSound(behind, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
        }
    }

    @Override
    public @NotNull GoalKey<Mob> getKey() { return key; }

    @Override
    public @NotNull EnumSet<GoalType> getTypes() { return EnumSet.of(GoalType.MOVE); }
}

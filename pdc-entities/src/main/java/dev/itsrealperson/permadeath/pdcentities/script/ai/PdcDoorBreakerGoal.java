package dev.itsrealperson.permadeath.pdcentities.script.ai;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Mob;
import java.util.EnumSet;

public class PdcDoorBreakerGoal implements Goal<Mob> {
    private final Mob mob;
    private final GoalKey<Mob> key;
    private int breakTick = 0;

    public PdcDoorBreakerGoal(Mob mob) {
        this.mob = mob;
        this.key = GoalKey.of(Mob.class, new NamespacedKey("permadeath", "door_breaker_" + mob.getUniqueId()));
    }

    @Override
    public boolean shouldActivate() {
        if (mob.getTarget() == null) return false;
        Block b = mob.getLocation().getBlock().getRelative(getDirection());
        return b.getType().name().contains("DOOR") || b.getType().name().contains("GATE");
    }

    @Override
    public void tick() {
        Block door = mob.getLocation().getBlock().getRelative(getDirection());
        if (door.getType().name().contains("DOOR") || door.getType().name().contains("GATE")) {
            breakTick++;
            if (breakTick % 5 == 0) {
                mob.getWorld().playSound(mob.getLocation(), Sound.BLOCK_WOOD_BREAK, 1.0f, 1.0f);
                mob.swingMainHand();
            }
            if (breakTick > 60) { // 3 segundos para romper una puerta
                door.setType(Material.AIR);
                mob.getWorld().playSound(mob.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1.0f, 1.0f);
                breakTick = 0;
            }
        }
    }

    private BlockFace getDirection() {
        float yaw = mob.getLocation().getYaw();
        if (yaw < 0) yaw += 360;
        if (yaw >= 315 || yaw < 45) return BlockFace.SOUTH;
        if (yaw < 135) return BlockFace.WEST;
        if (yaw < 225) return BlockFace.NORTH;
        return BlockFace.EAST;
    }

    @Override
    public @org.jetbrains.annotations.NotNull GoalKey<Mob> getKey() { return key; }

    @Override
    public @org.jetbrains.annotations.NotNull EnumSet<GoalType> getTypes() { return EnumSet.of(GoalType.MOVE); }
}

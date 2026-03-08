package dev.itsrealperson.permadeath.pdcentities.script.ai;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import java.util.EnumSet;

public class PdcBlockBreakerGoal implements Goal<Mob> {
    private final Mob mob;
    private final GoalKey<Mob> key;
    private int breakTick = 0;

    public PdcBlockBreakerGoal(Mob mob) {
        this.mob = mob;
        this.key = GoalKey.of(Mob.class, new NamespacedKey("permadeath", "breaker_" + mob.getUniqueId()));
    }

    @Override
    public boolean shouldActivate() {
        LivingEntity target = mob.getTarget();
        if (target == null || !mob.isOnGround()) return false;
        
        // No romper si el objetivo está muy lejos (más de 16 bloques)
        if (mob.getLocation().distanceSquared(target.getLocation()) > 256) {
            return false;
        }
        
        // Comprobar 3 alturas: pies, torso y cabeza
        for (int y = 0; y <= 2; y++) {
            Block b = mob.getLocation().add(0, y, 0).getBlock().getRelative(getDirection());
            if (b.getType().isSolid() && !b.getType().name().contains("BEDROCK") && b.getType() != Material.AIR) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean shouldStayActive() {
        LivingEntity target = mob.getTarget();
        if (target == null) return false;
        return mob.getLocation().distanceSquared(target.getLocation()) <= 256;
    }

    @Override
    public void tick() {
        Block targetBlock = null;
        BlockFace dir = getDirection();
        
        for (int y = 0; y <= 2; y++) {
            Block b = mob.getLocation().add(0, y, 0).getBlock().getRelative(dir);
            if (b.getType().isSolid() && !b.getType().name().contains("BEDROCK") && b.getType() != Material.AIR) {
                targetBlock = b;
                break;
            }
        }

        if (targetBlock != null) {
            breakTick++;
            if (breakTick % 5 == 0) {
                mob.getWorld().playSound(mob.getLocation(), Sound.BLOCK_STONE_HIT, 1.0f, 1.0f);
                mob.swingMainHand();
            }
            if (breakTick > 30) { // 1.5 segundos por bloque
                targetBlock.setType(Material.AIR);
                breakTick = 0;
            }
        } else {
            breakTick = 0;
        }
    }

    private BlockFace getDirection() {
        LivingEntity target = mob.getTarget();
        if (target == null) return BlockFace.NORTH;

        // Obtener dirección basada en el objetivo
        org.bukkit.util.Vector targetVec = target.getLocation().toVector();
        org.bukkit.util.Vector mobVec = mob.getLocation().toVector();
        org.bukkit.util.Vector dir = targetVec.subtract(mobVec).normalize();
        double x = dir.getX();
        double z = dir.getZ();

        if (Math.abs(x) > Math.abs(z)) {
            return (x > 0) ? BlockFace.EAST : BlockFace.WEST;
        } else {
            return (z > 0) ? BlockFace.SOUTH : BlockFace.NORTH;
        }
    }

    @Override
    public @org.jetbrains.annotations.NotNull GoalKey<Mob> getKey() { return key; }

    @Override
    public @org.jetbrains.annotations.NotNull EnumSet<GoalType> getTypes() { return EnumSet.of(GoalType.MOVE); }
}

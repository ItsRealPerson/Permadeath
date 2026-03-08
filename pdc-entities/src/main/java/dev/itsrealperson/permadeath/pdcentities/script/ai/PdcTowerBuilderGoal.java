package dev.itsrealperson.permadeath.pdcentities.script.ai;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import java.util.EnumSet;
import java.util.Random;
import org.jetbrains.annotations.NotNull;

public class PdcTowerBuilderGoal implements Goal<Mob> {
    private final Mob mob;
    private final GoalKey<Mob> key;
    private final Random random = new Random();

    public PdcTowerBuilderGoal(Mob mob) {
        this.mob = mob;
        this.key = GoalKey.of(Mob.class, new NamespacedKey("permadeath", "builder_" + mob.getUniqueId()));
    }

    @Override
    public boolean shouldActivate() {
        LivingEntity target = mob.getTarget();
        if (target == null) return false;
        double yDiff = target.getLocation().getY() - mob.getLocation().getY();
        
        org.bukkit.Location loc = mob.getLocation().clone();
        loc.setY(target.getLocation().getY());
        double xzDistSqr = loc.distanceSquared(target.getLocation());
        
        // Permitir activarse en agua o en el suelo
        boolean canBuild = mob.isOnGround() || mob.isInWater() || mob.getLocation().getBlock().getType().name().contains("WATER");
        
        return yDiff > 1.5D && xzDistSqr < 4.0D && canBuild && random.nextInt(5) == 0;
    }

    @Override
    public void start() {
        // En agua, el impulso debe ser mayor o diferente
        double yVel = mob.isInWater() ? 0.6 : 0.5;
        mob.setVelocity(new org.bukkit.util.Vector(0, yVel, 0)); 
        
        mob.getScheduler().runDelayed(
            org.bukkit.Bukkit.getPluginManager().getPlugin("Permadeath"),
            task -> {
                if (mob.isValid()) {
                    // Colocar bloque justo donde estaban los pies
                    org.bukkit.block.Block b = mob.getLocation().getBlock().getRelative(org.bukkit.block.BlockFace.DOWN);
                    
                    if (b.getType().isAir() || b.getType().name().contains("WATER") || b.getType().name().contains("LAVA")) {
                        b.setType(Material.COBBLESTONE);
                        mob.getWorld().playSound(mob.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
                        dev.itsrealperson.permadeath.pdcentities.PdcEntitiesAddon.debug("§d[AI] TowerBuilder colocó bloque en " + b.getY() + " (Agua: " + b.getType().name().contains("WATER") + ")");
                    }
                }
            }, null, 3L); // 3 ticks es más reactivo
    }

    @Override
    public @NotNull GoalKey<Mob> getKey() { return key; }

    @Override
    public @NotNull EnumSet<GoalType> getTypes() { return EnumSet.of(GoalType.MOVE, GoalType.JUMP); }
}

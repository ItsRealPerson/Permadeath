package dev.itsrealperson.permadeath.pdcentities.script.ai;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.util.Vector;
import java.util.EnumSet;
import org.jetbrains.annotations.NotNull;

public class PdcPillarClimbGoal implements Goal<Mob> {
    private final Mob mob;
    private final GoalKey<Mob> key;

    public PdcPillarClimbGoal(Mob mob) {
        this.mob = mob;
        this.key = GoalKey.of(Mob.class, new NamespacedKey("permadeath", "pillar_" + mob.getUniqueId()));
    }

    @Override
    public boolean shouldActivate() {
        LivingEntity target = mob.getTarget();
        if (target == null) return false;
        
        double yDiff = target.getLocation().getY() - mob.getLocation().getY();
        
        org.bukkit.Location loc = mob.getLocation().clone();
        loc.setY(target.getLocation().getY());
        double xzDistSqr = loc.distanceSquared(target.getLocation());
        
        // Solo activar si estamos cerca horizontalmente pero el objetivo está arriba
        return yDiff > 3.0D && xzDistSqr < 25.0D;
    }

    @Override
    public boolean shouldStayActive() {
        LivingEntity target = mob.getTarget();
        if (target == null) return false;

        double yDiff = target.getLocation().getY() - mob.getLocation().getY();
        
        org.bukkit.Location loc = mob.getLocation().clone();
        loc.setY(target.getLocation().getY());
        double xzDistSqr = loc.distanceSquared(target.getLocation());

        // Si nos alejamos demasiado horizontalmente (ej: empujón fuerte), dejar de escalar
        return yDiff > 0.5D && xzDistSqr < 49.0D;
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null) return;

        double dX = target.getLocation().getX() - mob.getLocation().getX();
        double dZ = target.getLocation().getZ() - mob.getLocation().getZ();
        
        // Normalizar horizontal para evitar tps (velocidad constante)
        org.bukkit.util.Vector horizontal = new org.bukkit.util.Vector(dX, 0, dZ).normalize().multiply(0.2);
        
        mob.setVelocity(new Vector(horizontal.getX(), 0.25, horizontal.getZ()));
        mob.setFallDistance(0); 
    }

    @Override
    public @NotNull GoalKey<Mob> getKey() { return key; }

    @Override
    public @NotNull EnumSet<GoalType> getTypes() { return EnumSet.of(GoalType.MOVE, GoalType.JUMP); }
}

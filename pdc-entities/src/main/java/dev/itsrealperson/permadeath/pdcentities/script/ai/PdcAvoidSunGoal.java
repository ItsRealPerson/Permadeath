package dev.itsrealperson.permadeath.pdcentities.script.ai;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Mob;
import java.util.EnumSet;

public class PdcAvoidSunGoal implements Goal<Mob> {
    private final Mob mob;
    private final GoalKey<Mob> key;

    public PdcAvoidSunGoal(Mob mob) {
        this.mob = mob;
        this.key = GoalKey.of(Mob.class, new NamespacedKey("permadeath", "avoid_sun_" + mob.getUniqueId()));
    }

    @Override
    public boolean shouldActivate() {
        return mob.getFireTicks() > 0 && mob.getWorld().getTime() < 13000;
    }

    @Override
    public void tick() {
        // Buscar bloque con luz de cielo baja (sombra)
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                Block b = mob.getLocation().add(x, 0, z).getBlock();
                if (b.getLightFromSky() < 10) {
                    mob.getPathfinder().moveTo(b.getLocation(), 1.5);
                    return;
                }
            }
        }
    }

    @Override
    public @org.jetbrains.annotations.NotNull GoalKey<Mob> getKey() { return key; }

    @Override
    public @org.jetbrains.annotations.NotNull EnumSet<GoalType> getTypes() { return EnumSet.of(GoalType.MOVE); }
}

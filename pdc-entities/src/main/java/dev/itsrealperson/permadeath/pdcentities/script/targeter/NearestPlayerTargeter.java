package dev.itsrealperson.permadeath.pdcentities.script.targeter;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class NearestPlayerTargeter implements Targeter {
    @Override
    public Collection<Entity> getTargets(Entity caster) {
        return caster.getWorld().getPlayers().stream()
                .filter(p -> p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR)
                .min(Comparator.comparingDouble(p -> p.getLocation().distanceSquared(caster.getLocation())))
                .map(p -> Collections.singletonList((Entity) p))
                .orElse(Collections.emptyList());
    }
}

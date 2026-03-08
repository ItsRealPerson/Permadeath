package dev.itsrealperson.permadeath.pdcentities.script.targeter;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Selecciona jugadores en un radio circular.
 * Argumentos: r (radio)
 */
public class PlayersInRadiusTargeter implements Targeter {

    private final double radius;

    public PlayersInRadiusTargeter(Map<String, String> args) {
        this.radius = Double.parseDouble(args.getOrDefault("r", "5"));
    }

    @Override
    public Collection<Entity> getTargets(Entity caster) {
        return caster.getNearbyEntities(radius, radius, radius).stream()
                .filter(e -> e instanceof Player p && p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR)
                .collect(Collectors.toList());
    }
}

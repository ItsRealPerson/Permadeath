package dev.itsrealperson.permadeath.pdcentities.script.targeter;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Define la lógica para seleccionar objetivos de una habilidad.
 */
public interface Targeter {
    /**
     * Obtiene las entidades objetivo.
     */
    default Collection<Entity> getTargets(Entity caster) {
        return Collections.emptyList();
    }

    /**
     * Obtiene las localizaciones objetivo (para selectores espaciales).
     * Por defecto, devuelve la ubicación de las entidades objetivo.
     */
    default Collection<Location> getTargetLocations(Entity caster) {
        return getTargets(caster).stream().map(Entity::getLocation).collect(Collectors.toList());
    }
}

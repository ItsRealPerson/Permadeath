package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import java.util.Collection;

public interface Mechanic {
    /**
     * Ejecuta sobre entidades.
     */
    void execute(SkillContext context, Collection<Entity> targets);

    /**
     * Ejecuta sobre localizaciones (para @Ring, @Block).
     * Por defecto no hace nada, las mecánicas deben sobrescribirlo si lo soportan.
     */
    default void executeAtLocations(SkillContext context, Collection<Location> locations) {}
}

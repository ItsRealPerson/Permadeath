package dev.itsrealperson.permadeath.pdcentities.script.targeter;

import org.bukkit.entity.Entity;
import java.util.Collection;
import java.util.Collections;

/**
 * Selecciona al propio lanzador como objetivo.
 */
public class SelfTargeter implements Targeter {
    @Override
    public Collection<Entity> getTargets(Entity caster) {
        return Collections.singletonList(caster);
    }
}

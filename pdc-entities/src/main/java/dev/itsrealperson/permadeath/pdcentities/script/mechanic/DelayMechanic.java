package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.entity.Entity;
import java.util.Collection;
import java.util.Map;

/**
 * Mecánica especial para pausar la ejecución de una MetaSkill.
 */
public class DelayMechanic implements Mechanic {
    private final int ticks;

    public DelayMechanic(Map<String, String> args) {
        this.ticks = Integer.parseInt(args.getOrDefault("t", args.getOrDefault("ticks", "10")));
    }

    public int getTicks() {
        return ticks;
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        // No hace nada por sí misma, la lógica la maneja el ejecutor de listas
    }
}

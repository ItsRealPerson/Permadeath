package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.entity.Entity;
import java.util.Collection;
import java.util.Map;

public class IgniteMechanic implements Mechanic {
    private final int ticks;

    public IgniteMechanic(Map<String, String> args) {
        this.ticks = Integer.parseInt(args.getOrDefault("ticks", "100"));
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        for (Entity target : targets) {
            target.setFireTicks(ticks);
        }
    }
}

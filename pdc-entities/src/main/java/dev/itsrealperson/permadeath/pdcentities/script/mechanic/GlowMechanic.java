package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.entity.Entity;
import java.util.Collection;
import java.util.Map;

public class GlowMechanic implements Mechanic {
    private final int duration;

    public GlowMechanic(Map<String, String> args) {
        this.duration = Integer.parseInt(args.getOrDefault("duration", "100"));
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        for (Entity target : targets) {
            target.setGlowing(true);
            org.bukkit.Bukkit.getGlobalRegionScheduler().runDelayed(
                org.bukkit.Bukkit.getPluginManager().getPlugin("Permadeath"),
                t -> target.setGlowing(false), 
                duration
            );
        }
    }
}

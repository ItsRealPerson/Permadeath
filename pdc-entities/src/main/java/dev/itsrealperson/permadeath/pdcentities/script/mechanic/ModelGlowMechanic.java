package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.integration.BetterModelHook;
import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import kr.toxicity.model.api.tracker.TrackerUpdateAction;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import java.util.Collection;
import java.util.Map;

public class ModelGlowMechanic implements Mechanic {
    private final boolean enabled;
    private final Integer color;
    private final String part;

    public ModelGlowMechanic(Map<String, String> args) {
        this.enabled = Boolean.parseBoolean(args.getOrDefault("enabled", "true"));
        String colorStr = args.get("color");
        this.color = (colorStr != null) ? Integer.parseInt(colorStr.replace("#", ""), 16) : null;
        this.part = args.getOrDefault("part", null);
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        for (Entity target : targets) {
            if (target instanceof LivingEntity living) {
                BetterModelHook.updateModel(living, TrackerUpdateAction.glow(enabled), part);
                if (enabled && color != null) {
                    BetterModelHook.updateModel(living, TrackerUpdateAction.glowColor(color), part);
                }
            }
        }
    }
}

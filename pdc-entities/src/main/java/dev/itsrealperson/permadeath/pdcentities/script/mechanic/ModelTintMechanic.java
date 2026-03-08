package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.integration.BetterModelHook;
import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import kr.toxicity.model.api.tracker.TrackerUpdateAction;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import java.util.Collection;
import java.util.Map;

public class ModelTintMechanic implements Mechanic {
    private final int color;
    private final String part;

    public ModelTintMechanic(Map<String, String> args) {
        String colorStr = args.getOrDefault("color", "FFFFFF").replace("#", "");
        this.color = Integer.parseInt(colorStr, 16);
        this.part = args.getOrDefault("part", null);
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        for (Entity target : targets) {
            if (target instanceof LivingEntity living) {
                BetterModelHook.updateModel(living, TrackerUpdateAction.tint(color), part);
            }
        }
    }
}

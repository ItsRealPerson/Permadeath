package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.integration.BetterModelHook;
import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import kr.toxicity.model.api.tracker.TrackerUpdateAction;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import java.util.Collection;
import java.util.Map;

public class ModelVisibilityMechanic implements Mechanic {
    private final boolean visible;
    private final String part;

    public ModelVisibilityMechanic(Map<String, String> args) {
        this.visible = Boolean.parseBoolean(args.getOrDefault("visible", "true"));
        this.part = args.getOrDefault("part", null);
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        for (Entity target : targets) {
            if (target instanceof LivingEntity living) {
                BetterModelHook.updateModel(living, TrackerUpdateAction.togglePart(visible), part);
            }
        }
    }
}

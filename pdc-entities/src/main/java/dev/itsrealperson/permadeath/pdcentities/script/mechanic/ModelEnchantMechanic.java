package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.integration.BetterModelHook;
import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import kr.toxicity.model.api.tracker.TrackerUpdateAction;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import java.util.Collection;
import java.util.Map;

public class ModelEnchantMechanic implements Mechanic {
    private final boolean enabled;
    private final String part;

    public ModelEnchantMechanic(Map<String, String> args) {
        this.enabled = Boolean.parseBoolean(args.getOrDefault("enabled", "true"));
        this.part = args.getOrDefault("part", null);
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        for (Entity target : targets) {
            if (target instanceof LivingEntity living) {
                BetterModelHook.updateModel(living, TrackerUpdateAction.enchant(enabled), part);
            }
        }
    }
}

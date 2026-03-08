package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.integration.BetterModelHook;
import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.animation.AnimationModifier;
import kr.toxicity.model.api.bukkit.platform.BukkitAdapter;
import kr.toxicity.model.api.tracker.EntityTracker;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.Collection;
import java.util.Map;

public class AnimationMechanic implements Mechanic {

    private final String animation;
    private final boolean playOnce;

    public AnimationMechanic(Map<String, String> args) {
        this.animation = args.getOrDefault("name", "idle");
        this.playOnce = Boolean.parseBoolean(args.getOrDefault("once", "true"));
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        if (!BetterModelHook.isEnabled()) return;

        for (Entity target : targets) {
            BetterModel.registry(target.getUniqueId()).ifPresent(reg -> {
                reg.trackers().forEach(tracker -> {
                    tracker.animate(animation, playOnce ? 
                        kr.toxicity.model.api.animation.AnimationModifier.DEFAULT_WITH_PLAY_ONCE : 
                        kr.toxicity.model.api.animation.AnimationModifier.DEFAULT);
                });
            });
        }
    }
}

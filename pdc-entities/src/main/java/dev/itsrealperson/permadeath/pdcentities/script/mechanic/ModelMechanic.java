package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.integration.BetterModelHook;
import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.Collection;
import java.util.Map;

public class ModelMechanic implements Mechanic {

    private final String modelId;

    public ModelMechanic(Map<String, String> args) {
        this.modelId = args.getOrDefault("id", "");
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        if (!BetterModelHook.isEnabled() || modelId.isEmpty()) return;

        for (Entity target : targets) {
            if (target instanceof LivingEntity living) {
                // Primero removemos el modelo actual para asegurar el cambio
                BetterModelHook.removeModel(living);
                // Aplicamos el nuevo
                BetterModelHook.applyModel(living, modelId);
            }
        }
    }
}

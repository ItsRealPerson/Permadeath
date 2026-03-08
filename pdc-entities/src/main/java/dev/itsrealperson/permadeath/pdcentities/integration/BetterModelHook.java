package dev.itsrealperson.permadeath.pdcentities.integration;

import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.bukkit.BetterModelBukkit;
import kr.toxicity.model.api.bukkit.platform.BukkitAdapter;
import kr.toxicity.model.api.tracker.EntityTrackerRegistry;
import kr.toxicity.model.api.tracker.TrackerUpdateAction;
import kr.toxicity.model.api.util.function.BonePredicate;
import dev.itsrealperson.permadeath.pdcentities.PdcEntitiesAddon;
import org.bukkit.entity.LivingEntity;

public class BetterModelHook {

    private static boolean enabled = false;

    public static void init() {
        try {
            Class.forName("kr.toxicity.model.api.BetterModel");
            enabled = org.bukkit.Bukkit.getPluginManager().isPluginEnabled("BetterModel");
            if (enabled) {
                PdcEntitiesAddon.log("§aBetterModel detectado y vinculado con éxito (API v2.0.0).");
            }
        } catch (ClassNotFoundException e) {
            enabled = false;
        }
    }

    public static void applyModel(LivingEntity entity, String modelId) {
        if (!enabled || modelId == null || modelId.isEmpty()) return;

        BetterModel.model(modelId).ifPresent(blueprint -> {
            blueprint.getOrCreate(BukkitAdapter.adapt(entity));
            PdcEntitiesAddon.debug("§b[BetterModel] Aplicado modelo '" + modelId + "' a " + entity.getType());
        });
    }

    public static void removeModel(LivingEntity entity) {
        if (!enabled) return;
        BetterModel.registry(BukkitAdapter.adapt(entity)).ifPresent(EntityTrackerRegistry::close);
    }

    public static void updateModel(LivingEntity entity, TrackerUpdateAction action, String part) {
        if (!enabled) return;
        BetterModel.registry(BukkitAdapter.adapt(entity)).ifPresent(reg -> {
            reg.trackers().forEach(tracker -> {
                if (part == null || part.isEmpty()) {
                    tracker.update(action);
                } else {
                    tracker.update(action, BonePredicate.name(part));
                }
            });
        });
    }

    public static boolean isEnabled() {
        return enabled;
    }
}

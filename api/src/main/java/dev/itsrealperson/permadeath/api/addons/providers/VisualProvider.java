package dev.itsrealperson.permadeath.api.addons.providers;

import org.bukkit.entity.Entity;

/**
 * Permite a un addon manejar la representación visual de una entidad.
 * Principalmente utilizado para la integración con sistemas de modelos 3D como BetterModel.
 */
public interface VisualProvider {

    /**
     * Aplica un modelo visual a una entidad.
     * @param entity La entidad a la que se aplicará el modelo.
     * @param modelId El identificador del modelo (ej: "custom_boss").
     */
    void applyModel(Entity entity, String modelId);

    /**
     * Cambia la animación actual del modelo de la entidad.
     * @param entity La entidad.
     * @param animationId El identificador de la animación (ej: "attack", "walk").
     */
    void playAnimation(Entity entity, String animationId);

    /**
     * Elimina el modelo visual de la entidad.
     * @param entity La entidad.
     */
    void removeModel(Entity entity);
}

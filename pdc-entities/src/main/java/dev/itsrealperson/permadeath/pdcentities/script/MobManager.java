package dev.itsrealperson.permadeath.pdcentities.script;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Gestiona todas las instancias de mobs personalizados activos.
 */
public class MobManager {

    private static final Map<UUID, PdcMobInstance> activeMobs = new HashMap<>();
    private static final Map<String, PdcMobTemplate> templates = new HashMap<>();
    private static NamespacedKey templateKey;

    public static void init(org.bukkit.plugin.Plugin plugin) {
        templateKey = new NamespacedKey(plugin, "pdc_mob_template");
    }

    public static void registerTemplate(PdcMobTemplate template) {
        templates.put(template.getId().toLowerCase(), template);
    }

    public static PdcMobInstance spawnMob(String templateId, org.bukkit.Location loc, long day) {
        PdcMobTemplate template = templates.get(templateId.toLowerCase());
        if (template == null) return null;

        LivingEntity entity = (LivingEntity) loc.getWorld().spawnEntity(loc, template.getBaseType());
        UUID uuid = entity.getUniqueId();
        
        // Persistencia
        entity.getPersistentDataContainer().set(templateKey, PersistentDataType.STRING, template.getId());
        
        // IMPORTANTE: Registrar UUID antes de crear la instancia para evitar duplicados por eventos de carga
        activeMobs.put(uuid, null); 
        
        PdcMobInstance instance = new PdcMobInstance(entity, template, day);
        activeMobs.put(uuid, instance);
        
        instance.executeTrigger("onSpawn");
        return instance;
    }

    public static void restoreInstance(Entity entity) {
        if (!(entity instanceof LivingEntity living)) return;
        UUID uuid = entity.getUniqueId();
        if (activeMobs.containsKey(uuid) && activeMobs.get(uuid) != null) return;
        if (!entity.getPersistentDataContainer().has(templateKey, PersistentDataType.STRING)) return;

        String id = entity.getPersistentDataContainer().get(templateKey, PersistentDataType.STRING);
        PdcMobTemplate template = templates.get(id.toLowerCase());
        
        if (template != null) {
            PdcMobInstance instance = new PdcMobInstance(living, template, dev.itsrealperson.permadeath.pdcentities.PdcEntitiesAddon.getApi().getDay());
            activeMobs.put(uuid, instance);
            dev.itsrealperson.permadeath.pdcentities.PdcEntitiesAddon.debug("§a[Restauración] Mob " + id + " restaurado para " + uuid);
        }
    }

    public static void updateAll() {
        for (PdcMobInstance instance : activeMobs.values()) {
            if (instance == null) continue;
            PdcMobTemplate newTemplate = templates.get(instance.getTemplate().getId().toLowerCase());
            if (newTemplate != null) {
                instance.updateTemplate(newTemplate);
            }
        }
    }

    public static PdcMobInstance getInstance(Entity entity) {
        if (entity == null) return null;
        
        // 1. Check directo por UUID
        PdcMobInstance inst = activeMobs.get(entity.getUniqueId());
        if (inst != null) return inst;

        // 2. Check por registro de BetterModel (para Hitboxes)
        if (dev.itsrealperson.permadeath.pdcentities.integration.BetterModelHook.isEnabled()) {
            try {
                var platformEntity = kr.toxicity.model.api.bukkit.platform.BukkitAdapter.adapt(entity);
                if (platformEntity instanceof kr.toxicity.model.api.nms.HitBox hitbox) {
                    var reg = hitbox.registry();
                    if (reg.isPresent()) {
                        return activeMobs.get(reg.get().uuid());
                    }
                }
            } catch (Exception ignored) {}
        }

        // 3. Check por persistencia (si no estaba en memoria)
        if (entity.getPersistentDataContainer().has(templateKey, PersistentDataType.STRING)) {
            restoreInstance(entity);
            return activeMobs.get(entity.getUniqueId());
        }
        return null;
    }

    public static Map<String, PdcMobTemplate> getTemplates() {
        return templates;
    }

    public static PdcMobTemplate findReplacement(EntityType vanillaType, java.util.Random random) {
        for (PdcMobTemplate template : templates.values()) {
            if (template.getReplaceTypes().contains(vanillaType)) {
                if (random.nextDouble() * 100.0 <= template.getSpawnChance()) {
                    return template;
                }
            }
        }
        return null;
    }

    public static void removeInstance(UUID uuid) {
        PdcMobInstance instance = activeMobs.remove(uuid);
        if (instance != null) {
            instance.cancelTasks();
        }
    }
}

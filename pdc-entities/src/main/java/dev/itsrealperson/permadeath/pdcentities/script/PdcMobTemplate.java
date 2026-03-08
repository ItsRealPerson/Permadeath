package dev.itsrealperson.permadeath.pdcentities.script;

import org.bukkit.entity.EntityType;
import java.util.ArrayList;
import java.util.List;

/**
 * Plantilla de un mob personalizado cargado desde el YAML.
 */
public class PdcMobTemplate {

    private final String id;
    private final EntityType baseType;
    private final String displayName;
    private final List<Skill> skills = new ArrayList<>();
    
    // Configuración de vida y daño con soporte para fórmulas
    private String healthFormula = "20";
    private String damageFormula = "5";
    
    // Reglas de Spawn
    private double spawnChance = 0.0;
    private List<EntityType> replaceTypes = new ArrayList<>();
    
    // Spawneo Natural
    private double naturalChance = 0.0;
    
    // Spawneo por Interacción
    private org.bukkit.Material interactBlock;
    private String interactAction;

    // Integración Visual
    private String modelId;
    private final java.util.Map<String, String> animations = new java.util.HashMap<>();

    // IA Avanzada
    private boolean clearAi = false;
    private final List<String> aiGoals = new ArrayList<>();
    private final List<String> aiTargets = new ArrayList<>();

    public PdcMobTemplate(String id, EntityType baseType, String displayName) {
        this.id = id;
        this.baseType = baseType;
        this.displayName = displayName;
    }

    public void addSkill(Skill skill) {
        skills.add(skill);
    }

    public String getId() { return id; }
    public EntityType getBaseType() { return baseType; }
    public String getDisplayName() { return displayName; }
    public List<Skill> getSkills() { return skills; }
    
    public String getHealthFormula() { return healthFormula; }
    public void setHealthFormula(String formula) { this.healthFormula = formula; }
    
    public String getDamageFormula() { return damageFormula; }
    public void setDamageFormula(String formula) { this.damageFormula = formula; }

    public double getSpawnChance() { return spawnChance; }
    public void setSpawnChance(double chance) { this.spawnChance = chance; }

    public List<EntityType> getReplaceTypes() { return replaceTypes; }
    public void addReplaceType(EntityType type) { this.replaceTypes.add(type); }

    public double getNaturalChance() { return naturalChance; }
    public void setNaturalChance(double chance) { this.naturalChance = chance; }

    public org.bukkit.Material getInteractBlock() { return interactBlock; }
    public void setInteractBlock(org.bukkit.Material material) { this.interactBlock = material; }

    public String getInteractAction() { return interactAction; }
    public void setInteractAction(String action) { this.interactAction = action; }

    public boolean isClearAi() { return clearAi; }
    public void setClearAi(boolean clearAi) { this.clearAi = clearAi; }

    public List<String> getAiGoals() { return aiGoals; }
    public List<String> getAiTargets() { return aiTargets; }

    public String getModelId() { return modelId; }
    public void setModelId(String modelId) { this.modelId = modelId; }

    public java.util.Map<String, String> getAnimations() { return animations; }
}

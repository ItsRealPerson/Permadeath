package dev.itsrealperson.permadeath.pdcentities.script;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import java.io.File;
import java.util.logging.Logger;

public class MobLoader {

    private final File mobsFolder;
    private final Logger logger;

    public MobLoader(File folder, Logger logger) {
        this.mobsFolder = new File(folder, "mobs");
        this.logger = logger;
        if (!mobsFolder.exists()) mobsFolder.mkdirs();
    }

    public void loadAll() {
        if (!mobsFolder.exists()) mobsFolder.mkdirs();
        int count = scanAndLoad(mobsFolder);
        logger.info(" [PDC-Entities] Cargados " + count + " mobs personalizados desde carpetas y paquetes.");
    }

    private int scanAndLoad(File folder) {
        File[] files = folder.listFiles();
        if (files == null) return 0;

        int count = 0;
        for (File file : files) {
            if (file.isDirectory()) {
                // Escaneo recursivo de subcarpetas
                count += scanAndLoad(file);
            } else if (file.getName().endsWith(".yml")) {
                try {
                    loadMob(file);
                    count++;
                } catch (Exception e) {
                    logger.severe("Error al cargar mob desde " + file.getName() + ": " + e.getMessage());
                }
            }
        }
        return count;
    }

    private void loadMob(File file) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String id : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(id);
            if (section == null) continue;

            String typeStr = section.getString("Type", "ZOMBIE");
            String display = section.getString("Display", id);
            
            PdcMobTemplate template = new PdcMobTemplate(id, EntityType.valueOf(typeStr.toUpperCase()), display);
            template.setHealthFormula(section.getString("Health", "20"));
            template.setDamageFormula(section.getString("Damage", "5"));
            template.setModelId(section.getString("Model", null));

            if (section.contains("Animations")) {
                ConfigurationSection animSection = section.getConfigurationSection("Animations");
                if (animSection != null) {
                    for (String key : animSection.getKeys(false)) {
                        template.getAnimations().put(key.toLowerCase(), animSection.getString(key));
                    }
                }
            }

            // Cargar Reglas de Spawn
            if (section.contains("SpawnRules")) {
                ConfigurationSection spawnSection = section.getConfigurationSection("SpawnRules");
                template.setSpawnChance(spawnSection.getDouble("Chance", 0.0));
                for (String typeName : spawnSection.getStringList("Replace")) {
                    try {
                        template.addReplaceType(EntityType.valueOf(typeName.toUpperCase()));
                    } catch (Exception ignored) {}
                }

                // Natural
                if (spawnSection.contains("Natural")) {
                    template.setNaturalChance(spawnSection.getDouble("Natural.Chance", 0.0));
                }

                // Interact
                if (spawnSection.contains("Interact")) {
                    String matStr = spawnSection.getString("Interact.Block");
                    if (matStr != null) template.setInteractBlock(org.bukkit.Material.valueOf(matStr.toUpperCase()));
                    template.setInteractAction(spawnSection.getString("Interact.Action", "RIGHT_CLICK"));
                }
            }

            // Cargar IA
            if (section.contains("AI")) {
                ConfigurationSection aiSection = section.getConfigurationSection("AI");
                if (aiSection != null) {
                    template.setClearAi(aiSection.getBoolean("Clear", false));
                    
                    // Cargar Goals
                    if (aiSection.contains("Goals")) {
                        for (Object obj : aiSection.getList("Goals")) {
                            String goalStr = String.valueOf(obj);
                            // Si obj es un Map (ej: {0=float}), convertirlo a "0: float"
                            if (obj instanceof java.util.Map<?, ?> map && !map.isEmpty()) {
                                var entry = map.entrySet().iterator().next();
                                goalStr = entry.getKey() + ": " + entry.getValue();
                            }
                            template.getAiGoals().add(goalStr);
                        }
                        dev.itsrealperson.permadeath.pdcentities.PdcEntitiesAddon.debug("  - Cargadas " + template.getAiGoals().size() + " AI Goals para " + id);
                    }

                    // Cargar Targets
                    if (aiSection.contains("Targets")) {
                        for (Object obj : aiSection.getList("Targets")) {
                            String targetStr = String.valueOf(obj);
                            if (obj instanceof java.util.Map<?, ?> map && !map.isEmpty()) {
                                var entry = map.entrySet().iterator().next();
                                targetStr = entry.getKey() + ": " + entry.getValue();
                            }
                            template.getAiTargets().add(targetStr);
                        }
                        dev.itsrealperson.permadeath.pdcentities.PdcEntitiesAddon.debug("  - Cargadas " + template.getAiTargets().size() + " AI Targets para " + id);
                    }
                }
            }

            // Cargar Habilidades
            if (section.contains("Skills")) {
                for (String line : section.getStringList("Skills")) {
                    Skill skill = SkillParser.parse(line);
                    if (skill != null) template.addSkill(skill);
                }
            }

            MobManager.registerTemplate(template);
        }
    }
}

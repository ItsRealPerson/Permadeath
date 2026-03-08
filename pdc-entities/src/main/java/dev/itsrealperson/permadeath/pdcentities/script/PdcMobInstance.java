package dev.itsrealperson.permadeath.pdcentities.script;

import dev.itsrealperson.permadeath.pdcentities.PdcEntitiesAddon;
import dev.itsrealperson.permadeath.pdcentities.integration.BetterModelHook;
import dev.itsrealperson.permadeath.api.script.FormulaEvaluator;
import dev.itsrealperson.permadeath.util.TextUtils;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PdcMobInstance {

    private final LivingEntity entity;
    private PdcMobTemplate template;
    private final Map<String, Double> variables = new HashMap<>();
    private final Map<String, Double> persistentVariables = new HashMap<>();
    private final java.util.LinkedList<String> skillLog = new java.util.LinkedList<>();
    private final List<ScheduledTask> activeTasks = new ArrayList<>();
    private boolean inCombat = false;
    private boolean executing = false;

    public PdcMobInstance(LivingEntity entity, PdcMobTemplate template, long currentDay) {
        this.entity = entity;
        this.template = template;
        
        PdcEntitiesAddon.debug("§a[PdcMobInstance] Nueva instancia creada: " + template.getId() + " (" + entity.getUniqueId() + ")");
        
        variables.put("day", (double) currentDay);
        variables.put("health", entity.getHealth());
        
        applyAttributes();
    }

    public void updateTemplate(PdcMobTemplate newTemplate) {
        this.template = newTemplate;
        PdcEntitiesAddon.debug("§e[Reload] Actualizando lógica para " + entity.getUniqueId() + " (" + newTemplate.getId() + ")");
        applyAttributes();
    }

    public void cancelTasks() {
        activeTasks.forEach(ScheduledTask::cancel);
        activeTasks.clear();
    }

    public void playAnimation(String state) {
        String animConfig = template.getAnimations().get(state.toLowerCase());
        if (animConfig != null && BetterModelHook.isEnabled()) {
            String[] anims = animConfig.split(",");
            String animName = anims[new java.util.Random().nextInt(anims.length)].trim();

            kr.toxicity.model.api.BetterModel.registry(entity.getUniqueId()).ifPresent(reg -> {
                PdcEntitiesAddon.debug("§b[BetterModel] Disparando animación '" + animName + "' para " + state);
                reg.trackers().forEach(tracker -> {
                    tracker.animate(animName, kr.toxicity.model.api.animation.AnimationModifier.DEFAULT_WITH_PLAY_ONCE);
                });
            });
        }
    }

    public void logSkill(String info) {
        skillLog.addFirst("§7[" + new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date()) + "] §f" + info);
        if (skillLog.size() > 5) skillLog.removeLast();
    }

    public String replacePlaceholders(String text) {
        String result = text.replace("%mob%", entity.getCustomName() != null ? entity.getCustomName() : entity.getName());
        if (entity instanceof Mob mob && mob.getTarget() != null) {
            result = result.replace("%target%", mob.getTarget().getName());
        } else {
            result = result.replace("%target%", "nadie");
        }
        
        // Reemplazar variables %var:nombre%
        for (Map.Entry<String, Double> entry : getVariables().entrySet()) {
            result = result.replace("%var:" + entry.getKey() + "%", String.valueOf(entry.getValue()));
        }
        
        return result;
    }

    public java.util.List<String> getSkillLog() {
        return skillLog;
    }

    private void applyAttributes() {
        // Cancelar tareas previas (Timers, etc.) para evitar duplicados en recargas
        cancelTasks();

        double maxHealth = FormulaEvaluator.evaluate(template.getHealthFormula(), variables);
        
        // Usar NMSAccessor para saltarse el cap de Bukkit si es necesario
        var nms = PdcEntitiesAddon.getApi().getNmsAccessor();
        nms.setMaxHealth(entity, maxHealth, true);
        PdcEntitiesAddon.debug("Aplicada vida (" + maxHealth + ") a " + template.getId());
        
        // Aplicar modelo de BetterModel si existe
        BetterModelHook.applyModel(entity, template.getModelId());
        
        if (template.getDisplayName() != null) {
            entity.setCustomName(TextUtils.format(template.getDisplayName()));
            entity.setCustomNameVisible(true);
        }

        if (entity instanceof Mob mob) {
            if (template.isClearAi()) {
                nms.clearEntityPathfinders(mob);
                // También limpiar metas de Paper API
                org.bukkit.Bukkit.getMobGoals().getAllGoals(mob).forEach(g -> org.bukkit.Bukkit.getMobGoals().removeGoal(mob, g));
                PdcEntitiesAddon.debug("IA Limpiada para " + template.getId());
            }

            // Si se limpia la IA y no hay metas de ataque, añadir una por defecto si el mob es hostil
            boolean hasAttackGoal = template.getAiGoals().stream().anyMatch(g -> g.contains("attack") || g.contains("melee"));
            
            for (String line : template.getAiGoals()) {
                PdcEntitiesAddon.debug("Aplicando Goal: " + line);
                applyAiPiece(line, true, mob);
            }

            for (String line : template.getAiTargets()) {
                PdcEntitiesAddon.debug("Aplicando Target: " + line);
                applyAiPiece(line, false, mob);
            }

            // Inyectar pathfinders hostiles si la IA fue limpiada y no se especificaron metas de ataque
            if (template.isClearAi() && !hasAttackGoal) {
                PdcEntitiesAddon.debug("Inyectando IA hostil por defecto a " + template.getId());
                nms.injectHostilePathfinders(mob);
            }
        }

        for (Skill skill : template.getSkills()) {
            if (skill.getTrigger().equalsIgnoreCase("onTimer")) {
                long interval = Math.max(1, skill.getTriggerValue());
                PdcEntitiesAddon.debug("Registrado onTimer (" + interval + "t) para " + template.getId());
                
                ScheduledTask task = entity.getScheduler().runAtFixedRate(
                    org.bukkit.Bukkit.getPluginManager().getPlugin("Permadeath"),
                    t -> {
                        if (!entity.isValid()) {
                            t.cancel();
                            return;
                        }
                        skill.cast(entity);
                    },
                    null, interval, interval
                );
                
                if (task != null) {
                    activeTasks.add(task);
                }
            }
        }
    }

    private void applyAiPiece(String line, boolean isGoal, Mob mob) {
        try {
            String[] parts = line.split(":", 2);
            if (parts.length < 2) {
                PdcEntitiesAddon.debug("Formato de IA inválido: " + line);
                return;
            }

            int priority = Integer.parseInt(parts[0].trim());
            String data = parts[1].trim();

            String id = data;
            Map<String, String> args = new HashMap<>();
            if (data.contains("{")) {
                id = data.substring(0, data.indexOf("{"));
                String argsStr = data.substring(data.indexOf("{") + 1, data.indexOf("}"));
                args = SkillParser.parseArgs(argsStr);
            }

            var mobGoals = org.bukkit.Bukkit.getMobGoals();
            if (isGoal) {
                if (id.equalsIgnoreCase("leap")) {
                    mobGoals.addGoal(mob, priority, new dev.itsrealperson.permadeath.pdcentities.script.ai.PdcLeapGoal(mob, Float.parseFloat(args.getOrDefault("speed", "1.2"))));
                } else if (id.equalsIgnoreCase("stalk")) {
                    mobGoals.addGoal(mob, priority, new dev.itsrealperson.permadeath.pdcentities.script.ai.PdcStalkGoal(mob, Double.parseDouble(args.getOrDefault("dist", "10")), Double.parseDouble(args.getOrDefault("speed", "1.0"))));
                } else if (id.equalsIgnoreCase("shadow_step")) {
                    mobGoals.addGoal(mob, priority, new dev.itsrealperson.permadeath.pdcentities.script.ai.PdcShadowStepGoal(mob));
                } else if (id.equalsIgnoreCase("block_breaker")) {
                    mobGoals.addGoal(mob, priority, new dev.itsrealperson.permadeath.pdcentities.script.ai.PdcBlockBreakerGoal(mob));
                } else if (id.equalsIgnoreCase("door_breaker")) {
                    mobGoals.addGoal(mob, priority, new dev.itsrealperson.permadeath.pdcentities.script.ai.PdcDoorBreakerGoal(mob));
                } else if (id.equalsIgnoreCase("avoid_sun")) {
                    mobGoals.addGoal(mob, priority, new dev.itsrealperson.permadeath.pdcentities.script.ai.PdcAvoidSunGoal(mob));
                } else if (id.equalsIgnoreCase("tower_builder")) {
                    mobGoals.addGoal(mob, priority, new dev.itsrealperson.permadeath.pdcentities.script.ai.PdcTowerBuilderGoal(mob));
                } else if (id.equalsIgnoreCase("pillar_climb")) {
                    mobGoals.addGoal(mob, priority, new dev.itsrealperson.permadeath.pdcentities.script.ai.PdcPillarClimbGoal(mob));
                } else if (id.equalsIgnoreCase("strafe")) {
                    mobGoals.addGoal(mob, priority, new dev.itsrealperson.permadeath.pdcentities.script.ai.PdcStrafeGoal(mob, Double.parseDouble(args.getOrDefault("speed", "1.0"))));
                } else if (id.equalsIgnoreCase("sniper")) {
                    mobGoals.addGoal(mob, priority, new dev.itsrealperson.permadeath.pdcentities.script.ai.PdcSniperGoal(mob, Integer.parseInt(args.getOrDefault("interval", "20"))));
                } else {
                    PdcEntitiesAddon.getApi().getNmsAccessor().addGoalSelector(mob, priority, id, args);
                }
            } else {
                if (id.equalsIgnoreCase("blood_scent")) {
                    mobGoals.addGoal(mob, priority, new dev.itsrealperson.permadeath.pdcentities.script.ai.PdcBloodScentGoal(mob, Double.parseDouble(args.getOrDefault("range", "100"))));
                } else {
                    PdcEntitiesAddon.getApi().getNmsAccessor().addTargetSelector(mob, priority, id, args);
                }
            }
            PdcEntitiesAddon.debug("IA registrada con éxito: " + id + " (prioridad " + priority + ")");
        } catch (Exception e) {
            org.bukkit.Bukkit.getLogger().severe("[PDC-Entities] Error al aplicar pieza de IA '" + line + "': " + e.getMessage());
        }
    }

    public void executeTrigger(String triggerName) {
        if (executing) return;
        
        if (triggerName.equalsIgnoreCase("onEnterCombat")) {
            if (inCombat) return;
            inCombat = true;
        }
        
        executing = true;
        try {
            PdcEntitiesAddon.debug("Trigger '" + triggerName + "' activado para " + template.getId());
            
            for (Skill skill : template.getSkills()) {
                String skillTrigger = skill.getTrigger().toLowerCase();
                boolean match = skillTrigger.equalsIgnoreCase(triggerName);
                
                // Soporte para triggers parametrizados (onSignal:x)
                if (!match && triggerName.contains(":") && skillTrigger.equalsIgnoreCase(triggerName.split(":")[0])) {
                    match = true; 
                }

                if (match) {
                    PdcEntitiesAddon.debug("  - Ejecutando skill vinculada a " + triggerName);
                    skill.cast(entity);
                }
            }
        } finally {
            executing = false;
        }
    }

    public LivingEntity getEntity() { return entity; }
    public PdcMobTemplate getTemplate() { return template; }
    public Map<String, Double> getVariables() {
        Map<String, Double> allVars = new HashMap<>(variables);
        allVars.putAll(persistentVariables);
        return allVars;
    }
    public void setVariable(String key, double val) { persistentVariables.put(key, val); }
    public Map<String, Double> getPersistentVariables() { return persistentVariables; }
}

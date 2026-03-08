package dev.itsrealperson.permadeath.pdcentities.script;

import dev.itsrealperson.permadeath.pdcentities.PdcEntitiesAddon;
import dev.itsrealperson.permadeath.pdcentities.script.mechanic.Mechanic;
import dev.itsrealperson.permadeath.pdcentities.script.targeter.Targeter;
import org.bukkit.entity.Entity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Skill {

    private final Mechanic mechanic;
    private final Targeter targeter;
    private final String trigger;
    private int triggerValue = 0;
    private final List<Condition> conditions;

    public Skill(Mechanic mechanic, Targeter targeter, String trigger, List<Condition> conditions) {
        this.mechanic = mechanic;
        this.targeter = targeter;
        this.trigger = trigger;
        this.conditions = conditions != null ? conditions : new ArrayList<>();
    }

    public int getTriggerValue() { return triggerValue; }
    public void setTriggerValue(int val) { this.triggerValue = val; }

    public void cast(Entity caster) {
        if (mechanic == null || targeter == null) {
            PdcEntitiesAddon.debug("§c[Skill] Fallo al castear: mechanic o targeter nulo.");
            return;
        }

        PdcMobInstance instance = MobManager.getInstance(caster);
        String mechName = mechanic.getClass().getSimpleName();
        
        PdcEntitiesAddon.debug("§e[Skill] Evaluando " + mechName + " para " + caster.getName());

        // 1. Chequear condiciones del lanzador primero (si no hay objetivo aún)
        SkillContext globalContext = new SkillContext(caster, null);
        if (!checkConditions(globalContext)) {
            return;
        }

        // 2. Buscar objetivos
        Collection<Entity> targets = targeter.getTargets(caster);
        if (!targets.isEmpty()) {
            PdcEntitiesAddon.debug("  §7- Objetivos encontrados: " + targets.size() + " entidades.");
            for (Entity target : targets) {
                SkillContext context = new SkillContext(caster, target);
                // Volver a chequear condiciones si dependen del objetivo (distancia, etc)
                if (checkConditions(context)) {
                    PdcEntitiesAddon.debug("  §a- Ejecutando " + mechName + " sobre " + target.getType());
                    if (instance != null) instance.logSkill(mechName + " -> " + target.getType());
                    mechanic.execute(context, java.util.Collections.singletonList(target));
                }
            }
            return;
        }

        // 3. Intentar con Localizaciones
        Collection<org.bukkit.Location> locs = targeter.getTargetLocations(caster);
        if (!locs.isEmpty()) {
            PdcEntitiesAddon.debug("  §7- Localizaciones encontradas: " + locs.size());
            if (checkConditions(globalContext)) {
                PdcEntitiesAddon.debug("  §a- Ejecutando " + mechName + " en localizaciones.");
                if (instance != null) instance.logSkill(mechName + " -> Loc (" + locs.size() + ")");
                mechanic.executeAtLocations(globalContext, locs);
            }
        } else {
            PdcEntitiesAddon.debug("  §c- No se encontraron objetivos para " + mechName);
        }
    }

    private boolean checkConditions(SkillContext context) {
        for (Condition cond : conditions) {
            if (!cond.check(context)) {
                PdcEntitiesAddon.debug("  §6[Condición] Falló: " + cond.getClass().getSimpleName());
                return false;
            }
        }
        return true;
    }

    public String getTrigger() {
        return trigger;
    }

    public Mechanic getMechanic() {
        return mechanic;
    }
}

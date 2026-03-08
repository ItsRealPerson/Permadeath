package dev.itsrealperson.permadeath.util.script;

import dev.itsrealperson.permadeath.api.script.ScriptAction;
import dev.itsrealperson.permadeath.api.script.ScriptCondition;
import dev.itsrealperson.permadeath.api.script.ScriptContext;
import dev.itsrealperson.permadeath.api.script.ScriptTrigger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Representa un script cargado desde YAML con soporte de contexto y placeholders.
 */
public class Script {

    private final String id;
    private final ScriptTrigger trigger;
    private final List<ExecutableLine<ScriptCondition>> conditions = new ArrayList<>();
    private final List<ExecutableLine<ScriptAction>> actions = new ArrayList<>();

    public Script(String id, ScriptTrigger trigger) {
        this.id = id;
        this.trigger = trigger;
    }

    public void addCondition(ScriptCondition condition, Map<String, String> args) {
        conditions.add(new ExecutableLine<>(condition, args));
    }

    public void addAction(ScriptAction action, Map<String, String> args) {
        actions.add(new ExecutableLine<>(action, args));
    }

    public void execute(ScriptContext context) {
        // 1. Evaluar condiciones
        for (var cond : conditions) {
            if (!cond.executor().test(context, cond.args())) {
                return; // Si una falla, no ejecutamos nada.
            }
        }

        // 2. Ejecutar acciones
        for (var action : actions) {
            action.executor().execute(context, action.args());
        }
    }

    public String getId() { return id; }
    public ScriptTrigger getTrigger() { return trigger; }

    private record ExecutableLine<T>(T executor, Map<String, String> args) {}
}

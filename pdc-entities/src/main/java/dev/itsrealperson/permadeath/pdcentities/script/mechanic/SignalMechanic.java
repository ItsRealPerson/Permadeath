package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.MobManager;
import dev.itsrealperson.permadeath.pdcentities.script.PdcMobInstance;
import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.entity.Entity;
import java.util.Collection;
import java.util.Map;

public class SignalMechanic implements Mechanic {

    private final String signal;

    public SignalMechanic(Map<String, String> args) {
        this.signal = args.getOrDefault("id", "default_signal");
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        for (Entity target : targets) {
            PdcMobInstance instance = MobManager.getInstance(target);
            if (instance != null) {
                instance.executeTrigger("onSignal:" + signal);
            }
        }
    }
}

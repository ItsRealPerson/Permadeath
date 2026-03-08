package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.MobManager;
import dev.itsrealperson.permadeath.pdcentities.script.PdcMobInstance;
import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import dev.itsrealperson.permadeath.util.TextUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import java.util.Collection;
import java.util.Map;

public class SpeakMechanic implements Mechanic {
    private final String message;

    public SpeakMechanic(Map<String, String> args) {
        this.message = args.getOrDefault("m", args.getOrDefault("msg", ""));
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        if (message.isEmpty()) return;
        
        String finalMsg = message;
        PdcMobInstance instance = MobManager.getInstance(context.getCaster());
        if (instance != null) {
            finalMsg = instance.replacePlaceholders(message);
        }
        
        String formatted = TextUtils.format("&7[&f" + context.getCaster().getName() + "&7] &f" + finalMsg);
        for (Entity target : targets) {
            if (target instanceof Player p) {
                p.sendMessage(formatted);
            }
        }
    }
}

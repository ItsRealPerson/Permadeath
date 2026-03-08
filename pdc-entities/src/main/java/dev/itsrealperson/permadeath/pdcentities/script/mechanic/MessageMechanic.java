package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.MobManager;
import dev.itsrealperson.permadeath.pdcentities.script.PdcMobInstance;
import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import dev.itsrealperson.permadeath.util.TextUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import java.util.Collection;
import java.util.Map;

public class MessageMechanic implements Mechanic {
    private final String message;

    public MessageMechanic(Map<String, String> args) {
        this.message = args.getOrDefault("msg", "¡Cuidado!");
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        String finalMsg = message;
        
        PdcMobInstance instance = MobManager.getInstance(context.getCaster());
        if (instance != null) {
            finalMsg = instance.replacePlaceholders(message);
        } else {
            finalMsg = message
                .replace("%mob%", context.getCaster().getName())
                .replace("%target%", context.getTarget() != null ? context.getTarget().getName() : "desconocido");
        }
            
        String formatted = TextUtils.format(finalMsg);
        for (Entity target : targets) {
            if (target instanceof Player player) {
                player.sendMessage(formatted);
            }
        }
    }
}

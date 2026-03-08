package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.MobManager;
import dev.itsrealperson.permadeath.pdcentities.script.PdcMobInstance;
import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import dev.itsrealperson.permadeath.util.TextUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import java.util.Collection;
import java.util.Map;

public class ActionBarMechanic implements Mechanic {
    private final String message;

    public ActionBarMechanic(Map<String, String> args) {
        this.message = args.getOrDefault("m", args.getOrDefault("msg", ""));
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        if (message.isEmpty()) return;

        PdcMobInstance instance = MobManager.getInstance(context.getCaster());
        String finalMsg = (instance != null) ? instance.replacePlaceholders(message) : message;

        for (Entity target : targets) {
            if (target instanceof Player p) {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(TextUtils.format(finalMsg)));
            }
        }
    }
}

package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.MobManager;
import dev.itsrealperson.permadeath.pdcentities.script.PdcMobInstance;
import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import dev.itsrealperson.permadeath.util.TextUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import java.util.Collection;
import java.util.Map;

public class TitleMechanic implements Mechanic {
    private final String title;
    private final String subtitle;
    private final int fadeIn, stay, fadeOut;

    public TitleMechanic(Map<String, String> args) {
        this.title = args.getOrDefault("t", "");
        this.subtitle = args.getOrDefault("s", "");
        this.fadeIn = Integer.parseInt(args.getOrDefault("fi", "10"));
        this.stay = Integer.parseInt(args.getOrDefault("st", "40"));
        this.fadeOut = Integer.parseInt(args.getOrDefault("fo", "10"));
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        PdcMobInstance instance = MobManager.getInstance(context.getCaster());
        
        String fTitle = (instance != null) ? instance.replacePlaceholders(title) : title;
        String fSubtitle = (instance != null) ? instance.replacePlaceholders(subtitle) : subtitle;

        for (Entity target : targets) {
            if (target instanceof Player p) {
                p.sendTitle(TextUtils.format(fTitle), TextUtils.format(fSubtitle), fadeIn, stay, fadeOut);
            }
        }
    }
}

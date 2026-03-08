package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.Collection;
import java.util.Map;

public class PotionMechanic implements Mechanic {

    private final PotionEffectType type;
    private final int duration;
    private final int level;

    public PotionMechanic(Map<String, String> args) {
        this.type = PotionEffectType.getByName(args.getOrDefault("type", "POISON").toUpperCase());
        this.duration = Integer.parseInt(args.getOrDefault("duration", "100"));
        this.level = Integer.parseInt(args.getOrDefault("level", "1")) - 1;
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        if (type == null) return;
        PotionEffect effect = new PotionEffect(type, duration, level);
        for (Entity target : targets) {
            if (target instanceof LivingEntity living) {
                living.addPotionEffect(effect);
            }
        }
    }
}

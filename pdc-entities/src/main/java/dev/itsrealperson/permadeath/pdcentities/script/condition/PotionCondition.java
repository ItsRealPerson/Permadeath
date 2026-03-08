package dev.itsrealperson.permadeath.pdcentities.script.condition;

import dev.itsrealperson.permadeath.pdcentities.script.Condition;
import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;
import java.util.Map;

public class PotionCondition implements Condition {
    private final PotionEffectType type;
    private final boolean target;

    public PotionCondition(Map<String, String> args) {
        this.type = PotionEffectType.getByName(args.getOrDefault("type", "POISON").toUpperCase());
        this.target = Boolean.parseBoolean(args.getOrDefault("target", "false"));
    }

    @Override
    public boolean check(SkillContext context) {
        org.bukkit.entity.Entity ent = target ? context.getTarget() : context.getCaster();
        if (ent instanceof LivingEntity living) {
            return living.hasPotionEffect(type);
        }
        return false;
    }
}

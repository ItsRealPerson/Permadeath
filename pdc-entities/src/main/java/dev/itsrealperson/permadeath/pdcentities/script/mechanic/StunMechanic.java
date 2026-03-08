package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.Collection;
import java.util.Map;

public class StunMechanic implements Mechanic {
    private final int duration;

    public StunMechanic(Map<String, String> args) {
        this.duration = Integer.parseInt(args.getOrDefault("duration", "40"));
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        for (Entity target : targets) {
            if (target instanceof LivingEntity living) {
                living.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration, 10));
                living.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, duration, 200));
            }
        }
    }
}

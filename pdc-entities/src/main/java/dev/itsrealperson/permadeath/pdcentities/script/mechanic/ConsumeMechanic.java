package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import java.util.Collection;
import java.util.Map;

public class ConsumeMechanic implements Mechanic {
    private final double healAmount;

    public ConsumeMechanic(Map<String, String> args) {
        this.healAmount = Double.parseDouble(args.getOrDefault("heal", "10.0"));
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        if (!(context.getCaster() instanceof LivingEntity caster)) return;

        for (Entity target : targets) {
            if (target instanceof LivingEntity living && target != caster) {
                living.setHealth(0); // Matar objetivo
                caster.setHealth(Math.min(caster.getMaxHealth(), caster.getHealth() + healAmount));
                
                target.getWorld().spawnParticle(Particle.SOUL, target.getLocation(), 20, 0.5, 0.5, 0.5, 0.1);
                target.getWorld().playSound(target.getLocation(), Sound.ENTITY_GENERIC_EAT, 1.0f, 0.5f);
            }
        }
    }
}

package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.Skill;
import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import dev.itsrealperson.permadeath.pdcentities.script.SkillRegistry;
import org.bukkit.entity.Entity;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomSkillMechanic implements Mechanic {
    private final String[] skillIds;
    private final Random random = new Random();

    public RandomSkillMechanic(Map<String, String> args) {
        String list = args.getOrDefault("s", args.getOrDefault("skills", ""));
        this.skillIds = list.split(",");
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        if (skillIds.length == 0) return;
        
        String chosenId = skillIds[random.nextInt(skillIds.length)].trim();
        List<Skill> subSkills = SkillRegistry.getSkill(chosenId);
        
        if (subSkills != null) {
            executeList(context.getCaster(), subSkills, 0);
        }
    }

    private void executeList(org.bukkit.entity.Entity caster, List<Skill> skills, int index) {
        for (int i = index; i < skills.size(); i++) {
            Skill s = skills.get(i);
            
            if (s.getMechanic() instanceof DelayMechanic delay) {
                int nextIndex = i + 1;
                caster.getScheduler().runDelayed(
                    org.bukkit.Bukkit.getPluginManager().getPlugin("Permadeath"),
                    task -> {
                        if (caster.isValid()) executeList(caster, skills, nextIndex);
                    }, null, delay.getTicks());
                return;
            }
            
            s.cast(caster);
        }
    }
}

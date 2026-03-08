package dev.itsrealperson.permadeath.pdcentities.script.condition;

import dev.itsrealperson.permadeath.pdcentities.script.Condition;
import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import java.util.Map;

public class InBlockCondition implements Condition {
    private final Material material;

    public InBlockCondition(Map<String, String> args) {
        this.material = Material.valueOf(args.getOrDefault("m", "WATER").toUpperCase());
    }

    @Override
    public boolean check(SkillContext context) {
        return context.getCaster().getLocation().getBlock().getType() == material;
    }
}

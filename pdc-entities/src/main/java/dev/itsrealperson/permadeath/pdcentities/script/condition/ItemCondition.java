package dev.itsrealperson.permadeath.pdcentities.script.condition;

import dev.itsrealperson.permadeath.pdcentities.script.Condition;
import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import java.util.Map;

public class ItemCondition implements Condition {
    private final Material material;
    private final boolean handOnly;

    public ItemCondition(Map<String, String> args, boolean handOnly) {
        this.material = Material.valueOf(args.getOrDefault("i", "AIR").toUpperCase());
        this.handOnly = handOnly;
    }

    @Override
    public boolean check(SkillContext context) {
        if (context.getTarget() instanceof Player p) {
            if (handOnly) {
                return p.getInventory().getItemInMainHand().getType() == material ||
                       p.getInventory().getItemInOffHand().getType() == material;
            } else {
                return p.getInventory().contains(material);
            }
        }
        return false;
    }
}

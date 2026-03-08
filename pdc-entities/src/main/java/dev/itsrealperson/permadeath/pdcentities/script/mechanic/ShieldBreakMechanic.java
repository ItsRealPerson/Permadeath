package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import java.util.Collection;
import java.util.Map;

public class ShieldBreakMechanic implements Mechanic {
    private final int cooldown;

    public ShieldBreakMechanic(Map<String, String> args) {
        this.cooldown = Integer.parseInt(args.getOrDefault("c", "100")); // 5 segundos por defecto
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        for (Entity target : targets) {
            if (target instanceof Player p && p.isBlocking()) {
                p.setCooldown(Material.SHIELD, cooldown);
                // Forzar a bajar el escudo
                p.clearActiveItem();
                p.getWorld().playSound(p.getLocation(), org.bukkit.Sound.ITEM_SHIELD_BREAK, 1.0f, 0.8f);
            }
        }
    }
}

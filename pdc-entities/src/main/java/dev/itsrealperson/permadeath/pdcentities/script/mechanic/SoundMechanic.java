package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import java.util.Collection;
import java.util.Map;

public class SoundMechanic implements Mechanic {
    private final Sound sound;
    private final float volume;
    private final float pitch;

    public SoundMechanic(Map<String, String> args) {
        this.sound = Sound.valueOf(args.getOrDefault("s", "ENTITY_ZOMBIE_AMBIENT").toUpperCase());
        this.volume = Float.parseFloat(args.getOrDefault("v", "1.0"));
        this.pitch = Float.parseFloat(args.getOrDefault("p", "1.0"));
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        for (Entity target : targets) {
            if (target instanceof Player p) {
                p.playSound(target.getLocation(), sound, volume, pitch);
            }
        }
    }
}

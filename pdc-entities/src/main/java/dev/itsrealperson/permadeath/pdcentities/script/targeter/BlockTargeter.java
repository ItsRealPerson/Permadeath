package dev.itsrealperson.permadeath.pdcentities.script.targeter;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class BlockTargeter implements Targeter {
    private final double x, y, z;
    private final boolean relative;

    public BlockTargeter(Map<String, String> args) {
        this.x = Double.parseDouble(args.getOrDefault("x", "0"));
        this.y = Double.parseDouble(args.getOrDefault("y", "0"));
        this.z = Double.parseDouble(args.getOrDefault("z", "0"));
        this.relative = Boolean.parseBoolean(args.getOrDefault("rel", "true"));
    }

    @Override
    public Collection<Location> getTargetLocations(Entity caster) {
        if (relative) {
            return Collections.singletonList(caster.getLocation().add(x, y, z));
        } else {
            return Collections.singletonList(new Location(caster.getWorld(), x, y, z));
        }
    }
}

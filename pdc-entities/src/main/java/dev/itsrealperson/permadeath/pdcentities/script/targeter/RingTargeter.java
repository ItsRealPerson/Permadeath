package dev.itsrealperson.permadeath.pdcentities.script.targeter;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class RingTargeter implements Targeter {
    private final double radius;
    private final int points;

    public RingTargeter(Map<String, String> args) {
        this.radius = Double.parseDouble(args.getOrDefault("r", "5.0"));
        this.points = Integer.parseInt(args.getOrDefault("points", "8"));
    }

    @Override
    public Collection<Location> getTargetLocations(Entity caster) {
        Collection<Location> locs = new ArrayList<>();
        Location center = caster.getLocation();
        
        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            locs.add(center.clone().add(x, 0, z));
        }
        return locs;
    }
}

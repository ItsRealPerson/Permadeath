package tech.sebazcrc.permadeath.nms.v1_21_R3.utils;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class ParticleUtils {

    public static void drawCircle(Location center, double radius, Particle particle) {
        for (double t = 0; t < Math.PI * 2; t += 0.5) {
            double x = radius * Math.cos(t);
            double z = radius * Math.sin(t);
            center.add(x, 0, z);
            center.getWorld().spawnParticle(particle, center, 1, 0, 0, 0, 0);
            center.subtract(x, 0, z);
        }
    }

    public static void trailEntity(Entity entity, Particle particle) {
        entity.getWorld().spawnParticle(particle, entity.getLocation().add(0, 1, 0), 2, 0.2, 0.2, 0.2, 0.01);
    }

    /**
     * Dibuja una línea de partículas entre dos puntos.
     * Útil para simular el rastro de olor o rayos sónicos.
     */
    public static void drawLine(Location start, Location end, Particle particle, int count) {
        if (start == null || end == null) return;
        double distance = start.distance(end);
        Vector vector = end.toVector().subtract(start.toVector()).normalize();
        for (double i = 0; i < distance; i += 0.5) {
            start.add(vector.multiply(i));
            start.getWorld().spawnParticle(particle, start, count, 0, 0, 0, 0);
            start.subtract(vector.multiply(i));
        }
    }
}

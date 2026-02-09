package dev.itsrealperson.permadeath.nms.v1_21_R3.utils;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

public class SpawnUtils {

    /**
     * Reproduce efectos visuales y sonoros en la ubicación de spawn.
     */
    public static void playSpawnEffects(Location loc) {
        // Sonido de explosión mágica
        loc.getWorld().playSound(loc, Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1.0f, 0.5f);
        // Partículas de humo grande
        loc.getWorld().spawnParticle(Particle.LARGE_SMOKE, loc.add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.05);
        // Partículas de llama
        loc.getWorld().spawnParticle(Particle.FLAME, loc, 15, 0.3, 0.5, 0.3, 0.02);
    }

    /**
     * Spawnea un rayo falso (efecto visual) en la ubicación.
     */
    public static void strikeFakeLightning(Location loc) {
        loc.getWorld().strikeLightningEffect(loc);
    }
}

package tech.sebazcrc.permadeath.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import tech.sebazcrc.permadeath.Main;

import java.util.ArrayList;
import java.util.List;

public class SupernovaExplosionSimulator {

    /**
     * Simula matemáticamente una explosión masiva de Supernova manteniendo la estética
     * cúbica, tosca e irregular característica de Minecraft (sin lag, sin miles de rayos inútiles).
     */
    public static void simulate(Location center, float power, boolean breakBlocks, boolean setFire, LivingEntity cat, Plugin plugin) {
        if (center == null || center.getWorld() == null) return;
        final World world = center.getWorld();

        // 1. Efectos audiovisuales de clímax masivo
        world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 100.0F, 0.5F);
        world.playSound(center, Sound.ENTITY_WITHER_SPAWN, 50.0F, 0.8F);
        world.playSound(center, Sound.ITEM_TRIDENT_THUNDER, 80.0F, 0.6F);

        world.spawnParticle(Particle.EXPLOSION_EMITTER, center, 8, 3.0, 3.0, 3.0, 0.1);
        world.spawnParticle(Particle.FLASH, center, 4, 1.0, 1.0, 1.0, 0.1);

        // 2. Cálculo de dimensiones del cráter basado en potencia nominal
        // Con power = 200, genera un cráter tosco de ~70-80 bloques de diámetro
        final int radiusH = Math.max(16, (int) Math.min(42.0, power * 0.20));
        final int radiusDown = (int) (radiusH * 0.52); // ~22 bloques de profundidad
        final int radiusUp = (int) (radiusH * 0.90);

        final int cX = center.getBlockX();
        final int cY = center.getBlockY();
        final int cZ = center.getBlockZ();

        // 3. Onda de choque cinemática y daño masivo a entidades (Radio 75m)
        final double shockwaveRadius = radiusH * 1.8;
        for (Entity e : world.getNearbyEntities(center, shockwaveRadius, shockwaveRadius, shockwaveRadius)) {
            if (!(e instanceof LivingEntity living) || e == cat) continue;

            double dist = e.getLocation().distance(center);
            if (dist > shockwaveRadius) continue;

            // Daño no lineal (letal en epicentro, decreciente hacia el borde)
            double normDist = dist / shockwaveRadius;
            double damage;
            if (dist <= 12.0) {
                damage = 350.0; // Muerte prácticamente garantizada en el núcleo
            } else {
                damage = Math.max(10.0, 250.0 * Math.pow(1.0 - normDist, 1.8));
            }

            // Aplicar daño
            living.damage(damage);

            // Impulso vectorial balístico radial hacia afuera y hacia arriba
            Vector push = e.getLocation().toVector().subtract(center.toVector());
            if (push.lengthSquared() > 0.001) {
                push.normalize();
            } else {
                push = new Vector(0, 1, 0);
            }
            double pushStrength = Math.max(0.4, 2.5 * (1.0 - normDist));
            push.multiply(pushStrength);
            push.setY(Math.min(1.4, push.getY() + 0.65));
            e.setVelocity(push);
        }

        if (!breakBlocks) return;

        // 4. Recopilación de bloques por capas Y para ejecución escalonada (sin lag)
        final int minY = Math.max(world.getMinHeight() + 1, cY - radiusDown);
        final int maxY = Math.min(world.getMaxHeight() - 1, cY + radiusUp);

        List<List<BlockCoordinate>> layers = new ArrayList<>();
        for (int y = maxY; y >= minY; y--) {
            List<BlockCoordinate> layer = new ArrayList<>();
            int dy = y - cY;
            double vertDist = dy <= 0 ? (double) Math.abs(dy) / radiusDown : (double) dy / radiusUp;

            for (int dx = -radiusH - 4; dx <= radiusH + 4; dx++) {
                for (int dz = -radiusH - 4; dz <= radiusH + 4; dz++) {
                    // Métrica Híbrida Cúbica de Minecraft (Euclídea + Chebyshev)
                    double euclidean = Math.sqrt(dx * dx + dz * dz);
                    double chebyshev = Math.max(Math.abs(dx), Math.abs(dz));
                    double horizDist = (0.55 * euclidean + 0.45 * chebyshev) / radiusH;

                    double dTotal = (horizDist * horizDist) + (vertDist * vertDist);

                    // Ruido hash pseudoaleatorio cuantizado por coordenadas para crear
                    // escalones pronunciados, dientes de roca y paredes imperfectas (tosqueo)
                    long hash = ((long) (cX + dx) * 3129871L) ^ ((long) (cZ + dz) * 618033L) ^ ((long) y * 1123L);
                    double noise = ((hash ^ (hash >>> 16)) & 0xFFFF) / 65535.0; // [0, 1]

                    // Modulación escalonada (genera desniveles de 2 a 4 bloques de golpe)
                    double step = (Math.floor(noise * 4.0) / 4.0 - 0.5) * 0.32;
                    double threshold = 1.0 + step;

                    if (dTotal <= threshold) {
                        layer.add(new BlockCoordinate(cX + dx, y, cZ + dz, dTotal, noise));
                    }
                }
            }
            if (!layer.isEmpty()) {
                layers.add(layer);
            }
        }

        // 5. Aplicación escalonada capa por capa (Time-Sliced) para mantener 20 TPS
        executeLayers(world, layers, setFire, 0, plugin, center);
    }

    private static void executeLayers(World world, List<List<BlockCoordinate>> layers, boolean setFire, int index, Plugin plugin, Location center) {
        if (index >= layers.size()) {
            return;
        }

        // Ejecutar 2 capas por tick para velocidad sin afectar el TPS
        int maxLayersThisTick = Math.min(layers.size(), index + 2);
        for (int i = index; i < maxLayersThisTick; i++) {
            List<BlockCoordinate> currentLayer = layers.get(i);
            for (BlockCoordinate coord : currentLayer) {
                Block b = world.getBlockAt(coord.x, coord.y, coord.z);
                Material mat = b.getType();

                // Proteger bloques indestructibles
                if (mat == Material.BEDROCK || mat == Material.BARRIER || mat == Material.END_PORTAL_FRAME
                        || mat == Material.END_PORTAL || mat == Material.NETHER_PORTAL) {
                    continue;
                }

                // Zona de fondo/muros del cráter: Vitrificación térmica y calcinado
                if (coord.dTotal > 0.88) {
                    // Cerca del borde del cráter: no borrar todo, vitrificar la roca expuesta
                    if (coord.noise > 0.82) {
                        b.setType(Material.MAGMA_BLOCK, false);
                    } else if (coord.noise > 0.74) {
                        b.setType(Material.CRYING_OBSIDIAN, false);
                    } else if (coord.noise > 0.60) {
                        b.setType(Material.COBBLESTONE, false);
                    } else if (coord.noise > 0.50) {
                        b.setType(Material.CRACKED_STONE_BRICKS, false);
                    } else {
                        b.setType(Material.AIR, false);
                    }
                } else {
                    // Núcleo del cráter: pulverizado total
                    b.setType(Material.AIR, false);
                }
            }
        }

        // Programar siguiente lote en el scheduler
        final int nextIndex = maxLayersThisTick;
        if (nextIndex < layers.size()) {
            if (Main.isRunningFolia()) {
                Bukkit.getRegionScheduler().runDelayed(plugin, center, t -> executeLayers(world, layers, setFire, nextIndex, plugin, center), 1L);
            } else {
                Bukkit.getScheduler().runTaskLater(plugin, () -> executeLayers(world, layers, setFire, nextIndex, plugin, center), 1L);
            }
        }
    }

    private static class BlockCoordinate {
        final int x, y, z;
        final double dTotal;
        final double noise;

        BlockCoordinate(int x, int y, int z, double dTotal, double noise) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.dTotal = dTotal;
            this.noise = noise;
        }
    }
}

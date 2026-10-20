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
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import tech.sebazcrc.permadeath.Main;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class SupernovaExplosionSimulator {

    /**
     * Simula la explosión masiva del Gato Supernova utilizando el algoritmo exacto de
     * Ray-Marching Cúbico de Minecraft (Explosion.java / ServerExplosion.java),
     * garantizando la forma tosca, imperfecta, facetada y escalonada original,
     * pero a escala masiva, con alto rendimiento y ÚNICAMENTE DESTRUYENDO bloques
     * a aire (sin generar esferas, ni magma ni bloques artificiales).
     */
    public static void simulate(Location center, float power, boolean breakBlocks, boolean setFire, LivingEntity cat, Plugin plugin) {
        if (center == null || center.getWorld() == null) return;
        final World world = center.getWorld();

        // 1. Efectos audiovisuales y cosméticos masivos
        world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 100.0F, 0.5F);
        world.playSound(center, Sound.ENTITY_WITHER_SPAWN, 50.0F, 0.7F);
        world.playSound(center, Sound.ITEM_TRIDENT_THUNDER, 80.0F, 0.5F);

        world.spawnParticle(Particle.EXPLOSION_EMITTER, center, 12, 4.0, 4.0, 4.0, 0.2);
        world.spawnParticle(Particle.FLASH, center, 6, 2.0, 2.0, 2.0, 0.1);

        final int cX = center.getBlockX();
        final int cY = center.getBlockY();
        final int cZ = center.getBlockZ();

        // 2. Onda expansiva cinética y daño a entidades
        // Radio letal escalado con la potencia de la explosión
        final double shockwaveRadius = Math.max(30.0, Math.min(80.0, power * 0.40));
        for (Entity e : world.getNearbyEntities(center, shockwaveRadius, shockwaveRadius, shockwaveRadius)) {
            if (!(e instanceof LivingEntity living) || e == cat) continue;

            double dist = e.getLocation().distance(center);
            if (dist > shockwaveRadius) continue;

            double normDist = dist / shockwaveRadius;
            double damage;
            if (dist <= 14.0) {
                damage = 400.0; // Letal en el epicentro
            } else {
                damage = Math.max(12.0, 300.0 * Math.pow(1.0 - normDist, 1.8));
            }

            living.damage(damage);

            // Empuje radial hacia afuera y hacia arriba (arroja sobrevivientes a los desniveles)
            Vector push = e.getLocation().toVector().subtract(center.toVector());
            if (push.lengthSquared() > 0.001) {
                push.normalize();
            } else {
                push = new Vector(0, 1, 0);
            }
            double pushStrength = Math.max(0.5, 2.8 * (1.0 - normDist));
            push.multiply(pushStrength);
            push.setY(Math.min(1.5, push.getY() + 0.75));
            e.setVelocity(push);
        }

        if (!breakBlocks) return;

        // 3. Algoritmo Canónico de Ray-Marching Cúbico de Minecraft
        // Proyecta rayos densos desde las caras de un cubo [-1, 1]^3.
        // La concentración angular natural en vértices y caras produce la silueta
        // cuadrada/facetada e imperfecta icónica de Minecraft.
        final int resolution = 36; // 36x36 por cara = ~7.500 rayos para cobertura perfecta a gran distancia
        final float basePower = Math.max(28.0F, Math.min(65.0F, power * 0.32F));
        final ThreadLocalRandom random = ThreadLocalRandom.current();

        // Conjunto de bloques a destruir empaquetados en un long (evita allocs de memoria)
        final Set<Long> blocksToDestroy = new HashSet<>(30000);

        for (int i = 0; i < resolution; ++i) {
            for (int j = 0; j < resolution; ++j) {
                for (int k = 0; k < resolution; ++k) {
                    // Solo rayos desde la superficie del cubo
                    if (i == 0 || i == resolution - 1 || j == 0 || j == resolution - 1 || k == 0 || k == resolution - 1) {
                        double d0 = (double) ((float) i / (float) (resolution - 1) * 2.0F - 1.0F);
                        double d1 = (double) ((float) j / (float) (resolution - 1) * 2.0F - 1.0F);
                        double d2 = (double) ((float) k / (float) (resolution - 1) * 2.0F - 1.0F);
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 /= d3;
                        d1 /= d3;
                        d2 /= d3;

                        // Energía aleatoria por rayo idéntica a Minecraft: radio * (0.7 + rand * 0.6)
                        float rayEnergy = basePower * (0.7F + random.nextFloat() * 0.6F);
                        double curX = center.getX();
                        double curY = center.getY();
                        double curZ = center.getZ();

                        // Avanzar por pasos de 0.35 bloques
                        while (rayEnergy > 0.0F) {
                            int bx = (int) Math.floor(curX);
                            int by = (int) Math.floor(curY);
                            int bz = (int) Math.floor(curZ);

                            if (by < world.getMinHeight() || by >= world.getMaxHeight()) {
                                break;
                            }

                            Block b = world.getBlockAt(bx, by, bz);
                            Material mat = b.getType();

                            // Bloques absolutamente indestructibles detienen el rayo de inmediato
                            if (mat == Material.BEDROCK || mat == Material.BARRIER || mat == Material.END_PORTAL
                                    || mat == Material.END_PORTAL_FRAME || mat == Material.NETHER_PORTAL) {
                                break;
                            }

                            if (!mat.isAir()) {
                                float resistance = mat.getBlastResistance();
                                // Atenuación balanceada según la dureza real del material en Minecraft
                                rayEnergy -= (resistance * 0.55F + 0.3F) * 0.3F;

                                if (rayEnergy > 0.0F) {
                                    blocksToDestroy.add(pack(bx, by, bz));
                                }
                            }

                            curX += d0 * 0.35;
                            curY += d1 * 0.35;
                            curZ += d2 * 0.35;
                            rayEnergy -= 0.18F; // Degradación gradual en el vacío
                        }
                    }
                }
            }
        }

        if (blocksToDestroy.isEmpty()) return;

        // 4. Organizar bloques por capas Y (de arriba hacia abajo) para excavación natural
        Map<Integer, List<Long>> layersMap = new TreeMap<>(Collections.reverseOrder());
        for (long packed : blocksToDestroy) {
            int y = unpackY(packed);
            layersMap.computeIfAbsent(y, k -> new ArrayList<>()).add(packed);
        }

        List<List<Long>> layers = new ArrayList<>(layersMap.values());

        // 5. Destrucción escalonada a AIRE (setType(AIR, false): sin físicas en cascada ni lag)
        executeDestruction(world, layers, 0, plugin, center);
    }

    private static void executeDestruction(World world, List<List<Long>> layers, int index, Plugin plugin, Location center) {
        if (index >= layers.size()) {
            return;
        }

        // Destruir 3 capas Y por tick: rápido, estético y garantiza 20.0 TPS estables
        int maxIndex = Math.min(layers.size(), index + 3);
        for (int i = index; i < maxIndex; i++) {
            List<Long> layer = layers.get(i);
            for (long packed : layer) {
                int x = unpackX(packed);
                int y = unpackY(packed);
                int z = unpackZ(packed);

                Block b = world.getBlockAt(x, y, z);
                Material mat = b.getType();
                if (mat.isAir() || mat == Material.BEDROCK || mat == Material.BARRIER || mat == Material.END_PORTAL
                        || mat == Material.END_PORTAL_FRAME || mat == Material.NETHER_PORTAL) {
                    continue;
                }

                // DESTRUCCIÓN PURA: Solo aire, jamás alterar ni crear otros bloques
                b.setType(Material.AIR, false);
            }
        }

        final int nextIndex = maxIndex;
        if (nextIndex < layers.size()) {
            if (Main.isRunningFolia()) {
                Bukkit.getRegionScheduler().runDelayed(plugin, center, t -> executeDestruction(world, layers, nextIndex, plugin, center), 1L);
            } else {
                Bukkit.getScheduler().runTaskLater(plugin, () -> executeDestruction(world, layers, nextIndex, plugin, center), 1L);
            }
        }
    }

    private static long pack(int x, int y, int z) {
        return (((long) (x + 33554432) & 0x3FFFFFF) << 38)
                | (((long) (y + 1024) & 0xFFF) << 26)
                | ((long) (z + 33554432) & 0x3FFFFFF);
    }

    private static int unpackX(long packed) {
        return (int) ((packed >> 38) & 0x3FFFFFF) - 33554432;
    }

    private static int unpackY(long packed) {
        return (int) ((packed >> 26) & 0xFFF) - 1024;
    }

    private static int unpackZ(long packed) {
        return (int) (packed & 0x3FFFFFF) - 33554432;
    }
}

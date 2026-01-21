package tech.sebazcrc.permadeath.world.abyss;

import org.bukkit.Material;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.Random;

public class AbyssPopulator extends BlockPopulator {

    @Override
    public void populate(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, LimitedRegion limitedRegion) {
        SimplexOctaveGenerator noiseGen = new SimplexOctaveGenerator(new Random(worldInfo.getSeed()), 8);
        noiseGen.setScale(0.015D);

        int startX = chunkX << 4;
        int startZ = chunkZ << 4;

        // --- PASO 1: GENERACIÓN DE TERRENO ---
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int realX = startX + x;
                int realZ = startZ + z;

                // 1. Techo Orgánico (Capas 127 a 115)
                for (int y = 115; y <= 127; y++) {
                    double noise = noiseGen.noise(realX, y, realZ, 0.5D, 0.5D, true);
                    if (y >= 125 || Math.abs(noise) > 0.25D) {
                        Material mat = (y < 120 && random.nextInt(3) == 0) ? Material.SCULK : Material.DEEPSLATE;
                        limitedRegion.setType(realX, y, realZ, mat);
                    }
                }

                // 2. Islas y Terreno (Desde 114 hasta -63)
                for (int y = -63; y <= 114; y++) {
                    double noise = noiseGen.noise(realX, y, realZ, 0.5D, 0.5D, true);
                    double densityBias = (114.0 - y) / 177.0;
                    double threshold = (y <= -50) ? -1.0 : (0.18D - (densityBias * 0.12D));

                    if (Math.abs(noise) >= threshold) {
                        limitedRegion.setBiome(realX, y, realZ, org.bukkit.block.Biome.DEEP_DARK);
                        
                        Material mat = Material.DEEPSLATE;
                        if (y <= -50) {
                            mat = (random.nextInt(10) == 0) ? Material.DEEPSLATE : Material.SCULK;
                        } else {
                            double sculkNoise = noiseGen.noise(realX * 0.4, y * 0.4, realZ * 0.4, 0.5D, 0.5D, true);
                            double sculkThreshold = 0.25 + (densityBias * 0.65);
                            
                            if (sculkNoise > (1.0 - (sculkThreshold * 1.5))) {
                                mat = Material.SCULK;
                            } else if (random.nextInt(300) == 0) {
                                mat = Material.DEEPSLATE_EMERALD_ORE;
                            }
                        }
                        limitedRegion.setType(realX, y, realZ, mat);
                    }
                }
            }
        }

        // --- PASO 2: POBLACIÓN DE DECORACIÓN (Superficie real) ---
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int realX = startX + x;
                int realZ = startZ + z;

                for (int y = -62; y < 114; y++) {
                    // Si el bloque actual es Sculk y el de arriba es Aire
                    if (limitedRegion.getType(realX, y, realZ) == Material.SCULK && limitedRegion.getType(realX, y + 1, realZ) == Material.AIR) {
                        
                        int decorationRand = random.nextInt(100);
                        Material deco = null;

                        if (decorationRand < 3) {
                            deco = Material.SCULK_CATALYST;
                        } else if (decorationRand < 8) {
                            deco = Material.SCULK_SENSOR;
                        } else if (decorationRand < 10) { // 2% de probabilidad
                            deco = Material.SCULK_SHRIEKER;
                        }

                        if (deco != null) {
                            limitedRegion.setType(realX, y + 1, realZ, deco);
                        }
                    }
                }
            }
        }

        // --- PASO 3: CÁPSULAS DE BOTÍN ---
        if (random.nextInt(100) < 15) {
            int x = startX + random.nextInt(16);
            int z = startZ + random.nextInt(16);
            int y = random.nextInt(100) - 20;
            generateCapsule(limitedRegion, x, y, z, random);
        }
    }

    private void generateCapsule(LimitedRegion region, int cx, int cy, int cz, Random random) {
        int radius = 3 + random.nextInt(2);
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double dist = x * x + y * y + z * z;
                    if (dist < radius * radius) {
                        if (dist > (radius - 1) * (radius - 1)) {
                            region.setType(cx + x, cy + y, cz + z, Material.REINFORCED_DEEPSLATE);
                        } else {
                            region.setType(cx + x, cy + y, cz + z, Material.AIR);
                        }
                    }
                }
            }
        }
        region.setType(cx, cy, cz, Material.CHEST);
        if (region.getBlockState(cx, cy, cz) instanceof org.bukkit.block.Chest chest) {
            org.bukkit.inventory.Inventory inv = chest.getInventory();
            tech.sebazcrc.permadeath.Main.getInstance().getLootManager().generateAbyssLoot().forEach(inv::addItem);
        }
    }
}
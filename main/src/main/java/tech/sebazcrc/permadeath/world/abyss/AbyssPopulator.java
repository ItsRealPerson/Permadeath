package tech.sebazcrc.permadeath.world.abyss;

import org.bukkit.Material;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class AbyssPopulator extends BlockPopulator {

    @Override
    public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion) {
        if (!worldInfo.getName().endsWith("permadeath_abyss") && !worldInfo.getName().endsWith("permadeath/abyss")) return;

        long seed = worldInfo.getSeed();
        SimplexOctaveGenerator noiseGen = new SimplexOctaveGenerator(seed, 8);
        noiseGen.setScale(0.02D);

        int startX = chunkX << 4;
        int startZ = chunkZ << 4;

        // 1. Generación de Terreno (Rango Extendido: -64 a 128)
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int realX = startX + x;
                int realZ = startZ + z;

                for (int y = -64; y <= 128; y++) {
                    // Base de Bedrock en la capa más baja (-64)
                    if (y == -64) {
                        limitedRegion.setType(realX, y, realZ, Material.BEDROCK);
                        continue;
                    }
                    
                    // Techo de Bedrock (119 a 128)
                    if (y >= 119 && y <= 128) {
                        limitedRegion.setType(realX, y, realZ, Material.BEDROCK);
                        continue;
                    }

                    // Suelo masivo de Deepslate (hasta la capa -40)
                    if (y > -64 && y <= -40) {
                        limitedRegion.setType(realX, y, realZ, Material.DEEPSLATE);
                        continue;
                    }

                    // Ruido para islas y cuevas (de -39 a 118)
                    double noise = noiseGen.noise(realX, y, realZ, 0.5D, 0.5D, true);
                    
                    // Plataforma de Spawn en el centro (capa -35 por ejemplo)
                    boolean isSpawn = (Math.abs(realX) < 6 && Math.abs(realZ) < 6 && y == -35);

                    if (Math.abs(noise) >= 0.15D || isSpawn) {
                        Material mat = (random.nextInt(100) == 0) ? Material.DEEPSLATE_EMERALD_ORE : Material.DEEPSLATE;
                        if (isSpawn) mat = Material.SCULK;
                        limitedRegion.setType(realX, y, realZ, mat);
                    }
                }
            }
        }

        // 2. Decoración con Sculk (Solo en superficies expuestas)
        SimplexOctaveGenerator sculkNoise = new SimplexOctaveGenerator(seed + 1, 4);
        sculkNoise.setScale(0.05D);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int realX = startX + x;
                int realZ = startZ + z;
                for (int y = -63; y < 118; y++) {
                    if (limitedRegion.getType(realX, y, realZ) == Material.DEEPSLATE) {
                        if (limitedRegion.getType(realX, y + 1, realZ) == Material.AIR) {
                            double noise = sculkNoise.noise(realX, y, realZ, 0.5D, 0.5D, true);
                            if (noise > -0.2D) {
                                limitedRegion.setType(realX, y, realZ, Material.SCULK);
                                if (random.nextInt(100) < 5) limitedRegion.setType(realX, y + 1, realZ, Material.SCULK_CATALYST);
                            }
                        }
                    }
                }
            }
        }
    }
}
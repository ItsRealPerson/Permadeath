package tech.sebazcrc.permadeath.world.abyss.generator;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DeepDarkAbyssGenerator extends ChunkGenerator {

    @Override
    public List<org.bukkit.generator.BlockPopulator> getDefaultPopulators(@NotNull World world) {
        List<org.bukkit.generator.BlockPopulator> populators = new ArrayList<>();
        populators.add(new org.bukkit.generator.BlockPopulator() {
            @Override
            public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull org.bukkit.generator.LimitedRegion region) {
                if (random.nextInt(100) < 5) {
                    int x = (chunkX << 4) + random.nextInt(16);
                    int z = (chunkZ << 4) + random.nextInt(16);
                    int y = random.nextInt(60) + 20;
                    generateCapsule(region, x, y, z, random);
                }
            }

            private void generateCapsule(org.bukkit.generator.LimitedRegion region, int cx, int cy, int cz, Random random) {
                int radius = 3 + random.nextInt(2);
                for (int x = -radius; x <= radius; x++) {
                    for (int y = -radius; y <= radius; y++) {
                        for (int z = -radius; z <= radius; z++) {
                            double dist = x * x + y * y + z * z;
                            if (dist < radius * radius) {
                                if (dist > (radius - 1) * (radius - 1)) region.setType(cx + x, cy + y, cz + z, Material.REINFORCED_DEEPSLATE);
                                else region.setType(cx + x, cy + y, cz + z, Material.AIR);
                            }
                        }
                    }
                }
                region.setType(cx, cy, cz, Material.CHEST);
                if (region.getBlockState(cx, cy, cz) instanceof org.bukkit.block.Chest chest) {
                    tech.sebazcrc.permadeath.Main.getInstance().getLootManager().generateAbyssLoot().forEach(chest.getInventory()::addItem);
                }
            }
        });
        return populators;
    }

    @Override
    public boolean shouldGenerateStructures() { return false; }

    @Override
    public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
        SimplexOctaveGenerator generator = new SimplexOctaveGenerator(worldInfo.getSeed(), 8);
        generator.setScale(0.02D);

        int minHeight = worldInfo.getMinHeight();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int realX = (chunkX << 4) + x;
                int realZ = (chunkZ << 4) + z;

                chunkData.setBlock(x, minHeight, z, Material.BEDROCK);
                
                // 10 capas de Sculk sobre la Bedrock del suelo
                for (int sy = minHeight + 1; sy <= minHeight + 11; sy++) {
                    chunkData.setBlock(x, sy, z, Material.SCULK);
                }
                
                // Techo de 10 capas de Bedrock
                for (int ty = 119; ty <= 128; ty++) {
                    chunkData.setBlock(x, ty, z, Material.BEDROCK);
                }

                for (int y = minHeight + 12; y < 119; y++) {
                    if (y <= 19) {
                        chunkData.setBlock(x, y, z, Material.DEEPSLATE);
                        continue; 
                    }

                    double noise = generator.noise(realX, y, realZ, 0.5D, 0.5D, true);
                    if (Math.abs(noise) >= 0.12D) { 
                        chunkData.setBlock(x, y, z, Material.DEEPSLATE);
                    }
                }
            }
        }
    }

    @Override
    public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
        SimplexOctaveGenerator sculkNoise = new SimplexOctaveGenerator(worldInfo.getSeed() + 1, 4);
        sculkNoise.setScale(0.05D);

        int minHeight = worldInfo.getMinHeight();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int realX = (chunkX << 4) + x;
                int realZ = (chunkZ << 4) + z;

                // 1. Decoración sobre el suelo de Sculk (Y = minHeight + 11)
                if (random.nextInt(100) < 20) { // 20% de probabilidad de decoración en el suelo por chunk-column
                    applyDecoration(chunkData, x, minHeight + 12, z, random);
                }

                // 2. Decoración en cuevas
                for (int y = minHeight + 12; y < 119; y++) {
                    Material current = chunkData.getType(x, y, z);
                    if (current == Material.DEEPSLATE && isExposed(chunkData, x, y, z)) {
                        double noise = sculkNoise.noise(realX, y, realZ, 0.5D, 0.5D, true);
                        
                        // Generación unificada en todas las alturas
                        if (noise > -0.2D) {
                            chunkData.setBlock(x, y, z, Material.SCULK);
                            if (y < 118 && chunkData.getType(x, y + 1, z) == Material.AIR) {
                                applyDecoration(chunkData, x, y + 1, z, random);
                            }
                        }
                    }
                }
            }
        }
    }

    private void applyDecoration(ChunkData chunkData, int x, int y, int z, Random random) {
        if (y >= 128) return;
        int roll = random.nextInt(100);
        
        // Probabilidades unificadas para toda la dimensión
        if (roll < 5) chunkData.setBlock(x, y, z, Material.SCULK_CATALYST); // 5%
        else if (roll < 6) chunkData.setBlock(x, y, z, Material.SCULK_SHRIEKER); // 1% Chilladores
        else if (roll < 13) chunkData.setBlock(x, y, z, Material.SCULK_SENSOR); // 7% Sensores
    }

    private boolean isExposed(ChunkData data, int x, int y, int z) {
        if (y <= data.getMinHeight() || y >= data.getMaxHeight() - 1) return false;
        return (x > 0 && data.getType(x - 1, y, z) == Material.AIR) ||
               (x < 15 && data.getType(x + 1, y, z) == Material.AIR) ||
               (y > data.getMinHeight() && data.getType(x, y - 1, z) == Material.AIR) ||
               (y < data.getMaxHeight() - 1 && data.getType(x, y + 1, z) == Material.AIR) ||
               (z > 0 && data.getType(x, y, z - 1) == Material.AIR) ||
               (z < 15 && data.getType(x, y, z + 1) == Material.AIR);
    }

    @Override
    public boolean shouldGenerateCaves() { return true; }
}
package tech.sebazcrc.permadeath.world.abyss.generator;

import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class DeepDarkAbyssGenerator extends ChunkGenerator {

    @Override
    public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
        SimplexOctaveGenerator generator = new SimplexOctaveGenerator(worldInfo.getSeed(), 8);
        generator.setScale(0.015D);

        int minHeight = worldInfo.getMinHeight();
        int maxHeight = worldInfo.getMaxHeight();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int realX = (chunkX << 4) + x;
                int realZ = (chunkZ << 4) + z;

                chunkData.setBlock(x, minHeight, z, Material.BEDROCK);
                chunkData.setBlock(x, maxHeight - 1, z, Material.BEDROCK);

                for (int y = minHeight + 1; y < maxHeight - 1; y++) {
                    double noise = generator.noise(realX, y, realZ, 0.5D, 0.5D, true);
                    if (noise > 0.15D) {
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

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int realX = (chunkX << 4) + x;
                int realZ = (chunkZ << 4) + z;

                for (int y = worldInfo.getMinHeight() + 1; y < worldInfo.getMaxHeight() - 1; y++) {
                    Material current = chunkData.getType(x, y, z);
                    if (current == Material.DEEPSLATE) {
                        if (isExposed(chunkData, x, y, z)) {
                            double noise = sculkNoise.noise(realX, y, realZ, 0.5D, 0.5D, true);
                            if (noise > 0.4D) {
                                chunkData.setBlock(x, y, z, Material.SCULK);
                                if (random.nextInt(100) < 2) {
                                    chunkData.setBlock(x, y + 1, z, Material.SCULK_CATALYST);
                                } else if (random.nextInt(100) < 1) {
                                    chunkData.setBlock(x, y + 1, z, Material.SCULK_SHRIEKER);
                                }
                            }
                        }
                    }
                }
            }
        }
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










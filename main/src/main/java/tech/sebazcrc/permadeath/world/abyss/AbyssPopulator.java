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
        long seed = worldInfo.getSeed();
        SimplexOctaveGenerator noiseGen = new SimplexOctaveGenerator(seed, 8);
        noiseGen.setScale(0.02D);

        int startX = chunkX << 4;
        int startZ = chunkZ << 4;

        // 1. Suelo y Techo (Muy rápido con LimitedRegion)
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int realX = startX + x;
                int realZ = startZ + z;

                limitedRegion.setType(realX, -64, realZ, Material.BEDROCK);
                for (int y = -63; y <= -40; y++) {
                    limitedRegion.setType(realX, y, realZ, Material.DEEPSLATE);
                }
                for (int y = 119; y <= 128; y++) {
                    limitedRegion.setType(realX, y, realZ, Material.BEDROCK);
                }

                // 2. Islas
                for (int y = -39; y <= 118; y++) {
                    double noise = noiseGen.noise(realX, y, realZ, 0.5D, 0.5D, true);
                    boolean isSpawn = (Math.abs(realX) < 6 && Math.abs(realZ) < 6 && y == -35);

                    if (Math.abs(noise) >= 0.15D || isSpawn) {
                        Material mat = (random.nextInt(100) == 0) ? Material.DEEPSLATE_EMERALD_ORE : Material.DEEPSLATE;
                        if (isSpawn) mat = Material.SCULK;
                        limitedRegion.setType(realX, y, realZ, mat);
                    }
                }
            }
        }
    }
}

package tech.sebazcrc.permadeath.world.abyss.generator;

import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;
import tech.sebazcrc.permadeath.world.abyss.AbyssPopulator;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class EmptyGenerator extends ChunkGenerator {
    @Override
    public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int x, int z, @NotNull ChunkData chunkData) { }

    @Override
    public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int x, int z, @NotNull ChunkData chunkData) { }

    @Override
    public void generateBedrock(@NotNull WorldInfo worldInfo, @NotNull Random random, int x, int z, @NotNull ChunkData chunkData) { }

    @Override
    public @NotNull List<BlockPopulator> getDefaultPopulators(@NotNull World world) {
        return Collections.singletonList(new AbyssPopulator());
    }
}
package dev.itsrealperson.permadeath.world.beginning.generator;

import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BeginningGenerator extends ChunkGenerator {

    @Override
    public ChunkData generateChunkData(World world, Random cRandom, int chunkX, int chunkZ, BiomeGrid biomes) {
        return createChunkData(world);
    }

    @NotNull
    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return Collections.emptyList();
    }
}


















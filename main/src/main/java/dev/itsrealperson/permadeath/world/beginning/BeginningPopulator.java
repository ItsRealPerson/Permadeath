package dev.itsrealperson.permadeath.world.beginning;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.world.WorldEditPortal;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.SplittableRandom;

public class BeginningPopulator extends BlockPopulator {

    private static final int HEIGHT = 100;
    private final SplittableRandom splittableRandom = new SplittableRandom();

    @Override
    public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion) {
        long seed = worldInfo.getSeed();
        SimplexOctaveGenerator lowGenerator = new SimplexOctaveGenerator(new Random(seed), 8);
        lowGenerator.setScale(0.02D);

        int startX = chunkX << 4;
        int startZ = chunkZ << 4;
        
        // Añadimos un offset aleatorio basado en la semilla para que (0,0) no sea siempre vacío
        int offsetX = (int) (worldInfo.getSeed() % 100000);
        int offsetZ = (int) (worldInfo.getSeed() / 100000 % 100000);

        // 2. Islas de Purpur
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int realX = startX + x;
                int realZ = startZ + z;

                // Usamos el ruido con el offset
                int noise = (int) (lowGenerator.noise(realX + offsetX, realZ + offsetZ, 0.5D, 0.5D) * 15);

                if (noise <= 0) {
                    continue;
                }

                for (int i = 0; i < noise / 3; i++) limitedRegion.setType(realX, HEIGHT + i, realZ, Material.PURPUR_BLOCK);
                for (int i = 0; i < noise; i++) limitedRegion.setType(realX, HEIGHT - i - 1, realZ, Material.PURPUR_BLOCK);
            
                // 3. Generación de Estructuras NBT e Islas (Integrado)
                if (x == 8 && z == 8) { // Centro del Chunk
                    // Zona Segura para el Portal (Radio de 50 bloques)
                    if (Math.abs(realX) < 50 && Math.abs(realZ) < 50) continue;

                    // Probabilidad Ytic (1 cada 324 chunks aprox)
                    if (splittableRandom.nextInt(324) == 0) {
                        // Altura 4 para que con 96 bloques de base coincida con la capa 100
                        scheduleStructure(worldInfo, realX, 7, realZ, "ytic");
                    } 
                    // Probabilidad Isla NBT (1 cada 25 chunks aprox)
                    else if (splittableRandom.nextInt(25) == 0) {
                        int id = splittableRandom.nextInt(5) + 1;
                        // Altura 120 para que floten 20 bloques sobre el Purpur
                        scheduleStructure(worldInfo, realX, 120, realZ, "island" + id);
                    }
                    
                    // 4. Arboles (20% de probabilidad si no hubo estructura)
                    else if (splittableRandom.nextInt(5) == 0) {
                         scheduleTree(worldInfo, realX, realZ);
                    }
                }
            }
        }
    }

    private void scheduleStructure(WorldInfo worldInfo, int x, int y, int z, String name) {
        Bukkit.getGlobalRegionScheduler().runDelayed(Main.instance, t -> {
            World world = Bukkit.getWorld(worldInfo.getUID());
            if (world == null) return;

            Bukkit.getRegionScheduler().run(Main.instance, new org.bukkit.Location(world, x, y, z), task -> {
                WorldEditPortal.pasteStructure(new org.bukkit.Location(world, x, y, z), name);
            });
        }, 100L);
    }

    private void scheduleTree(WorldInfo worldInfo, int x, int z) {
        // En Folia, no podemos usar Bukkit.getWorld durante la generación inicial del chunk de forma segura
        Bukkit.getGlobalRegionScheduler().runDelayed(Main.instance, t -> {
            World world = Bukkit.getWorld(worldInfo.getUID());
            if (world == null) return;

            Bukkit.getRegionScheduler().run(Main.instance, new org.bukkit.Location(world, x, HEIGHT, z), task -> {
                generateTree(world, x, z);
            });
        }, 100L);
    }

    private void generateTree(World world, int x, int z) {
        int y = world.getHighestBlockYAt(x, z);
        
        // Solo generar si el suelo está cerca de la altura de las islas (100)
        // Esto evita que se generen árboles sobre el portal (150)
        if (y < 95 || y > 110) return; 

        world.generateTree(new org.bukkit.Location(world, x, y + 1, z), TreeType.CHORUS_PLANT, new BlockChangeDelegate() {
            @Override
            public boolean setBlockData(int i, int i1, int i2, @NotNull BlockData blockData) {
                if (blockData.getMaterial() == Material.CHORUS_FLOWER) {
                    world.getBlockAt(i, i1, i2).setType(Material.SEA_LANTERN);
                } else if (blockData.getMaterial() == Material.CHORUS_PLANT) {
                    world.getBlockAt(i, i1, i2).setType(Material.END_STONE_BRICK_WALL);
                }
                return true;
            }

            @Override
            public @NotNull BlockData getBlockData(int i, int i1, int i2) {
                return world.getBlockData(i, i1, i2);
            }

            @Override
            public int getHeight() {
                return 255;
            }

            @Override
            public boolean isEmpty(int i, int i1, int i2) {
                return world.getBlockAt(i, i1, i2).getType() == Material.AIR;
            }
        });
    }
}

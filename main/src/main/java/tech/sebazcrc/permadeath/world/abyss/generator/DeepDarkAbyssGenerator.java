package tech.sebazcrc.permadeath.world.abyss.generator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.inventory.ItemStack;
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
            public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull org.bukkit.generator.LimitedRegion limitedRegion) {
                if (random.nextInt(100) < 5) {
                    int x = (chunkX << 4) + random.nextInt(16);
                    int z = (chunkZ << 4) + random.nextInt(16);
                    int y = random.nextInt(60) + 20;

                    generateCapsule(limitedRegion, x, y, z, random);
                }
            }

            private void generateCapsule(org.bukkit.generator.LimitedRegion region, int cx, int cy, int cz, Random random) {
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
                    inv.addItem(tech.sebazcrc.permadeath.util.item.NetheriteArmor.craftAncestralFragment());
                    if (random.nextBoolean()) inv.addItem(new ItemStack(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE));
                    inv.addItem(new ItemStack(Material.ECHO_SHARD, random.nextInt(3) + 1));
                }
            }
        });
        return populators;
    }

    @Override
    public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
        SimplexOctaveGenerator generator = new SimplexOctaveGenerator(worldInfo.getSeed(), 8);
        generator.setScale(0.02D); // Escala ajustada para cuevas menos anchas

        int minHeight = worldInfo.getMinHeight();
        int maxHeight = worldInfo.getMaxHeight();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int realX = (chunkX << 4) + x;
                int realZ = (chunkZ << 4) + z;

                chunkData.setBlock(x, minHeight, z, Material.BEDROCK);
                
                // Techo de 10 capas de Bedrock (119 a 128)
                for (int ty = 119; ty <= 128; ty++) {
                    chunkData.setBlock(x, ty, z, Material.BEDROCK);
                }

                for (int y = minHeight + 1; y < 119; y++) {
                    // Capa solida de Deepslate (-63 a -20)
                    if (y >= -63 && y <= -20) {
                        chunkData.setBlock(x, y, z, Material.DEEPSLATE);
                        continue; 
                    }

                    // Generación de cuevas conectadas (Túneles/Spaghetti)
                    if (y > -20) {
                        double noise = generator.noise(realX, y, realZ, 0.5D, 0.5D, true);
                        
                        // Usamos valor absoluto para crear "túneles" donde el ruido se acerca a 0.
                        // Si Math.abs(noise) es bajo (ej < 0.15), es aire (cueva).
                        // Si es alto, es roca. Esto garantiza conectividad.
                        if (Math.abs(noise) >= 0.12D) { 
                            chunkData.setBlock(x, y, z, Material.DEEPSLATE);
                        }
                        // Si es < 0.12, dejamos Aire (cueva túnel)
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

                // Rango extendido hasta 128 (límite del techo)
                for (int y = worldInfo.getMinHeight() + 1; y < 128; y++) {
                    Material current = chunkData.getType(x, y, z);
                    
                    if (current == Material.DEEPSLATE) {
                        if (isExposed(chunkData, x, y, z)) {
                            double noise = sculkNoise.noise(realX, y, realZ, 0.5D, 0.5D, true);
                            // Más Sculk: umbral reducido de 0.4 a -0.2
                            if (noise > -0.2D) {
                                chunkData.setBlock(x, y, z, Material.SCULK);
                                
                                // Decoración
                                if (random.nextInt(100) < 5) { // 5% Catalizadores
                                    chunkData.setBlock(x, y + 1, z, Material.SCULK_CATALYST);
                                } else if (random.nextInt(200) < 1) { // Reducido a 0.5% (1 en 200)
                                    chunkData.setBlock(x, y + 1, z, Material.SCULK_SHRIEKER);
                                } else if (random.nextInt(100) < 10) {
                                    chunkData.setBlock(x, y + 1, z, Material.SCULK_SENSOR);
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










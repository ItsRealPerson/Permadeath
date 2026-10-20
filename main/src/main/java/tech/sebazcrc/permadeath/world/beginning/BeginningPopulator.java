package tech.sebazcrc.permadeath.world.beginning;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import org.jetbrains.annotations.NotNull;
import tech.sebazcrc.permadeath.Main;
import tech.sebazcrc.permadeath.world.WorldEditPortal;

import java.util.Random;
import java.util.SplittableRandom;

import org.bukkit.BlockChangeDelegate;
import org.bukkit.TreeType;
import org.bukkit.block.data.BlockData;

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

        // 2. Islas de Purpur
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int realX = startX + x;
                int realZ = startZ + z;

                int noise = (int) (lowGenerator.noise(realX, realZ, 0.5D, 0.5D) * 15);

                if (noise <= 0) {
                    if (Main.worldEditFound && Main.getInstance().isSmallIslandsEnabled() && x == 8 && z == 8) {
                        if (splittableRandom.nextInt(20) == 0) {
                            scheduleSchematic(worldInfo, realX, realZ, "island", splittableRandom);
                        }
                    }
                    continue;
                }

                int chance = Main.getInstance().getConfig().getInt("Toggles.TheBeginning.YticGenerateChance", 100000);
                // Fix: Config comment says value is divided by 256. Code was using raw value.
                if (chance >= 256) chance = chance / 256;
                if (chance < 1) chance = 1;

                if (splittableRandom.nextInt(chance) == 0 && x == 8 && z == 8) {
                    // Bukkit.getConsoleSender().sendMessage("[Permadeath-Debug] Scheduling Ytic at " + realX + ", " + realZ);
                    scheduleSchematic(worldInfo, realX, realZ, "ytic", null);
                }

                for (int i = 0; i < noise / 3; i++) limitedRegion.setType(realX, HEIGHT + i, realZ, Material.PURPUR_BLOCK);
                for (int i = 0; i < noise; i++) limitedRegion.setType(realX, HEIGHT - i - 1, realZ, Material.PURPUR_BLOCK);
            
                // 3. Arboles (Corrupted Chorus) - Logic imported from TreePopulator
                // Check top block
                int topY = HEIGHT + (noise / 3) - 1; 
                // TreePopulator checks 100-105. HEIGHT is 100. noise/3 is max 5 (15/3). So range is 100-104. Perfect.
                
                if (x == 8 && z == 8) { // Try to spawn 1 tree per chunk center if valid
                     if (splittableRandom.nextInt(5) == 0) { // 20% chance
                         scheduleSchematic(worldInfo, realX, realZ, "tree", null);
                     }
                }
            }
        }
    }

    private World resolveWorld(WorldInfo worldInfo) {
        World w = Bukkit.getWorld(worldInfo.getUID());
        if (w != null) return w;
        return Bukkit.getWorld(worldInfo.getName());
    }

    private void scheduleSchematic(WorldInfo worldInfo, int x, int z, String type, SplittableRandom random) {
        if (Main.isRunningFolia()) {
            Bukkit.getGlobalRegionScheduler().runDelayed(Main.instance, t -> {
                final World world = resolveWorld(worldInfo);
                if (world == null) return;

                Bukkit.getRegionScheduler().run(Main.instance, new org.bukkit.Location(world, x, HEIGHT, z), task -> {
                    if (type.equals("island")) {
                        WorldEditPortal.generateIsland(world, x, z, HEIGHT, random);
                    } else if (type.equals("ytic")) {
                        WorldEditPortal.generateYtic(world, x, z, HEIGHT);
                    } else if (type.equals("tree")) {
                        generateTree(world, x, z);
                    }
                });
            }, 40L);
        } else {
            Bukkit.getScheduler().runTaskLater(Main.instance, () -> {
                final World world = resolveWorld(worldInfo);
                if (world == null) return;
                if (type.equals("island")) {
                    WorldEditPortal.generateIsland(world, x, z, HEIGHT, random);
                } else if (type.equals("ytic")) {
                    WorldEditPortal.generateYtic(world, x, z, HEIGHT);
                } else if (type.equals("tree")) {
                    generateTree(world, x, z);
                }
            }, 40L);
        }
    }

    private void generateTree(World world, int x, int z) {
        int y = world.getHighestBlockYAt(x, z);
        if (y < HEIGHT) return; 

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

    public void populateExistingChunk(@NotNull org.bukkit.Chunk chunk) {
        World world = chunk.getWorld();
        long seed = world.getSeed();
        SimplexOctaveGenerator lowGenerator = new SimplexOctaveGenerator(new Random(seed), 8);
        lowGenerator.setScale(0.02D);

        int startX = chunk.getX() << 4;
        int startZ = chunk.getZ() << 4;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int realX = startX + x;
                int realZ = startZ + z;

                int noise = (int) (lowGenerator.noise(realX, realZ, 0.5D, 0.5D) * 15);

                if (noise <= 0) {
                    if (Main.worldEditFound && Main.getInstance().isSmallIslandsEnabled() && x == 8 && z == 8) {
                        if (Math.abs(realX) > 30 || Math.abs(realZ) > 30) {
                            if (splittableRandom.nextInt(20) == 0) {
                                scheduleSchematicDirect(world, realX, realZ, "island", splittableRandom);
                            }
                        }
                    }
                    continue;
                }

                int chance = Main.getInstance().getConfig().getInt("Toggles.TheBeginning.YticGenerateChance", 100000);
                if (chance >= 256) chance = chance / 256;
                if (chance < 1) chance = 1;

                if (splittableRandom.nextInt(chance) == 0 && x == 8 && z == 8) {
                    if (Math.abs(realX) > 40 || Math.abs(realZ) > 40) {
                        scheduleSchematicDirect(world, realX, realZ, "ytic", null);
                    }
                }

                for (int i = 0; i < noise / 3; i++) {
                    org.bukkit.block.Block b = chunk.getBlock(x, HEIGHT + i, z);
                    if (b.getType() == Material.AIR) {
                        b.setType(Material.PURPUR_BLOCK, false);
                    }
                }
                for (int i = 0; i < noise; i++) {
                    org.bukkit.block.Block b = chunk.getBlock(x, HEIGHT - i - 1, z);
                    if (b.getType() == Material.AIR) {
                        b.setType(Material.PURPUR_BLOCK, false);
                    }
                }
            
                if (x == 8 && z == 8) {
                    if (splittableRandom.nextInt(5) == 0) {
                        if (Math.abs(realX) > 20 || Math.abs(realZ) > 20) {
                            scheduleSchematicDirect(world, realX, realZ, "tree", null);
                        }
                    }
                }
            }
        }
    }

    private void scheduleSchematicDirect(World world, int x, int z, String type, SplittableRandom random) {
        if (Main.isRunningFolia()) {
            Bukkit.getRegionScheduler().run(Main.instance, new org.bukkit.Location(world, x, HEIGHT, z), task -> {
                if (type.equals("island")) {
                    WorldEditPortal.generateIsland(world, x, z, HEIGHT, random);
                } else if (type.equals("ytic")) {
                    WorldEditPortal.generateYtic(world, x, z, HEIGHT);
                } else if (type.equals("tree")) {
                    generateTree(world, x, z);
                }
            });
        } else {
            Bukkit.getScheduler().runTask(Main.instance, () -> {
                if (type.equals("island")) {
                    WorldEditPortal.generateIsland(world, x, z, HEIGHT, random);
                } else if (type.equals("ytic")) {
                    WorldEditPortal.generateYtic(world, x, z, HEIGHT);
                } else if (type.equals("tree")) {
                    generateTree(world, x, z);
                }
            });
        }
    }
}
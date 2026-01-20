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

        // 1. Suelo de Bedrock
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                limitedRegion.setType(startX + x, 0, startZ + z, Material.BEDROCK);
            }
        }

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
                if (splittableRandom.nextInt(chance) == 0 && x == 8 && z == 8) {
                    scheduleSchematic(worldInfo, realX, realZ, "ytic", null);
                }

                for (int i = 0; i < noise / 3; i++) limitedRegion.setType(realX, HEIGHT + i, realZ, Material.PURPUR_BLOCK);
                for (int i = 0; i < noise; i++) limitedRegion.setType(realX, HEIGHT - i - 1, realZ, Material.PURPUR_BLOCK);
            }
        }
    }

    private void scheduleSchematic(WorldInfo worldInfo, int x, int z, String type, SplittableRandom random) {
        // En Folia, no podemos usar Bukkit.getWorld durante la generación inicial del chunk de forma segura
        // Programamos una tarea global para que espere a que el mundo esté cargado
        Bukkit.getGlobalRegionScheduler().runDelayed(Main.instance, t -> {
            World world = Bukkit.getWorld(worldInfo.getUID());
            if (world == null) return;

            // Ahora que tenemos el mundo, usamos el region scheduler
            Bukkit.getRegionScheduler().run(Main.instance, new org.bukkit.Location(world, x, HEIGHT, z), task -> {
                if (type.equals("island")) {
                    WorldEditPortal.generateIsland(world, x, z, HEIGHT, random);
                } else if (type.equals("ytic")) {
                    WorldEditPortal.generateYtic(world, x, z, HEIGHT);
                }
            });
        }, 100L); // Esperar 5 segundos para asegurar que el mundo esté listo
    }
}
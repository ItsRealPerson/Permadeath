package tech.sebazcrc.permadeath.world;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import tech.sebazcrc.permadeath.Main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import java.util.SplittableRandom;

public class WorldEditPortal {

    public static void generateIsland(World world, int x, int z, int height, SplittableRandom random) {
        Clipboard clipboard;
        File file;

        switch (random.nextInt(6)) {
            case 0:
                file = new File(Main.getInstance().getDataFolder(), "schematics/island1.schem");
                break;
            case 1:
                file = new File(Main.getInstance().getDataFolder(), "schematics/island2.schem");
                break;
            case 2:
                file = new File(Main.getInstance().getDataFolder(), "schematics/island3.schem");
                break;
            case 3:
                file = new File(Main.getInstance().getDataFolder(), "schematics/island4.schem");
                break;
            default:
                file = new File(Main.getInstance().getDataFolder(), "schematics/island5.schem");
                break;
        }

        ClipboardFormat format = ClipboardFormats.findByFile(file);

        assert format != null;

        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            clipboard = reader.read();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(world), -1)) {
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(x, height + 20, z))
                    .ignoreAirBlocks(true)
                    .build();
            Operations.complete(operation);
        } catch (WorldEditException e) {
            e.printStackTrace();
        }
    }

    public static void generateYtic(World world, int x, int z, int height) {
        Clipboard clipboard;
        File file = new File(Main.getInstance().getDataFolder(), "schematics/ytic.schem");

        ClipboardFormat format = ClipboardFormats.findByFile(file);

        assert format != null;
        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            clipboard = reader.read();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(world), -1)) {
            ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard);

            Operation operation = clipboardHolder
                    .createPaste(editSession)
                    .to(BlockVector3.at(x, height + 34, z))
                    .ignoreAirBlocks(true)
                    .copyEntities(true)
                    .build();

            Operations.complete(operation);
            //editSession.replaceBlocks(
        } catch (WorldEditException e) {
            e.printStackTrace();
        }
    }
    public static void generatePortal(boolean overworld, Location to) {

        if (!Main.getInstance().getBeData().generatedOverWorldBeginningPortal() && overworld) {

            int x = Main.getInstance().getConfig().getInt("TheBeginning.X-Limit");
            int z = Main.getInstance().getConfig().getInt("TheBeginning.Z-Limit");

            int ranX = new Random().nextInt(x);
            int ranZ = new Random().nextInt(z);

            if (new Random().nextBoolean()) ranX *= -1;
            if (new Random().nextBoolean()) ranZ *= -1;
            
            Location loc = new Location(Main.getInstance().world, ranX, 0, ranZ);

            // En Folia debemos ejecutar esto en el hilo que posee las coordenadas del portal
            if (Main.isRunningFolia()) {
                Bukkit.getRegionScheduler().run(Main.instance, loc, task -> {
                    int highestY = loc.getWorld().getHighestBlockYAt(loc);
                    if (highestY <= 0) highestY = 50;
                    loc.setY(highestY + 15);
                    pasteSchematic(loc, new File(Main.getInstance().getDataFolder().getAbsolutePath() + "/schematics/beginning_portal.schem"));
                    Main.getInstance().getBeData().setOverWorldPortal(loc);
                });
            } else {
                int highestY = loc.getWorld().getHighestBlockYAt(loc);
                if (highestY <= 0) highestY = 50;
                loc.setY(highestY + 15);
                pasteSchematic(loc, new File(Main.getInstance().getDataFolder().getAbsolutePath() + "/schematics/beginning_portal.schem"));
                Main.getInstance().getBeData().setOverWorldPortal(loc);
            }
        }

        if (!Main.getInstance().getBeData().generatedBeginningPortal() && !overworld) {
            if (Main.isRunningFolia()) {
                Bukkit.getRegionScheduler().run(Main.instance, to, task -> {
                    pasteSchematic(to, new File(Main.getInstance().getDataFolder().getAbsolutePath() + "/schematics/beginning_portal.schem"));
                    Main.getInstance().getBeData().setBeginningPortal(to);
                });
            } else {
                to.getWorld().loadChunk(to.getChunk());
                pasteSchematic(to, new File(Main.getInstance().getDataFolder().getAbsolutePath() + "/schematics/beginning_portal.schem"));
                Main.getInstance().getBeData().setBeginningPortal(to);
            }
        }
    }

    public static void pasteSchematic(Location loc, File schematic) {
        com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(loc.getWorld());
        ClipboardFormat format = ClipboardFormats.findByFile(schematic);
        try (ClipboardReader reader = format.getReader(new FileInputStream(schematic))) {
            Clipboard clipboard = reader.read();
            try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(adaptedWorld,
                    -1)) {
                Operation operation = new ClipboardHolder(clipboard).createPaste(editSession)
                        .to(BlockVector3.at(loc.getX(), loc.getY(), loc.getZ())).ignoreAirBlocks(true).build();
                try {
                    Operations.complete(operation);
                    editSession.flushSession();
                } catch (WorldEditException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


















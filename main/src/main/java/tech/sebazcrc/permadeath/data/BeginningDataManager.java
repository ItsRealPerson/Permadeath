package tech.sebazcrc.permadeath.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import tech.sebazcrc.permadeath.Main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class BeginningDataManager {

    private File beginningFile;
    private FileConfiguration config;
    private Main instance;

    public BeginningDataManager(Main instance) {
        this.instance = instance;
        this.beginningFile = new File(instance.getDataFolder(), "theBeginning.yml");
        this.config = YamlConfiguration.loadConfiguration(beginningFile);

        if (!beginningFile.exists()) {
            try {
                beginningFile.createNewFile();
            } catch (IOException e) {
                System.out.println("[ERROR] Ha ocurrido un error al crear el archivo 'theBeginning.yml'");
            }
        }

        if (!config.contains("GeneratedOverWorldBeginningPortal")) config.set("GeneratedOverWorldBeginningPortal", false);
        if (!config.contains("GeneratedBeginningPortal")) config.set("GeneratedBeginningPortal", false);
        if (!config.contains("OverWorldPortal")) config.set("OverWorldPortal", "");
        if (!config.contains("BeginningPortal")) config.set("BeginningPortal", "");
        if (!config.contains("KilledED")) config.set("KilledED", false);
        if (!config.contains("PopulatedChests")) config.set("PopulatedChests", new ArrayList<>());

        saveFileSync();
    }

    public boolean hasPopulatedChest(Location l) {
        return config.getStringList("PopulatedChests").contains(locationToString(l));
    }

    public void addPopulatedChest(Location l) {
        ArrayList<String> chests = (ArrayList<String>) config.getStringList("PopulatedChests");
        chests.add(locationToString(l));
        config.set("PopulatedChests", chests);
        saveFile();
    }

    public boolean generatedOverWorldBeginningPortal() { return config.getBoolean("GeneratedOverWorldBeginningPortal"); }
    public boolean generatedBeginningPortal() { return config.getBoolean("GeneratedBeginningPortal"); }

    public Location getBeginningPortal() {
        if (!generatedBeginningPortal()) return null;
        return buildLocation(config.getString("BeginningPortal"));
    }

    public void setBeginningPortal(Location loc) {
        if (generatedBeginningPortal()) return;
        config.set("GeneratedBeginningPortal", true);
        config.set("BeginningPortal", locationToString(loc));
        saveFileSync();
    }

    public Location getOverWorldPortal() {
        if (!generatedOverWorldBeginningPortal()) return null;
        return buildLocation(config.getString("OverWorldPortal"));
    }

    public void setOverWorldPortal(Location loc) {
        if (generatedOverWorldBeginningPortal()) return;
        config.set("GeneratedOverWorldBeginningPortal", true);
        config.set("OverWorldPortal", locationToString(loc));
        saveFileSync();
    }

    public boolean killedED() { return config.getBoolean("KilledED"); }
    public void setKilledED() {
        config.set("KilledED", true);
        saveFileSync();
    }

    public static Location buildLocation(String s) {
        String[] split = s.split(";");
        return new Location(Bukkit.getWorld(split[3]), Double.valueOf(split[0]), Double.valueOf(split[1]), Double.valueOf(split[2]));
    }

    public static String locationToString(Location loc) {
        return loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getWorld().getName();
    }

    public FileConfiguration getConfig() { return config; }

    public void saveFileSync() {
        try {
            config.save(beginningFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveFile() {
        final String data;
        synchronized (this.config) {
            data = this.config.saveToString();
        }
        final File fileToSave = this.beginningFile;
        Runnable task = () -> {
            try {
                java.nio.file.Files.writeString(fileToSave.toPath(), data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        if (Main.isRunningFolia()) Bukkit.getAsyncScheduler().runNow(instance, t -> task.run());
        else Bukkit.getScheduler().runTaskAsynchronously(instance, task);
    }

    public void reloadFile() {
        try {
            config.load(beginningFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}
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

        if (!config.contains("GeneratedOverWorldBeginningPortal")) {

            config.set("GeneratedOverWorldBeginningPortal", false);
        }

        if (!config.contains("GeneratedBeginningPortal")) {

            config.set("GeneratedBeginningPortal", false);
        }

        if (!config.contains("OverWorldPortal")) {

            config.set("OverWorldPortal", "");
        }

        if (!config.contains("BeginningPortal")) {

            config.set("BeginningPortal", "");
        }

        if (!config.contains("KilledED")) {

            config.set("KilledED", false);
        }

        if (!config.contains("PopulatedChests")) {

            config.set("PopulatedChests", new ArrayList<>());
        }

        saveFile();
        reloadFile();
    }

    public boolean hasPopulatedChest(Location l) {

        String s = locationToString(l);

        return config.getStringList("PopulatedChests").contains(s);
    }

    public void addPopulatedChest(Location l) {
        ArrayList<String> chests = (ArrayList<String>) config.getStringList("PopulatedChests");
        chests.add(locationToString(l));
        config.set("PopulatedChests", chests);
        saveFile();
    }

    public boolean generatedOverWorldBeginningPortal() {
        return config.getBoolean("GeneratedOverWorldBeginningPortal");
    }

    public boolean generatedBeginningPortal() {
        return config.getBoolean("GeneratedBeginningPortal");
    }

    public Location getBeginningPortal() {
        if (!generatedBeginningPortal()) {
            return null;
        }
        return buildLocation(config.getString("BeginningPortal"));
    }

    public void setBeginningPortal(Location loc) {
        if (generatedBeginningPortal()) {
            return;
        }
        config.set("GeneratedBeginningPortal", true);
        config.set("BeginningPortal", locationToString(loc));
        saveFileSync(); // Guardado síncrono crítico
    }

    public Location getOverWorldPortal() {
        if (!generatedOverWorldBeginningPortal()) {
            return null;
        }
        return buildLocation(config.getString("OverWorldPortal"));
    }

    public void setOverWorldPortal(Location loc) {
        if (generatedOverWorldBeginningPortal()) {
            return;
        }
        config.set("GeneratedOverWorldBeginningPortal", true);
        config.set("OverWorldPortal", locationToString(loc));
        saveFileSync(); // Guardado síncrono crítico
    }

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
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        };

        if (Main.isRunningFolia()) {
            Bukkit.getAsyncScheduler().runNow(instance, t -> task.run());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(instance, task);
        }
    }

    public boolean killedED() {
        return config.getBoolean("KilledED");
    }

    public void setKilledED() {
        config.set("KilledED", true);
        saveFile();
    }

    public static Location buildLocation(String s) {

        // X;Y;Z;WORLD
        String[] split = s.split(";");

        Double x = Double.valueOf(split[0]);
        Double y = Double.valueOf(split[1]);
        Double z = Double.valueOf(split[2]);
        World w = Bukkit.getWorld(split[3]);

        return new Location(w, x, y, z);
    }

    public static String locationToString(Location loc) {
        return loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getWorld().getName();
    }

    public FileConfiguration getConfig() {
        return config;
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
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        };

        if (Main.isRunningFolia()) {
            Bukkit.getAsyncScheduler().runNow(instance, t -> task.run());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(instance, task);
        }
    }

    public void reloadFile() {
        try {
            config.load(beginningFile);
        } catch (java.io.IOException | InvalidConfigurationException e) {
            System.out.println("[ERROR] Ha ocurrido un error al cargar el archivo 'theBeginning.yml'");
        }
    }
}



















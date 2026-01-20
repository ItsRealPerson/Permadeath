package tech.sebazcrc.permadeath.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import tech.sebazcrc.permadeath.Main;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class CauldronDataManager {

    private final File file;
    private FileConfiguration config;

    public CauldronDataManager() {
        this.file = new File(Main.instance.getDataFolder(), "data/alquimia.yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void saveCauldron(Location loc, int state, long endTime) {
        String path = serializeLoc(loc);
        config.set(path + ".state", state);
        config.set(path + ".endTime", endTime);
        save();
    }

    public void removeCauldron(Location loc) {
        config.set(serializeLoc(loc), null);
        save();
    }

    public Set<String> getActiveCauldrons() {
        return config.getKeys(false);
    }

    public int getState(String path) { return config.getInt(path + ".state"); }
    public long getEndTime(String path) { return config.getLong(path + ".endTime"); }

    public Location deserializeLoc(String s) {
        String[] parts = s.split(",");
        return new Location(Bukkit.getWorld(parts[3]), Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
    }

    private String serializeLoc(Location l) {
        return l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + "," + l.getWorld().getName();
    }

    private void save() {
        try { config.save(file); } catch (IOException e) { e.printStackTrace(); }
    }
}

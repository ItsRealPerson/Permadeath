package dev.itsrealperson.permadeath.util;

import dev.itsrealperson.permadeath.Main;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ShardManager {

    private final Main plugin;
    private File file;
    private FileConfiguration config;
    private final Map<String, Shard> shards = new HashMap<>();
    private String currentShardId;

    public ShardManager(Main plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    private void loadConfig() {
        File dataFolder = new File(plugin.getDataFolder(), "data");
        if (!dataFolder.exists()) dataFolder.mkdirs();
        
        file = new File(dataFolder, "sharding.yml");
        if (!file.exists()) {
            plugin.saveResource("sharding.yml", false);
            File temp = new File(plugin.getDataFolder(), "sharding.yml");
            if (temp.exists()) {
                try {
                    java.nio.file.Files.move(temp.toPath(), file.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                } catch (java.io.IOException ignored) {}
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        
        currentShardId = config.getString("CurrentShardID", "shard-1");
        
        // Cargar shards
        var section = config.getConfigurationSection("Shards");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                String proxyName = section.getString(key + ".ProxyName");
                int minX = section.getInt(key + ".Bounds.MinX");
                int maxX = section.getInt(key + ".Bounds.MaxX");
                int minZ = section.getInt(key + ".Bounds.MinZ");
                int maxZ = section.getInt(key + ".Bounds.MaxZ");
                
                shards.put(key, new Shard(key, proxyName, minX, maxX, minZ, maxZ));
            }
        }
    }

    public String getServerForLocation(Location loc) {
        int x = loc.getBlockX();
        int z = loc.getBlockZ();

        for (Shard shard : shards.values()) {
            if (x >= shard.minX && x <= shard.maxX && z >= shard.minZ && z <= shard.maxZ) {
                return shard.proxyName; // Devolvemos el nombre del servidor en el Proxy
            }
        }
        return null; // Fuera de límites conocidos
    }

    public boolean isLocationInCurrentShard(Location loc) {
        Shard current = shards.get(currentShardId);
        if (current == null) return true; // Si no hay config, asumimos que todo es local

        int x = loc.getBlockX();
        int z = loc.getBlockZ();

        return x >= current.minX && x <= current.maxX && z >= current.minZ && z <= current.maxZ;
    }

    public String getCurrentProxyName() {
        Shard current = shards.get(currentShardId);
        return current != null ? current.proxyName : "unknown";
    }

    private record Shard(String id, String proxyName, int minX, int maxX, int minZ, int maxZ) {}
}

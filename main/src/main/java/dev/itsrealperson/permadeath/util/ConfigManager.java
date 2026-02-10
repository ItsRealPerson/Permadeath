package dev.itsrealperson.permadeath.util;

import dev.itsrealperson.permadeath.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Gestor centralizado de archivos de configuración YAML.
 * Permite cargar, guardar y recargar múltiples archivos de forma unificada.
 */
public class ConfigManager {

    private final Main plugin;
    private final Map<String, ConfigHolder> configs = new HashMap<>();

    public ConfigManager(Main plugin) {
        this.plugin = plugin;
    }

    /**
     * Registra y carga un archivo de configuración.
     * @param fileName Nombre del archivo (ej: "loot.yml")
     */
    public void registerConfig(String fileName) {
        configs.put(fileName, new ConfigHolder(fileName));
    }

    public FileConfiguration getConfig(String fileName) {
        ConfigHolder holder = configs.get(fileName);
        return holder != null ? holder.getConfig() : null;
    }

    public void saveConfig(String fileName) {
        ConfigHolder holder = configs.get(fileName);
        if (holder != null) holder.save();
    }

    public void reloadAll() {
        configs.values().forEach(ConfigHolder::reload);
    }

    private class ConfigHolder {
        private final String relativePath;
        private File file;
        private FileConfiguration config;

        public ConfigHolder(String relativePath) {
            this.relativePath = relativePath;
            reload();
        }

        public void reload() {
            if (file == null) {
                file = new File(plugin.getDataFolder(), relativePath);
            }

            if (!file.exists()) {
                // Crear carpetas si no existen
                file.getParentFile().mkdirs();
                
                // Extraer el recurso (asumiendo que el nombre del recurso es solo el nombre del archivo)
                String resourceName = file.getName();
                if (plugin.getResource(resourceName) != null) {
                    plugin.saveResource(resourceName, false);
                    
                    // Si se guardó en la raíz por saveResource, moverlo a la subcarpeta
                    File tempFile = new File(plugin.getDataFolder(), resourceName);
                    if (tempFile.exists() && !tempFile.equals(file)) {
                        try {
                            java.nio.file.Files.move(tempFile.toPath(), file.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException ignored) {}
                    }
                }
            }

            config = YamlConfiguration.loadConfiguration(file);

            // Cargar valores por defecto desde el JAR usando solo el nombre del archivo
            InputStream defConfigStream = plugin.getResource(file.getName());
            if (defConfigStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, StandardCharsets.UTF_8));
                config.setDefaults(defConfig);
            }
        }

        public FileConfiguration getConfig() {
            if (config == null) reload();
            return config;
        }

        public void save() {
            if (config == null || file == null) return;
            try {
                getConfig().save(file);
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, "No se pudo guardar la configuración en " + file, ex);
            }
        }
    }
}

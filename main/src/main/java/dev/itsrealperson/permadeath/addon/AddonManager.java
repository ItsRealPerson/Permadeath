package dev.itsrealperson.permadeath.addon;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.api.addons.AddonRegistry;
import dev.itsrealperson.permadeath.api.addons.PermadeathAddon;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AddonManager {

    private final Main plugin;
    private final File addonsFolder;

    public AddonManager(Main plugin) {
        this.plugin = plugin;
        this.addonsFolder = new File(plugin.getDataFolder(), "addons");
        if (!addonsFolder.exists()) addonsFolder.mkdirs();
    }

    public void loadAddons() {
        File[] files = addonsFolder.listFiles((dir, name) -> name.endsWith(".jar"));
        if (files == null) return;

        for (File file : files) {
            try {
                loadAddon(file);
                plugin.getLogger().info("Addon cargado con éxito: " + file.getName());
            } catch (Throwable e) {
                plugin.getLogger().severe("------------------------------------------");
                plugin.getLogger().severe(" [CRÍTICO] Falló la carga del Addon: " + file.getName());
                plugin.getLogger().severe(" Motivo: " + e.getMessage());
                plugin.getLogger().severe("------------------------------------------");
            }
        }
    }

    private void loadAddon(File file) throws Exception {
        JarFile jarFile = new JarFile(file);
        JarEntry entry = jarFile.getJarEntry("permadeath-addon.yml");

        if (entry == null) {
            plugin.getLogger().warning("El archivo " + file.getName() + " no es un addon válido (falta permadeath-addon.yml)");
            jarFile.close();
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(jarFile.getInputStream(entry)));
        String mainClassPath = config.getString("main");

        if (mainClassPath == null) {
            plugin.getLogger().warning("El addon " + file.getName() + " no especifica una clase 'main' en permadeath-addon.yml");
            jarFile.close();
            return;
        }

        URLClassLoader loader = new URLClassLoader(
                new URL[]{file.toURI().toURL()},
                getClass().getClassLoader()
        );

        Class<?> mainClass = Class.forName(mainClassPath, true, loader);
        
        // Usar constructor vacío para evitar problemas de tipos con Reflexión
        PermadeathAddon addonInstance = (PermadeathAddon) mainClass.getConstructor().newInstance();
        
        // Inicializar con la API (Main implementa PermadeathAPIProvider)
        addonInstance.onInitialize(plugin);
        
        AddonRegistry.registerAddon(addonInstance);
        jarFile.close();
    }
}

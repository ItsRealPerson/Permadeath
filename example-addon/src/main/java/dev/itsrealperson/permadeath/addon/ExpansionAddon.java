package dev.itsrealperson.permadeath.addon;

import dev.itsrealperson.permadeath.api.PermadeathAPI;
import org.bukkit.plugin.java.JavaPlugin;

public class ExpansionAddon extends JavaPlugin {

    @Override
    public void onEnable() {
        // 1. Verificar si el plugin principal está cargado y la API está disponible
        if (PermadeathAPI.getModuleManager() == null) {
            getLogger().severe("¡No se pudo encontrar el plugin principal de Permadeath! Desactivando expansión...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // 2. Registrar nuestro nuevo módulo de expansión
        PermadeathAPI.getModuleManager().registerModule(new ExtraDaysModule());
        
        getLogger().info("¡Expansión de Días Extra cargada correctamente!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Expansión de Días Extra desactivada.");
    }
}

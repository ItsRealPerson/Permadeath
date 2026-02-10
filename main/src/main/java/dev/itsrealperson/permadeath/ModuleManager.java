package dev.itsrealperson.permadeath;

import dev.itsrealperson.permadeath.api.ModuleManagerAPI;
import dev.itsrealperson.permadeath.api.PermadeathModule;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Gestiona el ciclo de vida de los módulos de Permadeath.
 */
public class ModuleManager implements ModuleManagerAPI {

    private final Main plugin;
    private final Map<String, PermadeathModule> modules = Collections.synchronizedMap(new LinkedHashMap<>());

    public ModuleManager(Main plugin) {
        this.plugin = plugin;
    }

    /**
     * Registra e inicializa un nuevo módulo.
     */
    public void registerModule(PermadeathModule module) {
        if (modules.containsKey(module.getName())) {
            plugin.getLogger().warning("Intento de registrar un módulo duplicado: " + module.getName());
            return;
        }

        try {
            module.onEnable();
            modules.put(module.getName(), module);
            plugin.getLogger().info("Módulo cargado: " + module.getName());
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error al activar el módulo " + module.getName(), e);
        }
    }

    /**
     * Ejecuta el tick en todos los módulos registrados.
     */
    public void tickModules() {
        for (PermadeathModule module : modules.values()) {
            try {
                module.onTick();
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Error durante el tick del módulo " + module.getName(), e);
            }
        }
    }

    /**
     * Desactiva todos los módulos.
     */
    public void unregisterAll() {
        modules.values().forEach(module -> {
            try {
                module.onDisable();
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Error al desactivar el módulo " + module.getName(), e);
            }
        });
        modules.clear();
    }

    /**
     * Reinicia todos los módulos registrados.
     */
    public void reloadModules() {
        plugin.getLogger().info("Reiniciando módulos...");
        
        // Creamos una copia para evitar ConcurrentModificationException si algún onEnable/onDisable intenta registrar algo
        Map<String, PermadeathModule> copy = new LinkedHashMap<>(modules);
        
        copy.values().forEach(module -> {
            try {
                plugin.getLogger().info("Disabling Module: " + module.getName());
                module.onDisable();
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Error al desactivar el módulo " + module.getName(), e);
            }
        });
        
        copy.values().forEach(module -> {
            try {
                plugin.getLogger().info("Enabling Module: " + module.getName());
                module.onEnable();
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Error al reactivar el módulo " + module.getName(), e);
            }
        });
        
        plugin.getLogger().info("¡Módulos reiniciados con éxito!");
    }

    public Optional<PermadeathModule> getModule(String name) {
        return Optional.ofNullable(modules.get(name));
    }
}

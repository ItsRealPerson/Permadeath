package dev.itsrealperson.permadeath.util.placeholder;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.data.DateManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Gestor interno de variables y placeholders de Permadeath.
 */
public class PlaceholderManager {

    private final Main plugin;

    public PlaceholderManager(Main plugin) {
        this.plugin = plugin;
        
        // Integrar con PlaceholderAPI si está presente de forma segura
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            try {
                registerExpansion();
                plugin.getLogger().info("Integración con PlaceholderAPI activada.");
            } catch (Throwable ignored) {
                // Si algo falla al cargar la clase (ej: PAPI no está realmente)
            }
        }
    }

    private void registerExpansion() {
        new InternalPAPIExpansion(plugin).register();
    }

    public String replace(Player player, String text) {
        if (text == null) return "";
        
        return text
                .replace("%permadeath_day%", String.valueOf(DateManager.getInstance().getDay()))
                .replace("%permadeath_prefix%", Main.prefix)
                .replace("%permadeath_playtime%", dev.itsrealperson.permadeath.util.TextUtils.formatInterval(plugin.getPlayTime()))
                .replace("%permadeath_is_death_train%", String.valueOf(plugin.world != null && plugin.world.hasStorm()))
                .replace("%permadeath_death_train_time%", plugin.world != null ? dev.itsrealperson.permadeath.util.TextUtils.formatInterval(plugin.world.getWeatherDuration() / 20) : "00:00");
    }

    // La clase interna extendiendo una clase de PAPI puede causar NoClassDefFoundError
    // si el JVM intenta verificar PlaceholderManager. La movemos a una clase externa
    // o nos aseguramos de que solo se toque si PAPI existe.
}
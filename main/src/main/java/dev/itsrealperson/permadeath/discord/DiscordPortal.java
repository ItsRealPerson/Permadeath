package dev.itsrealperson.permadeath.discord;

import org.bukkit.OfflinePlayer;

/**
 * Punto de entrada simplificado para eventos de Discord.
 * Delega toda la lógica al DiscordManager (Relay Mode).
 */
public class DiscordPortal {

    public static void banPlayer(OfflinePlayer off, boolean isAFKBan) {
        DiscordManager.getInstance().banPlayer(off, isAFKBan);
    }

    public static void onDeathTrain(String msg) {
        DiscordManager.getInstance().onDeathTrain(msg);
    }

    public static void onDayChange() {
        DiscordManager.getInstance().onDayChange();
    }

    public static void onEnable() {
        DiscordManager.getInstance().onPluginEnable();
    }

    public static void onDisable() {
        DiscordManager.getInstance().onDisable();
    }

    public static void reload() {
        // En v1.5 el reload solo refresca la instancia
        DiscordManager.getInstance();
    }
}
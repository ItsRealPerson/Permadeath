package dev.itsrealperson.permadeath;

import dev.itsrealperson.permadeath.api.PermadeathModule;
import dev.itsrealperson.permadeath.util.TextUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Módulo encargado de la interfaz de usuario, Action Bar y sincronización de BossBars.
 */
public class InterfaceModule implements PermadeathModule {

    private final Main plugin;

    public InterfaceModule(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "InterfaceModule";
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onTick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateActionBar(player);
            updateEventBossBars(player);
        }
    }

    private void updateActionBar(Player player) {
        World world = plugin.world;
        long segundosbrutos = world != null ? world.getWeatherDuration() / 20 : 0;
        long hours = segundosbrutos % 86400 / 3600;
        long minutes = (segundosbrutos % 3600) / 60;
        long seconds = segundosbrutos % 60;
        long days = segundosbrutos / 86400;

        final String time = String.format((days >= 1 ? String.format("%02d día(s) ", days) : "") + "%02d:%02d:%02d", hours, minutes, seconds);

        if (Main.SPEED_RUN_MODE) {
            String actionBar = "";
            if (world != null && world.hasStorm()) {
                actionBar = plugin.messages.getMessageByPlayer("Server-Messages.ActionBarMessage", player.getName()).replace("%tiempo%", time) + " - ";
            }
            actionBar = actionBar + ChatColor.GRAY + "Tiempo total: " + TextUtils.formatInterval(plugin.getPlayTime());
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionBar));
        } else {
            if (world != null && world.hasStorm()) {
                String msg = plugin.messages.getMessageByPlayer("Server-Messages.ActionBarMessage", player.getName()).replace("%tiempo%", time);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
            }
        }
    }

    private void updateEventBossBars(Player player) {
        if (plugin.shulkerEvent != null && plugin.shulkerEvent.isRunning()) {
            if (!plugin.shulkerEvent.getBossBar().getPlayers().contains(player)) {
                plugin.shulkerEvent.getBossBar().addPlayer(player);
            }
        }

        if (plugin.orbEvent != null && plugin.orbEvent.isRunning()) {
            if (!plugin.orbEvent.getBossBar().getPlayers().contains(player)) {
                plugin.orbEvent.getBossBar().addPlayer(player);
            }
        }
    }
}

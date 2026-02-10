package dev.itsrealperson.permadeath.command.impl;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.command.SubCommand;
import dev.itsrealperson.permadeath.util.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.time.LocalTime;

public class DurationCommand extends SubCommand {

    private final Main plugin;

    public DurationCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "duracion";
    }

    @Override
    public String getDescription() {
        return "Muestra el tiempo restante de la tormenta.";
    }

    @Override
    public String getUsage() {
        return "/pdc duracion";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        World world = plugin.world;
        if (world == null || !world.hasStorm()) {
            sender.sendMessage(Main.prefix + ChatColor.RED + "¡No hay ninguna tormenta en marcha!");
            return;
        }
        int seconds = world.getWeatherDuration() / 20;
        int days = seconds / 86400;
        String time = LocalTime.ofSecondOfDay(seconds % 86400).toString();
        sender.sendMessage(Main.prefix + ChatColor.RED + "Quedan " + ChatColor.GRAY + (days >= 1 ? days + "d " : "") + time);
    }
}

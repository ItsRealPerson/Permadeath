package dev.itsrealperson.permadeath.command.impl;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.command.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class DayCommand extends SubCommand {

    private final Main plugin;

    public DayCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "dias";
    }

    @Override
    public String getDescription() {
        return "Muestra el día actual del servidor.";
    }

    @Override
    public String getUsage() {
        return "/pdc dias";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (plugin.getDay() < 1) {
            sender.sendMessage(Main.prefix + "&cError al cargar el día.");
        } else {
            sender.sendMessage(Main.prefix + ChatColor.RED + (Main.SPEED_RUN_MODE ? "Hora: " : "Día: ") + ChatColor.GRAY + plugin.getDay());
        }
    }
}
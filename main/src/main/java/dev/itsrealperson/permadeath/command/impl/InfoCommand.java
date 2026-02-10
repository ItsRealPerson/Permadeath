package dev.itsrealperson.permadeath.command.impl;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.command.SubCommand;
import dev.itsrealperson.permadeath.util.gui.InfoGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InfoCommand extends SubCommand {

    private final Main plugin;

    public InfoCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "Muestra información de estado del jugador.";
    }

    @Override
    public String getUsage() {
        return "/pdc info";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player p) {
            InfoGUI.open(p);
        } else {
            sender.sendMessage(Main.prefix + ChatColor.RED + "Version Info:");
            sender.sendMessage(ChatColor.GRAY + "- Plugin: " + ChatColor.GREEN + "PermaDeathCore.jar v" + plugin.getDescription().getVersion());
            sender.sendMessage(ChatColor.GRAY + "- Dificultad: " + ChatColor.GREEN + "Días 1-60");
            sender.sendMessage(ChatColor.GRAY + "- Autor: " + ChatColor.GREEN + "ItsRealPerson");
        }
    }
}
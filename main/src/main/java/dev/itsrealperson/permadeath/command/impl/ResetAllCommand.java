package dev.itsrealperson.permadeath.command.impl;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.command.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class ResetAllCommand extends SubCommand {

    private final Main plugin;

    public ResetAllCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "resetall";
    }

    @Override
    public String getDescription() {
        return "Borra todos los datos y reinicia el plugin al Día 1.";
    }

    @Override
    public String getUsage() {
        return "/pdc resetall confirm";
    }

    @Override
    public String getPermission() {
        return "permadeathcore.admin";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1 || !args[0].equalsIgnoreCase("confirm")) {
            sender.sendMessage(Main.prefix + ChatColor.RED + "¡ADVERTENCIA! Este comando borrará los datos de jugadores, desbaneará a todos y volverá al Día 1.");
            sender.sendMessage(Main.prefix + ChatColor.YELLOW + "Usa: " + ChatColor.WHITE + "/pdc resetall confirm" + ChatColor.YELLOW + " para proceder.");
            return;
        }
        plugin.resetManager.resetAll(sender);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Collections.singletonList("confirm");
        }
        return super.tabComplete(sender, args);
    }
}

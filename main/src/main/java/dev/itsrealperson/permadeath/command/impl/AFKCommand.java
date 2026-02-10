package dev.itsrealperson.permadeath.command.impl;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.command.SubCommand;
import dev.itsrealperson.permadeath.data.PlayerDataManager;
import dev.itsrealperson.permadeath.util.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class AFKCommand extends SubCommand {

    private final Main plugin;

    public AFKCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "afk";
    }

    @Override
    public String getDescription() {
        return "Gestiona baneos por inactividad.";
    }

    @Override
    public String getPermission() {
        return "permadeath.admin";
    }

    @Override
    public String getUsage() {
        return "/pdc afk unban <jugador>";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2 || !args[0].equalsIgnoreCase("unban")) {
            sender.sendMessage(TextUtils.format(Main.prefix + "&cUso: " + getUsage()));
            return;
        }

        String target = args[1];
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pardon " + target);
        new PlayerDataManager(target, plugin).setLastDay(plugin.getDay());
        sender.sendMessage(TextUtils.format(Main.prefix + "&aJugador " + target + " perdonado del baneo AFK."));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) return Arrays.asList("unban");
        return super.tabComplete(sender, args);
    }
}

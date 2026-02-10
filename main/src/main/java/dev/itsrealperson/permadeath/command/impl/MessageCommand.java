package dev.itsrealperson.permadeath.command.impl;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.command.SubCommand;
import dev.itsrealperson.permadeath.util.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageCommand extends SubCommand {

    private final Main plugin;

    public MessageCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "mensaje";
    }

    @Override
    public String getDescription() {
        return "Establece tu mensaje de muerte personalizado.";
    }

    @Override
    public String getUsage() {
        return "/pdc mensaje <mensaje>";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Solo jugadores pueden ejecutar este comando.");
            return;
        }

        if (args.length < 1) {
            sender.sendMessage(TextUtils.format(Main.prefix + "&cUsa: " + getUsage()));
            return;
        }

        StringBuilder msg = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) msg.append(" ");
            msg.append(args[i]);
        }

        plugin.getConfig().set("Server-Messages.CustomDeathMessages." + player.getName(), msg.toString());
        plugin.saveConfig();
        
        sender.sendMessage(TextUtils.format(Main.prefix + "&aMensaje de muerte actualizado a: &f" + msg.toString()));
    }
}
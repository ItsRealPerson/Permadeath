package dev.itsrealperson.permadeath.command.impl;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.command.SubCommand;
import dev.itsrealperson.permadeath.util.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LocateCommand extends SubCommand {

    private final Main plugin;

    public LocateCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "locate";
    }

    @Override
    public String getDescription() {
        return "Localiza estructuras o portales importantes.";
    }

    @Override
    public String getUsage() {
        return "/pdc locate <portal_beginning>";
    }

    @Override
    public String getPermission() {
        return "permadeathcore.admin";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Solo jugadores pueden ejecutar este comando.");
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(TextUtils.format(Main.prefix + "&cUsa: " + getUsage()));
            return;
        }

        if (args[1].equalsIgnoreCase("portal_beginning")) {
            Location loc = plugin.getBeData().getOverWorldPortal();
            if (loc == null) {
                sender.sendMessage(TextUtils.format(Main.prefix + "&cEl portal de The Beginning aún no ha sido generado."));
            } else {
                sender.sendMessage(TextUtils.format(Main.prefix + "&ePortal de The Beginning localizado en: &b" + 
                        loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ()));
            }
        }
    }
}

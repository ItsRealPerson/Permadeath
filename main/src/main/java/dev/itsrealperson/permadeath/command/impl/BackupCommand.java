package dev.itsrealperson.permadeath.command.impl;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.command.SubCommand;
import dev.itsrealperson.permadeath.util.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class BackupCommand extends SubCommand {

    private final Main plugin;

    public BackupCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "backup";
    }

    @Override
    public String getDescription() {
        return "Crea un respaldo de los datos del plugin.";
    }

    @Override
    public String getUsage() {
        return "/pdc backup";
    }

    @Override
    public String getPermission() {
        return "permadeathcore.admin";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(TextUtils.format(Main.prefix + "&eIniciando respaldo de seguridad..."));
        plugin.backupManager.createBackup(sender);
    }
}

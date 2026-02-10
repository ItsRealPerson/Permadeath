package dev.itsrealperson.permadeath.command.impl;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.command.SubCommand;
import dev.itsrealperson.permadeath.util.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class SetupBeginningCommand extends SubCommand {

    private final Main plugin;

    public SetupBeginningCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "setupbeginning";
    }

    @Override
    public String getDescription() {
        return "Configuración técnica de mundos para Folia.";
    }

    @Override
    public String getPermission() {
        return "permadeath.admin";
    }

    @Override
    public String getUsage() {
        return "/pdc setupbeginning";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "Solo operadores.");
            return;
        }
        plugin.setupFoliaWorldConfig(sender);
    }
}

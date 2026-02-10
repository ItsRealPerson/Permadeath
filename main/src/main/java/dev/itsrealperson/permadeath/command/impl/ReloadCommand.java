package dev.itsrealperson.permadeath.command.impl;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.command.SubCommand;
import dev.itsrealperson.permadeath.util.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends SubCommand {

    private final Main plugin;

    public ReloadCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Recarga la configuración del plugin.";
    }

    @Override
    public String getUsage() {
        return "/pdc reload";
    }

    @Override
    public String getPermission() {
        return "permadeathcore.admin";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        plugin.reloadConfig();
        if (plugin.getPdcConfigManager() != null) {
            plugin.getPdcConfigManager().reloadAll();
        }
        
        if (plugin.getModuleManager() != null) {
            ((dev.itsrealperson.permadeath.ModuleManager) plugin.getModuleManager()).reloadModules();
        }
        
        plugin.getMessages().reloadFiles();
        
        if (plugin.getBeginningManager() != null) {
            plugin.getBeginningManager().loadWorld();
        }
        
        sender.sendMessage(TextUtils.format(Main.prefix + "&a¡Configuración y mensajes recargados con éxito!"));
    }
}

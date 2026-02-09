package dev.itsrealperson.permadeath.command.impl;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.command.SubCommand;
import dev.itsrealperson.permadeath.util.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class SpeedRunCommand extends SubCommand {

    private final Main plugin;

    public SpeedRunCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "speedrun";
    }

    @Override
    public String getDescription() {
        return "Activa o desactiva el modo SpeedRun.";
    }

    @Override
    public String getUsage() {
        return "/pdc speedrun";
    }

    @Override
    public String getPermission() {
        return "permadeathcore.admin";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Main.SPEED_RUN_MODE = !Main.SPEED_RUN_MODE;
        plugin.getConfig().set("Toggles.SpeedRun", Main.SPEED_RUN_MODE);
        plugin.saveConfig();
        
        String status = Main.SPEED_RUN_MODE ? "&aactivado" : "&cdesactivado";
        sender.sendMessage(TextUtils.format(Main.prefix + "&eModo SpeedRun " + status));
    }
}

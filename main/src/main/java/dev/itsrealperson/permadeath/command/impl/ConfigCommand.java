package dev.itsrealperson.permadeath.command.impl;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.command.SubCommand;
import dev.itsrealperson.permadeath.util.TextUtils;
import dev.itsrealperson.permadeath.util.gui.ConfigMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class ConfigCommand extends SubCommand {

    private final Main plugin;

    public ConfigCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "config";
    }

    @Override
    public String getDescription() {
        return "Abre el editor de configuración in-game.";
    }

    @Override
    public String getUsage() {
        return "/pdc config";
    }

    @Override
    public String getPermission() {
        return "permadeath.admin";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(TextUtils.format(Main.prefix + "&cEste comando solo puede ser ejecutado por jugadores."));
            return;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("edit")) {
            boolean current = dev.itsrealperson.permadeath.util.gui.AbstractMenu.isSafeMode();
            dev.itsrealperson.permadeath.util.gui.AbstractMenu.setSafeMode(!current);
            String status = (!current ? "&cACTIVADO (Solo Lectura)" : "&aDESACTIVADO (Edición Libre)");
            player.sendMessage(TextUtils.format("&e&l[GUI] &fModo Seguro: " + status));
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            return;
        }

        new ConfigMenu().open(player);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Collections.singletonList("edit");
        }
        return Collections.emptyList();
    }
}

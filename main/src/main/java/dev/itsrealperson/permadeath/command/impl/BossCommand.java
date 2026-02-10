package dev.itsrealperson.permadeath.command.impl;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.command.SubCommand;
import dev.itsrealperson.permadeath.util.TextUtils;
import dev.itsrealperson.permadeath.util.VersionManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class BossCommand extends SubCommand {

    private final Main plugin;

    public BossCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "boss";
    }

    @Override
    public String getDescription() {
        return "Invoca bosses especiales de Permadeath.";
    }

    @Override
    public String getPermission() {
        return "permadeath.admin";
    }

    @Override
    public String getUsage() {
        return "/pdc boss spawn warden";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) return;

        if (args.length < 2 || !args[0].equalsIgnoreCase("spawn")) {
            sender.sendMessage(TextUtils.format(Main.prefix + "&6Comandos de Bosses: &7/pdc boss spawn warden"));
            return;
        }

        if (args[1].equalsIgnoreCase("warden")) {
            plugin.getNmsHandler().spawnNMSCustomEntity("boss.CustomWarden", org.bukkit.entity.EntityType.WARDEN, p.getLocation(), org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.CUSTOM);
            Bukkit.broadcastMessage(TextUtils.format(Main.prefix + "&3&l¡El Twisted Warden ha sido invocado!"));
        } else {
            sender.sendMessage(TextUtils.format("&cBoss no reconocido."));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) return Arrays.asList("spawn");
        if (args.length == 2) return Arrays.asList("warden");
        return super.tabComplete(sender, args);
    }
}

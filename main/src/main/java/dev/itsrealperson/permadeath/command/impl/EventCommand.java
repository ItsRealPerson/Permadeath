package dev.itsrealperson.permadeath.command.impl;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.command.SubCommand;
import dev.itsrealperson.permadeath.util.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class EventCommand extends SubCommand {

    private final Main plugin;

    public EventCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "event";
    }

    @Override
    public String getDescription() {
        return "Inicia eventos globales manualmente.";
    }

    @Override
    public String getPermission() {
        return "permadeath.admin";
    }

    @Override
    public String getUsage() {
        return "/pdc event <shulkershell/lifeorb>";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(TextUtils.format(Main.prefix + "&cUso: /pdc event <shulkershell/lifeorb>"));
            return;
        }

        String ev = args[0].toLowerCase();
        if (ev.equals("shulkershell")) {
            if (plugin.getShulkerEvent() != null) {
                plugin.getShulkerEvent().setRunning(true);
                Bukkit.getOnlinePlayers().forEach(plugin.getShulkerEvent()::addPlayer);
                sender.sendMessage(TextUtils.format("&aEvento Shulker iniciado."));
            }
        } else if (ev.equals("lifeorb")) {
            if (plugin.getOrbEvent() != null) {
                plugin.getOrbEvent().setRunning(true);
                Bukkit.getOnlinePlayers().forEach(plugin.getOrbEvent()::addPlayer);
                sender.sendMessage(TextUtils.format("&aEvento Life Orb iniciado."));
            }
        } else {
            sender.sendMessage(TextUtils.format("&cEvento no válido."));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) return Arrays.asList("shulkershell", "lifeorb");
        return super.tabComplete(sender, args);
    }
}

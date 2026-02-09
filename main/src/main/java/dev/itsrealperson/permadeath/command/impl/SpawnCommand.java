package dev.itsrealperson.permadeath.command.impl;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.command.SubCommand;
import dev.itsrealperson.permadeath.util.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SpawnCommand extends SubCommand {

    private final Main plugin;

    public SpawnCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "spawn";
    }

    @Override
    public String getDescription() {
        return "Spawnea un mob personalizado de Permadeath.";
    }

    @Override
    public String getUsage() {
        return "/pdc spawn <mob>";
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

        String mobName = args[1];
        plugin.getNmsHandler().spawnNMSCustomEntity(mobName, null, player.getLocation(), CreatureSpawnEvent.SpawnReason.CUSTOM);
        sender.sendMessage(TextUtils.format(Main.prefix + "&aIntentando spawnear mob: &f" + mobName));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return Arrays.asList("SilentSeeker", "SculkParasite", "EchoArcher", "HollowGuard", "TwistedWarden", "VoidSkeleton", "UltraRavager");
        }
        return Collections.emptyList();
    }
}

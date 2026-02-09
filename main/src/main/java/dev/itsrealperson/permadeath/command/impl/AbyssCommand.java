package dev.itsrealperson.permadeath.command.impl;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.command.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class AbyssCommand extends SubCommand {

    private final Main plugin;

    public AbyssCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "abyss";
    }

    @Override
    public String getDescription() {
        return "Te teletransporta al Abismo o fuerza su carga.";
    }

    @Override
    public String getUsage() {
        return "/pdc abyss [force]";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Solo jugadores pueden ejecutar este comando.");
            return;
        }

        if (args.length > 1 && args[1].equalsIgnoreCase("force") && player.isOp()) {
            player.sendMessage(ChatColor.YELLOW + "Forzando carga del Abismo...");
            plugin.getAbyssManager().loadWorld();
            return;
        }

        plugin.getAbyssManager().teleportToAbyss(player);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2 && sender.isOp()) {
            return Collections.singletonList("force");
        }
        return super.tabComplete(sender, args);
    }
}

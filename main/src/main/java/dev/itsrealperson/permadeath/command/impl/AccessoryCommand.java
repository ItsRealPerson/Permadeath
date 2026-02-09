package dev.itsrealperson.permadeath.command.impl;

import dev.itsrealperson.permadeath.command.SubCommand;
import dev.itsrealperson.permadeath.util.inventory.AccessoryInventory;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AccessoryCommand extends SubCommand {

    @Override
    public String getName() {
        return "accesorios";
    }

    @Override
    public String getDescription() {
        return "Abre el menú de accesorios.";
    }

    @Override
    public String getUsage() {
        return "/pdc accesorios";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Solo jugadores pueden ejecutar este comando.");
            return;
        }
        AccessoryInventory.open(player);
    }
}

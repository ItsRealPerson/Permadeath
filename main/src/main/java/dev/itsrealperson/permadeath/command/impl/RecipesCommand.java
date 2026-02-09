package dev.itsrealperson.permadeath.command.impl;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.command.SubCommand;
import dev.itsrealperson.permadeath.util.gui.RecipeGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RecipesCommand extends SubCommand {

    @Override
    public String getName() {
        return "recipes";
    }

    @Override
    public String getDescription() {
        return "Abre el menú de recetas personalizadas.";
    }

    @Override
    public String getUsage() {
        return "/pdc recipes";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Solo jugadores pueden ejecutar este comando.");
            return;
        }
        RecipeGUI.openMain(player);
    }
}

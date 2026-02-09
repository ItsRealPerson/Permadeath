package dev.itsrealperson.permadeath.command.impl;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.command.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class DiscordCommand extends SubCommand {

    @Override
    public String getName() {
        return "discord";
    }

    @Override
    public String getDescription() {
        return "Muestra los enlaces de Discord de la comunidad.";
    }

    @Override
    public String getUsage() {
        return "/pdc discord";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(Main.prefix + ChatColor.BLUE + "https://discord.gg/w58wzrcJU8 | https://discord.gg/infernalcore");
    }
}

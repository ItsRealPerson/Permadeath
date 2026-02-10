package dev.itsrealperson.permadeath.command.impl;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.command.SubCommand;
import dev.itsrealperson.permadeath.util.TextUtils;
import org.bukkit.command.CommandSender;

public class ChangesCommand extends SubCommand {

    @Override
    public String getName() {
        return "cambios";
    }

    @Override
    public String getDescription() {
        return "Enlace a la wiki de cambios de dificultad.";
    }

    @Override
    public String getUsage() {
        return "/pdc cambios";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(TextUtils.format("&eCambios de dificultad: &f&lhttps://permadeath.fandom.com/es/wiki/Cambios_de_dificultad"));
    }
}

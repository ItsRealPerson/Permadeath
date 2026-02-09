package dev.itsrealperson.permadeath.command.impl;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.command.SubCommand;
import dev.itsrealperson.permadeath.data.DateManager;
import dev.itsrealperson.permadeath.util.TextUtils;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class DayChangeCommand extends SubCommand {

    @Override
    public String getName() {
        return "cambiardia";
    }

    @Override
    public String getDescription() {
        return "Cambia el día actual del servidor.";
    }

    @Override
    public String getUsage() {
        return "/pdc cambiardia <día>";
    }

    @Override
    public String getPermission() {
        return "permadeathcore.admin";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(TextUtils.format(Main.prefix + "&cUsa: " + getUsage()));
            return;
        }
        
        DateManager.getInstance().setDay(sender, args[1]);
    }
}

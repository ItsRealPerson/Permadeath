package dev.itsrealperson.permadeath.command.impl;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.command.SubCommand;
import dev.itsrealperson.permadeath.data.DateManager;
import dev.itsrealperson.permadeath.util.TextUtils;
import org.bukkit.command.CommandSender;

public class DayCommand extends SubCommand {

    @Override
    public String getName() {
        return "dias";
    }

    @Override
    public String getDescription() {
        return "Muestra el día actual del servidor.";
    }

    @Override
    public String getUsage() {
        return "/pdc dias";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(TextUtils.format(Main.prefix + "&eHoy es el día: &b" + DateManager.getInstance().getDay()));
    }
}

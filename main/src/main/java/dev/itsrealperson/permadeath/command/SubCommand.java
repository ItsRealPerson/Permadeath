package dev.itsrealperson.permadeath.command;

import org.bukkit.command.CommandSender;
import java.util.Collections;
import java.util.List;

/**
 * Representa un sub-comando de /pdc
 */
public abstract class SubCommand {

    public abstract String getName();

    public abstract String getDescription();

    public abstract String getUsage();

    public String getPermission() {
        return null;
    }

    public abstract void execute(CommandSender sender, String[] args);

    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}

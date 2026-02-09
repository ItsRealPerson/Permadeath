package dev.itsrealperson.permadeath.command;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.util.TextUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class CommandManager implements CommandExecutor, TabCompleter {

    private final Main plugin;
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public CommandManager(Main plugin) {
        this.plugin = plugin;
    }

    public void registerSubCommand(SubCommand subCommand) {
        subCommands.put(subCommand.getName().toLowerCase(), subCommand);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        SubCommand sub = subCommands.get(args[0].toLowerCase());
        if (sub == null) {
            sender.sendMessage(TextUtils.format(Main.prefix + "&cComando no reconocido. Usa /pdc help para ver la lista."));
            return true;
        }

        if (sub.getPermission() != null && !sender.hasPermission(sub.getPermission())) {
            sender.sendMessage(TextUtils.format(Main.prefix + "&cNo tienes permiso para ejecutar este comando."));
            return true;
        }

        sub.execute(sender, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return subCommands.values().stream()
                    .filter(sub -> sub.getPermission() == null || sender.hasPermission(sub.getPermission()))
                    .map(SubCommand::getName)
                    .filter(name -> name.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length > 1) {
            SubCommand sub = subCommands.get(args[0].toLowerCase());
            if (sub != null) {
                return sub.tabComplete(sender, args);
            }
        }

        return Collections.emptyList();
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(TextUtils.format("&7&m------------------------------------------"));
        sender.sendMessage(TextUtils.format("             &c&lPERMADEATH &7- Ayuda"));
        for (SubCommand sub : subCommands.values()) {
            if (sub.getPermission() == null || sender.hasPermission(sub.getPermission())) {
                sender.sendMessage(TextUtils.format(" &7➤ &b/pdc " + sub.getName() + " &f- " + sub.getDescription()));
            }
        }
        sender.sendMessage(TextUtils.format("&7&m------------------------------------------"));
    }
}

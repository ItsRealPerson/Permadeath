package dev.itsrealperson.permadeath.command.impl;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.command.SubCommand;
import dev.itsrealperson.permadeath.util.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class StormCommand extends SubCommand {

    private final Main plugin;

    public StormCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "storm";
    }

    @Override
    public String getDescription() {
        return "Agrega o quita horas de tormenta";
    }

    @Override
    public String getUsage() {
        return "/pdc storm <add/remove> <cantidad> <h/m>";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        World world = plugin.world;

        if (args.length == 0) {
            if (world == null || !world.hasStorm()) {
                sender.sendMessage(Main.prefix + ChatColor.RED + "¡No hay ninguna tormenta en marcha!");
                return;
            }
            sender.sendMessage(Main.prefix + ChatColor.RED + "usa /pdc storm <add/remove> <cantidad> <h/m> para modificar el tiempo de tormenta");
        }

        if (args.length < 2) {
            sender.sendMessage(TextUtils.format(Main.prefix + "&cUso: " + getUsage()));
            return;
        }

        String action = args[0].toLowerCase();
        int amount = 0;
        String unit = "h";

        if (args.length == 2) {
            String raw = args[1].toLowerCase();
            if (raw.endsWith("h") || raw.endsWith("m") || raw.endsWith("s")) {
                unit = raw.substring(raw.length() - 1);
                try {
                    amount = Integer.parseInt(raw.substring(0, raw.length() - 1));
                } catch (NumberFormatException e) {
                    sender.sendMessage(TextUtils.format(Main.prefix + "&cLa cantidad debe ser un número (ej: 1h o 1 h)."));
                    return;
                }
            } else {
                try {
                    amount = Integer.parseInt(raw);
                } catch (NumberFormatException e) {
                    sender.sendMessage(TextUtils.format(Main.prefix + "&cLa cantidad debe ser un número."));
                    return;
                }
            }
        } else {
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(TextUtils.format(Main.prefix + "&cLa cantidad debe ser un número."));
                return;
            }
            unit = args[2].toLowerCase();
        }

        long ticksToAdd = amount * 20L;
        if (unit.startsWith("m")) ticksToAdd *= 60;
        else if (unit.startsWith("h")) ticksToAdd *= 3600;

        if (world == null) return;

        int currentDuration = world.getWeatherDuration();
        
        final int finalAmount = amount;
        final String finalUnit = unit;

        if (action.equals("add")) {
            final int newDuration = (int) (currentDuration + ticksToAdd);
            Runnable task = () -> {
                world.setStorm(true);
                world.setThundering(true);
                world.setWeatherDuration(newDuration);
                world.setThunderDuration(newDuration);
                sender.sendMessage(TextUtils.format(Main.prefix + "&eSe han &aañadido &b" + finalAmount + finalUnit + " &ea la tormenta."));
            };
            if (Main.isRunningFolia()) {
                org.bukkit.Bukkit.getGlobalRegionScheduler().run(plugin, t -> task.run());
            } else {
                task.run();
            }
        } else if (action.equals("remove")) {
            final int newDuration = (int) Math.max(0, currentDuration - ticksToAdd);
            Runnable task = () -> {
                if (newDuration <= 0) {
                    world.setStorm(false);
                    world.setThundering(false);
                    world.setWeatherDuration(0);
                    world.setThunderDuration(0);
                    sender.sendMessage(TextUtils.format(Main.prefix + "&eSe ha &celiminado &ela tormenta."));
                } else {
                    world.setWeatherDuration(newDuration);
                    world.setThunderDuration(newDuration);
                    sender.sendMessage(TextUtils.format(Main.prefix + "&eSe han &creducido &b" + finalAmount + finalUnit + " &ela tormenta."));
                }
            };
            if (Main.isRunningFolia()) {
                org.bukkit.Bukkit.getGlobalRegionScheduler().run(plugin, t -> task.run());
            } else {
                task.run();
            }
        } else {
            sender.sendMessage(TextUtils.format(Main.prefix + "&cAcción no válida. Usa 'add' o 'remove'."));
        }
    }
}

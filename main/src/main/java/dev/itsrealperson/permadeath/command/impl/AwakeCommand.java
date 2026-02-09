package dev.itsrealperson.permadeath.command.impl;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.command.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalTime;

public class AwakeCommand extends SubCommand {

    @Override
    public String getName() {
        return "awake";
    }

    @Override
    public String getDescription() {
        return "Muestra el tiempo que llevas despierto.";
    }

    @Override
    public String getUsage() {
        return "/pdc awake";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Solo jugadores pueden ejecutar este comando.");
            return;
        }

        int timeAwake = player.getStatistic(Statistic.TIME_SINCE_REST) / 20;
        long days = timeAwake / 86400;
        String time = LocalTime.ofSecondOfDay(timeAwake % 86400).toString();
        sender.sendMessage(Main.prefix + ChatColor.RED + "Tiempo despierto: " + ChatColor.GRAY + (days >= 1 ? days + " días " : "") + time);
    }
}

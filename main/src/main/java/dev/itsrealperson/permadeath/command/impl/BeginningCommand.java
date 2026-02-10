package dev.itsrealperson.permadeath.command.impl;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.command.SubCommand;
import dev.itsrealperson.permadeath.util.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BeginningCommand extends SubCommand {

    private final Main plugin;

    public BeginningCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "beginning";
    }

    @Override
    public String getDescription() {
        return "Gestiona bendiciones y maldiciones de The Beginning.";
    }

    @Override
    public String getPermission() {
        return "permadeath.admin";
    }

    @Override
    public String getUsage() {
        return "/pdc beginning <bendicion/maldicion> <jugador>";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(TextUtils.format(Main.prefix + "&cUso: /pdc beginning <bendicion/maldicion> <jugador>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(TextUtils.format("&cJugador no encontrado."));
            return;
        }

        if (args[0].equalsIgnoreCase("bendicion")) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20 * 60 * 60 * 12, 1));
            Bukkit.broadcastMessage(TextUtils.format(Main.prefix + "&d&l" + target.getName() + " ha recibido la bendición!"));
        } else if (args[0].equalsIgnoreCase("maldicion")) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 60 * 60 * 12, 0));
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 60 * 60 * 12, 0));
            Bukkit.broadcastMessage(TextUtils.format(Main.prefix + "&d&l" + target.getName() + " ha recibido la maldición!"));
        } else {
            sender.sendMessage(TextUtils.format("&cAcción no válida."));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) return Arrays.asList("bendicion", "maldicion");
        if (args.length == 2) return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        return super.tabComplete(sender, args);
    }
}

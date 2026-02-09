package dev.itsrealperson.permadeath.command.impl;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.command.SubCommand;
import dev.itsrealperson.permadeath.util.TextUtils;
import dev.itsrealperson.permadeath.util.item.PermadeathItems;
import dev.itsrealperson.permadeath.util.lib.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GiveCommand extends SubCommand {

    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getDescription() {
        return "Entrega un objeto especial de Permadeath a un jugador.";
    }

    @Override
    public String getUsage() {
        return "/pdc give <jugador> <item>";
    }

    @Override
    public String getPermission() {
        return "permadeathcore.give";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(TextUtils.format(Main.prefix + "&eItems: &7medalla, endrelic, beginningrelic, lifeorb, watermedal, infernalblock, voidshard..."));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(TextUtils.format(Main.prefix + "&cJugador no encontrado."));
            return;
        }

        String itemName = args[2].toLowerCase();
        ItemStack item = null;

        switch (itemName) {
            case "medalla" -> item = new ItemBuilder(Material.TOTEM_OF_UNDYING).setUnbrekeable(true).setDisplayName(TextUtils.format("&6&lMedalla de Superviviente")).build();
            case "endrelic" -> item = PermadeathItems.crearReliquia();
            case "beginningrelic" -> item = PermadeathItems.createBeginningRelic();
            case "lifeorb" -> item = PermadeathItems.createLifeOrb();
            case "watermedal" -> item = PermadeathItems.createWaterMedal();
            case "infernalblock" -> item = PermadeathItems.createInfernalNetheriteBlock();
            case "abyssalheart" -> item = PermadeathItems.createAbyssalHeart();
            case "abyssalmask" -> item = PermadeathItems.createAbyssalMask();
            case "voidshard" -> item = PermadeathItems.createVoidShard();
        }

        if (item == null) {
            sender.sendMessage(TextUtils.format(Main.prefix + "&cObjeto no reconocido."));
            return;
        }

        target.getInventory().addItem(item);
        sender.sendMessage(TextUtils.format(Main.prefix + "&aEntregado &f" + itemName + " &aa &b" + target.getName()));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        }
        if (args.length == 3) {
            return Arrays.asList("medalla", "endrelic", "beginningrelic", "lifeorb", "watermedal", "infernalblock", "abyssalheart", "abyssalmask", "voidshard");
        }
        return Collections.emptyList();
    }
}
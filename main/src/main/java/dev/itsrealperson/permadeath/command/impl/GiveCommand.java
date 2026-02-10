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
        if (args.length < 2) {
            sender.sendMessage(TextUtils.format(Main.prefix + "&eUso: &7/pdc give <jugador> <item>"));
            sender.sendMessage(TextUtils.format("&eItems Disponibles: &7medalla, endrelic, beginningrelic, lifeorb, watermedal, voidshard, abyssalheart, abyssalmask, abyssalfilter, abyssalore, abyssalpotion, infernalelytra, netheritesword, netheritepickaxe, netheriteaxe, netheriteshovel, netheritehoe, infernalingot, infernalblock, ancestralfragment, templatemask, templatechest, templatelegs, templateboots, infernalhelmet, infernalchest, infernallegs, infernalboots"));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(TextUtils.format(Main.prefix + "&cJugador no encontrado."));
            return;
        }

        String itemName = args[1].toLowerCase();
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
            case "abyssalfilter" -> item = PermadeathItems.createAbyssalFilter();
            case "abyssalore" -> item = PermadeathItems.createAbyssalOre();
            case "abyssalpotion" -> item = PermadeathItems.createAbyssalPotion();
            case "infernalelytra" -> item = PermadeathItems.craftInfernalElytra();
            case "netheritesword" -> item = PermadeathItems.craftNetheriteSword();
            case "netheritepickaxe" -> item = PermadeathItems.craftNetheritePickaxe();
            case "netheriteaxe" -> item = PermadeathItems.craftNetheriteAxe();
            case "netheriteshovel" -> item = PermadeathItems.craftNetheriteShovel();
            case "netheritehoe" -> item = PermadeathItems.craftNetheriteHoe();
            case "infernalingot" -> item = PermadeathItems.craftInfernalNetheriteIngot();
            case "ancestralfragment" -> item = dev.itsrealperson.permadeath.util.item.NetheriteArmor.craftAncestralFragment();
            case "templatemask" -> item = dev.itsrealperson.permadeath.util.item.NetheriteArmor.craftTemplate("helmet");
            case "templatechest" -> item = dev.itsrealperson.permadeath.util.item.NetheriteArmor.craftTemplate("chestplate");
            case "templatelegs" -> item = dev.itsrealperson.permadeath.util.item.NetheriteArmor.craftTemplate("leggings");
            case "templateboots" -> item = dev.itsrealperson.permadeath.util.item.NetheriteArmor.craftTemplate("boots");
            case "netheritehelmet" -> item = dev.itsrealperson.permadeath.util.item.NetheriteArmor.craftNetheriteHelmet();
            case "netheritechest" -> item = dev.itsrealperson.permadeath.util.item.NetheriteArmor.craftNetheriteChest();
            case "netheritelegs" -> item = dev.itsrealperson.permadeath.util.item.NetheriteArmor.craftNetheriteLegs();
            case "netheriteboots" -> item = dev.itsrealperson.permadeath.util.item.NetheriteArmor.craftNetheriteBoots();
            case "infernalhelmet" -> item = dev.itsrealperson.permadeath.util.item.InfernalNetherite.craftNetheriteHelmet();
            case "infernalchest" -> item = dev.itsrealperson.permadeath.util.item.InfernalNetherite.craftNetheriteChest();
            case "infernallegs" -> item = dev.itsrealperson.permadeath.util.item.InfernalNetherite.craftNetheriteLegs();
            case "infernalboots" -> item = dev.itsrealperson.permadeath.util.item.InfernalNetherite.craftNetheriteBoots();
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
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        }
        if (args.length == 2) {
            return Arrays.asList("medalla", "endrelic", "beginningrelic", "lifeorb", "watermedal", "voidshard", "abyssalheart", "abyssalmask", "abyssalfilter", "abyssalore", "abyssalpotion", "infernalelytra", "netheritesword", "netheritepickaxe", "netheriteaxe", "netheriteshovel", "netheritehoe", "infernalingot", "infernalblock", "ancestralfragment", "templatemask", "templatechest", "templatelegs", "templateboots", "netheritehelmet", "netheritechest", "netheritelegs", "netheriteboots", "infernalhelmet", "infernalchest", "infernallegs", "infernalboots");
        }
        return Collections.emptyList();
    }
}
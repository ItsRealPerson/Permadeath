package dev.itsrealperson.permadeath.command.impl;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.command.SubCommand;
import dev.itsrealperson.permadeath.data.DateManager;
import dev.itsrealperson.permadeath.util.TextUtils;
import dev.itsrealperson.permadeath.util.VersionManager;
import dev.itsrealperson.permadeath.util.item.NetheriteArmor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DebugCommand extends SubCommand {

    private final Main plugin;

    public DebugCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "debug";
    }

    @Override
    public String getDescription() {
        return "Comandos de depuración interna.";
    }

    @Override
    public String getPermission() {
        return "permadeath.admin";
    }

    @Override
    public String getUsage() {
        return "/pdc debug <info/toggle/optimize_spawns/health/module/removegaps>";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("Solo jugadores.");
            return;
        }

        if (args.length < 1) {
            p.sendMessage(TextUtils.format(Main.prefix + "&eSub comandos debug: &7info, toggle, optimize_spawns, health, module, removegaps"));
            return;
        }

        switch (args[0].toLowerCase()) {
            case "info" -> {
                p.sendMessage(TextUtils.format(Main.prefix + "&6&lInformación Debug:"));
                p.sendMessage(TextUtils.format("&fDía: &a" + DateManager.getInstance().getDay()));
                p.sendMessage(TextUtils.format("&fServer: &a" + VersionManager.getFormattedVersion()));
                p.sendMessage(TextUtils.format("&fMundos: &a" + plugin.world.getName() + " / " + plugin.endWorld.getName()));
            }
            case "toggle" -> {
                Main.DEBUG = !Main.DEBUG;
                p.sendMessage("Debug Global: " + (Main.DEBUG ? "§aON" : "§cOFF"));
            }
            case "optimize_spawns" -> {
                Main.OPTIMIZE_SPAWNS = !Main.OPTIMIZE_SPAWNS;
                plugin.getConfig().set("Toggles.Optimizar-Mob-Spawns", Main.OPTIMIZE_SPAWNS);
                plugin.saveConfig();
                p.sendMessage(TextUtils.format(Main.prefix + "&eOptimizar spawns: " + (Main.OPTIMIZE_SPAWNS ? "&aActivado" : "&cDesactivado")));
            }
            case "health" -> p.sendMessage("Vida máxima calculada: " + NetheriteArmor.getAvailableMaxHealth(p));
            case "module" -> plugin.getNmsHandler().spawnNMSCustomEntity("DeathModule", org.bukkit.entity.EntityType.ARMOR_STAND, p.getLocation(), org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.CUSTOM);
            case "removegaps" -> {
                p.getPersistentDataContainer().remove(new NamespacedKey(plugin, "hyper_one"));
                p.getPersistentDataContainer().remove(new NamespacedKey(plugin, "hyper_two"));
                p.sendMessage("§aMetadatos de manzanas eliminados.");
            }
            case "beginning" -> plugin.getBeginningManager().debugStructures(p);
            default -> p.sendMessage("§cSubcomando debug no válido.");
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("info", "toggle", "optimize_spawns", "health", "module", "removegaps", "beginning").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return super.tabComplete(sender, args);
    }
}

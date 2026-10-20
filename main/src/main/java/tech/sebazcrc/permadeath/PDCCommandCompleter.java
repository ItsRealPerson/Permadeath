package tech.sebazcrc.permadeath;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.usigtil.stream.Collectors;

public class PDCCommandCompleter implements TabCompleter {

    private static final List<String> PLAYER_SUBCOMMANDS = Arrays.asList(
            "awake", "duracion", "idioma", "mensaje", "dias", "info", "discord", "cambios", "recipes", "accesorios"
    );

    private static final List<String> ADMIN_SUBCOMMANDS = Arrays.asList(
            "cambiarDia", "reload", "debug", "beginning", "speedrun", "give", "afk", "storm", "spawn", "boss", "backup", "resetall", "setupbeginning"
    );

    private static final List<String> GIVE_ITEMS = Arrays.asList(
            "medalla", "netheriteArmor", "infernalArmor", "infernalBlock", 
            "netheriteTools", "lifeOrb", "endrelic", "beginningrelic", 
            "waterMedal", "ancestralFragment", "moldes", "abyssalheart",
            "abyssalmask", "abyssalfilter", "voidshard", "abyssalpotion",
            "infernalelytra", "infernalingot", "abyssalore"
    );

    private static final List<String> MOBS = Arrays.asList(
            "UltraRavager", "SpecialPig", "CustomGiant", "CustomCreeper", 
            "QuantumCreeper", "EnderQuantumCreeper", "CustomCod", 
            "SpecialBee", "CustomGhast", "DeathModule", "QuantumReactor", 
            "PaleParagon", "ArcaneEvoker", "ArcaneBreeze", "SilentSeeker", 
            "SculkParasite", "EchoArcher", "GloomBat", "HollowGuard",
            "AggressiveSnowGolem", "ExplosivePufferfish"
    );

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> subcommands = new ArrayList<>(PLAYER_SUBCOMMANDS);

            if (sender.hasPermission("permadeathcore.admin") || sender.isOp()) {
                subcommands.addAll(ADMIN_SUBCOMMANDS);
                Main instance = Main.getInstance();
                if (instance != null && instance.isAbyssEnabled()) {
                    subcommands.add("abyss");
                }
            }
            if (sender.hasPermission("permadeathcore.locate") || sender.hasPermission("permadeathcore.admin") || sender.isOp()) {
                subcommands.add("locate");
            }
            if (sender.hasPermission("permadeathcore.event") || sender.hasPermission("permadeathcore.admin") || sender.isOp()) {
                subcommands.add("event");
            }

            StringUtil.copyPartialMatches(args[0], subcommands, completions);
        } else if (args.length == 2) {
            String sub = args[0].toLowerCase();
            switch (sub) {
                case "idioma":
                    StringUtil.copyPartialMatches(args[1], Arrays.asList("es", "en"), completions);
                    break;
                case "cambiardia":
                    StringUtil.copyPartialMatches(args[1], Arrays.asList("1", "20", "30", "40", "50", "60", "70"), completions);
                    break;
                case "give":
                    StringUtil.copyPartialMatches(args[1], GIVE_ITEMS, completions);
                    break;
                case "spawn":
                    StringUtil.copyPartialMatches(args[1], MOBS, completions);
                    break;
                case "boss":
                    StringUtil.copyPartialMatches(args[1], Arrays.asList("spawn", "help"), completions);
                    break;
                case "event":
                    StringUtil.copyPartialMatches(args[1], Arrays.asList("shulkershell", "lifeorb"), completions);
                    break;
                case "locate":
                    StringUtil.copyPartialMatches(args[1], Collections.singletonList("beginning"), completions);
                    break;
                case "storm":
                    StringUtil.copyPartialMatches(args[1], Arrays.asList("add", "remove"), completions);
                    break;
                case "afk":
                    StringUtil.copyPartialMatches(args[1], Arrays.asList("unban", "bypass"), completions);
                    break;
                case "debug":
                    StringUtil.copyPartialMatches(args[1], Arrays.asList("info", "toggle", "optimize_spawns", "health", "module", "removegaps"), completions);
                    break;
                case "speedrun":
                    StringUtil.copyPartialMatches(args[1], Arrays.asList("toggle", "tiempo", "reset"), completions);
                    break;
                case "beginning":
                    StringUtil.copyPartialMatches(args[1], Arrays.asList("bendicion", "maldicion"), completions);
                    break;
                case "abyss":
                    if (sender.isOp() || sender.hasPermission("permadeathcore.admin")) {
                        StringUtil.copyPartialMatches(args[1], Collections.singletonList("force"), completions);
                    }
                    break;
                case "resetall":
                    StringUtil.copyPartialMatches(args[1], Collections.singletonList("confirm"), completions);
                    break;
            }
        } else if (args.length == 3) {
            String sub = args[0].toLowerCase();
            if (sub.equals("afk") && args[1].equalsIgnoreCase("unban")) {
                List<String> playerNames = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
                StringUtil.copyPartialMatches(args[2], playerNames, completions);
            } else if (sub.equals("afk") && args[1].equalsIgnoreCase("bypass")) {
                StringUtil.copyPartialMatches(args[2], Arrays.asList("add", "remove"), completions);
            } else if (sub.equals("beginning")) {
                List<String> playerNames = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
                StringUtil.copyPartialMatches(args[2], playerNames, completions);
            } else if (sub.equals("boss") && args[1].equalsIgnoreCase("spawn")) {
                StringUtil.copyPartialMatches(args[2], Collections.singletonList("warden"), completions);
            }
        } else if (args.length == 4) {
            String sub = args[0].toLowerCase();
            if (sub.equals("afk") && args[1].equalsIgnoreCase("bypass")) {
                List<String> playerNames = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
                StringUtil.copyPartialMatches(args[3], playerNames, completions);
            } else if (sub.equals("storm")) {
                StringUtil.copyPartialMatches(args[3], Arrays.asList("h", "m"), completions);
            }
        }

        Collections.sort(completions);
        return completions;
    }
}

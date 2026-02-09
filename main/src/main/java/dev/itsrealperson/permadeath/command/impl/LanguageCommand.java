package dev.itsrealperson.permadeath.command.impl;

import dev.itsrealperson.permadeath.api.Language;
import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.command.SubCommand;
import dev.itsrealperson.permadeath.data.PlayerDataManager;
import dev.itsrealperson.permadeath.util.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LanguageCommand extends SubCommand {

    private final Main plugin;

    public LanguageCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "idioma";
    }

    @Override
    public String getDescription() {
        return "Cambia tu idioma personal.";
    }

    @Override
    public String getUsage() {
        return "/pdc idioma <es/en>";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Solo jugadores pueden ejecutar este comando.");
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(TextUtils.format("&ePor favor ingresa un idioma. &7Ejemplo: &b/pdc idioma es &e<es, en>"));
            return;
        }

        String lang = args[1].toLowerCase();
        PlayerDataManager data = new PlayerDataManager(player.getName(), plugin);
        
        if (lang.equals("es")) {
            if (data.getLanguage() == Language.SPANISH) {
                player.sendMessage(TextUtils.format("&c¡Ya estás usando el idioma español!"));
                return;
            }
            data.setLanguage(Language.SPANISH);
            player.sendMessage(TextUtils.format("&eHas cambiado tu idioma a: &bEspañol"));
        } else if (lang.equals("en")) {
            if (data.getLanguage() == Language.ENGLISH) {
                player.sendMessage(TextUtils.format("&cYou are already using English!"));
                return;
            }
            data.setLanguage(Language.ENGLISH);
            player.sendMessage(TextUtils.format("&eYou have changed your language to: &bEnglish"));
        } else {
            player.sendMessage(TextUtils.format("&cIdioma no reconocido. Usa 'es' o 'en'."));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return Arrays.asList("es", "en");
        }
        return Collections.emptyList();
    }
}

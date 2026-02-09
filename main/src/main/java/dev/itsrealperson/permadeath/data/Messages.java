package dev.itsrealperson.permadeath.data;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import dev.itsrealperson.permadeath.api.Language;
import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.util.lib.FileAPI;
import dev.itsrealperson.permadeath.util.TextUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Messages {
    private final Main instance;
    private final Map<Language, Map<String, String>> cache = new ConcurrentHashMap<>();

    public Messages(Main instance) {
        this.instance = instance;
        reloadFiles();
    }

    public void reloadFiles() {
        cache.clear();
        
        // Extraer archivos si no existen
        new FileAPI.FileOut(instance, "mensajes_ES", "mensajes/", false);
        new FileAPI.FileOut(instance, "mensajes_EN", "mensajes/", false);
        
        loadLanguage(Language.SPANISH);
        loadLanguage(Language.ENGLISH);
    }

    private void loadLanguage(Language lang) {
        File f = getByLang(lang);
        if (f == null || !f.exists()) return;

        FileConfiguration config = YamlConfiguration.loadConfiguration(f);
        Map<String, String> langMap = new HashMap<>();

        for (String key : config.getKeys(true)) {
            if (config.isString(key)) {
                langMap.put(key, config.getString(key));
            }
        }
        cache.put(lang, langMap);
    }

    public String getMessageByPlayer(String path, String playerName, java.util.List replaces) {
        String msg = getRawMessage(playerName, path);

        if (replaces != null) {
            for (Object o : replaces) {
                String s = String.valueOf(o);
                String[] l = s.split(";;;");
                if (l.length >= 2) {
                    msg = msg.replace(l[0], l[1]);
                }
            }
        }

        return TextUtils.format(msg);
    }

    public String getMessageByPlayer(String path, String playerName) {
        return TextUtils.format(getRawMessage(playerName, path));
    }

    public String getMessageForConsole(String path) {
        Map<String, String> langMap = cache.get(Language.SPANISH);
        String msg = (langMap != null) ? langMap.get(path) : null;
        return TextUtils.format(msg != null ? msg : "Missing Message: " + path);
    }

    private String getRawMessage(String playerName, String path) {
        PlayerDataManager data = new PlayerDataManager(playerName, instance);
        Language lang = data.getLanguage();
        
        Map<String, String> langMap = cache.get(lang);
        if (langMap == null) return "Missing Lang: " + lang;
        
        String msg = langMap.get(path);
        return (msg != null) ? msg : "Missing Message: " + path;
    }

    private File getByLang(Language lang) {
        String fileName = (lang == Language.SPANISH) ? "mensajes_ES.yml" : "mensajes_EN.yml";
        return new File(instance.getDataFolder(), "mensajes/" + fileName);
    }

    public String getMessage(String path, Player player) {
        return getMessageByPlayer("Server-Messages." + path, player.getName());
    }

    public String getMessage(String path, Player player, List l) {
        return getMessageByPlayer("Server-Messages." + path, player.getName(), l);
    }

    public String getMsgForConsole(String path) {
        return getMessageForConsole("Server-Messages." + path);
    }

    public void sendConsole(String mensaje) {
        Bukkit.getConsoleSender().sendMessage(mensaje);
    }
}
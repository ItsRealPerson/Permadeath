package tech.sebazcrc.permadeath.data;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import tech.sebazcrc.permadeath.Language;
import tech.sebazcrc.permadeath.Main;
import tech.sebazcrc.permadeath.util.lib.FileAPI;
import tech.sebazcrc.permadeath.util.TextUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class Messages {
    private Main instance;
    private FileConfiguration configES;
    private FileConfiguration configEN;
    private final java.util.Map<String, Language> langCache = new java.util.concurrent.ConcurrentHashMap<>();

    public Messages(Main instance) {
        this.instance = instance;

        saveDataES();
        saveDataEN();
        reloadFiles();
    }

    public void saveDataES() {

        new FileAPI.FileOut(instance, "mensajes_ES", "mensajes/", false);

        File f = new File(instance.getDataFolder(), "mensajes/mensajes_ES.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(f);

        FileAPI.UtilFile c = FileAPI.select(instance, f, config);

        c.set("Server-Messages.coords-msg-enable", true);
        c.set("Server-Messages.OnJoin", "&e%player% se ha unido al servidor.");
        c.set("Server-Messages.OnLeave", "&e%player% ha abandonado el servidor.");
        c.set("Server-Messages.StormEnd", "&cLa tormenta ha llegado a su fin.");
        c.set("Server-Messages.Sleep", "&7%player% &efue a dormir.");
        c.set("Server-Messages.Sleeping", "&7%player% &eestá durmiendo &e(&b%players%&7/&b%needed%&e)");
        c.set("Server-Messages.DeathMessageTitle", "&c¡Permadeath!");
        c.set("Server-Messages.DeathMessageSubtitle", "%player% ha muerto");
        c.set("Server-Messages.DeathMessageChat", "&c&lEste es el comienzo del sufrimiento eterno de &4&l%player%&c&l. ¡HA SIDO PERMABANEADO!");
        c.set("Server-Messages.DeathTrainMessage", "&c¡Comienza el Death Train con duración de %tiempo%!");
        c.set("Server-Messages.DeathTrainMessageMinutes", "&c¡Comienza el Death Train con duración de %tiempo%!");
        c.set("Server-Messages.DeathTrainIncrease", "&c¡Aumenta el Death Train a %tiempo%!");
        c.set("Server-Messages.ActionBarMessage", "&7Quedan %tiempo% de tormenta");

        c.save();
        c.load();
    }

    public void saveDataEN() {

        new FileAPI.FileOut(instance, "mensajes_EN", "mensajes/", false);

        File f = new File(instance.getDataFolder(), "mensajes/mensajes_EN.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(f);

        FileAPI.UtilFile c = FileAPI.select(instance, f, config);

        c.set("Server-Messages.OnJoin", "&e%player% joined the game.");
        c.set("Server-Messages.OnLeave", "&e%player% left the game.");
        c.set("Server-Messages.StormEnd", "&cThe storm has been ended.");
        c.set("Server-Messages.Sleep", "&7%player% &ewent to sleep, Sweet dreams!.");
        c.set("Server-Messages.Sleeping", "&7%player% &eis sleeping &e(&b%players%&7/&b%needed%&e)");
        c.set("Server-Messages.DeathMessageTitle", "&cPermadeath!");
        c.set("Server-Messages.DeathMessageSubtitle", "%player% died");
        c.set("Server-Messages.DeathMessageChat", "&c&lThis is the beginning of the eternal suffering of &4&l%player%&c&l. HAS BEEN PERMA-BANNED!");
        c.set("Server-Messages.DeathTrainMessage", "&cStarting the Death Train with a duration of %tiempo%!");
        c.set("Server-Messages.DeathTrainMessageMinutes", "&cStarting the Death Train with a duration of %tiempo%!");
        c.set("Server-Messages.DeathTrainIncrease", "&cThe Death Train increases to %tiempo%!");
        c.set("Server-Messages.ActionBarMessage", "&7%tiempo% storm left");

        c.save();
        c.load();
    }

    public void reloadFiles() {
        new FileAPI.FileOut(instance, "mensajes_ES", "mensajes/", false);
        File fES = new File(instance.getDataFolder(), "mensajes/mensajes_ES.yml");
        if (!fES.exists()) saveDataES();
        this.configES = YamlConfiguration.loadConfiguration(fES);

        new FileAPI.FileOut(instance, "mensajes_EN", "mensajes/", false);
        File fEN = new File(instance.getDataFolder(), "mensajes/mensajes_EN.yml");
        if (!fEN.exists()) saveDataEN();
        this.configEN = YamlConfiguration.loadConfiguration(fEN);

        this.langCache.clear();
    }

    public FileConfiguration getConfigByLang(Language lang) {
        if (lang == Language.ENGLISH) {
            if (configEN == null) {
                File f = new File(instance.getDataFolder(), "mensajes/mensajes_EN.yml");
                configEN = YamlConfiguration.loadConfiguration(f);
            }
            return configEN;
        }
        if (configES == null) {
            File f = new File(instance.getDataFolder(), "mensajes/mensajes_ES.yml");
            configES = YamlConfiguration.loadConfiguration(f);
        }
        return configES;
    }

    public Language getPlayerLanguage(String playerName) {
        return langCache.computeIfAbsent(playerName, name -> {
            PlayerDataManager data = new PlayerDataManager(name, instance);
            return data.getLanguage();
        });
    }

    public void setPlayerLanguageCache(String playerName, Language lang) {
        langCache.put(playerName, lang);
    }

    public String getMessageByPlayer(String path, String playerName, java.util.List replaces) {

        HashMap<String, Object> r = new HashMap<>();

        for (Object o : replaces) {
            String s = String.valueOf(o);
            String[] l = s.split(";;;");
            if (l.length >= 2) {
                r.put(l[0], l[1]);
            }
        }

        Language lang = getPlayerLanguage(playerName);
        FileConfiguration c = getConfigByLang(lang);

        String returning = c.getString(path);
        if (returning == null) return "Missing Message: " + path;

        for (String a : r.keySet()) {
            String value = String.valueOf(r.get(a));
            returning = returning.replace(a, value);
        }

        return TextUtils.format(returning);
    }

    public String getMessageForConsole(String path) {

        Language lang = Language.SPANISH;
        FileConfiguration c = getConfigByLang(lang);

        String returning = c.getString(path);
        if (returning == null) return "Missing Message: " + path;

        return TextUtils.format(returning);
    }

    public String getMessageByPlayer(String path, String playerName) {

        Language lang = getPlayerLanguage(playerName);
        FileConfiguration c = getConfigByLang(lang);

        String returning = c.getString(path);
        if (returning == null) return "Missing Message: " + path;

        return TextUtils.format(returning);
    }

    private File getByLang(Language lang) {

        if (lang == Language.SPANISH) {

            return new File(instance.getDataFolder(), "mensajes/mensajes_ES.yml");
        } else if (lang == Language.ENGLISH) {

            return new File(instance.getDataFolder(), "mensajes/mensajes_EN.yml");
        }

        return null;
    }


    public String getMessage(String path, Player player) {

        return instance.getMessages().getMessageByPlayer("Server-Messages." + path, player.getName());
    }

    public String getMessage(String path, Player player, List l) {

        return instance.getMessages().getMessageByPlayer("Server-Messages." + path, player.getName(), l);
    }

    public String getMsgForConsole(String path) {

        return instance.getMessages().getMessageForConsole("Server-Messages." + path);
    }

    public void sendConsole(String mensaje) {

        Bukkit.getConsoleSender().sendMessage(mensaje);
    }
}


















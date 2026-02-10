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
        
        // Extraer y asegurar contenido de los archivos
        ensureLanguageFiles();
        
        loadLanguage(Language.SPANISH);
        loadLanguage(Language.ENGLISH);
    }

    private void ensureLanguageFiles() {
        // Asegurar carpeta lang
        File langDir = new File(instance.getDataFolder(), "lang");
        if (!langDir.exists()) langDir.mkdirs();

        // Asegurar mensajes en Español
        File fEs = new File(instance.getDataFolder(), "lang/mensajes_ES.yml");
        new FileAPI.FileOut(instance, "mensajes_ES.yml", "lang/", false);
        FileConfiguration cEs = YamlConfiguration.loadConfiguration(fEs);
        
        boolean changedEs = false;
        // Mensajes de Servidor
        changedEs |= addIfMissing(cEs, "Server-Messages.OnJoin", "&e%player% se ha unido al servidor.");
        changedEs |= addIfMissing(cEs, "Server-Messages.OnLeave", "&e%player% ha abandonado el servidor.");
        changedEs |= addIfMissing(cEs, "Server-Messages.StormEnd", "&cLa tormenta ha llegado a su fin.");
        changedEs |= addIfMissing(cEs, "Server-Messages.UHCMode", "&e¡Ha comenzado el modo UHC!");
        changedEs |= addIfMissing(cEs, "Server-Messages.PortalGenerated", "&e¡Se ha generado un portal a &b&lThe Beginning &een &b%coords%&e!");
        changedEs |= addIfMissing(cEs, "Server-Messages.DeathMessageChat", "&c&lEste es el comienzo del sufrimiento eterno de &4&l%player%&c&l. ¡HA SIDO PERMABANEADO!");
        changedEs |= addIfMissing(cEs, "Server-Messages.DeathMessageTitle", "&c¡Permadeath!");
        changedEs |= addIfMissing(cEs, "Server-Messages.DeathMessageSubtitle", "%player% ha muerto");
        changedEs |= addIfMissing(cEs, "Server-Messages.DeathTrainMessage", "&c¡Comienza el Death Train con duración de %tiempo%!");
        changedEs |= addIfMissing(cEs, "Server-Messages.DeathTrainIncrease", "&c¡Aumenta el Death Train a %tiempo%!");
        changedEs |= addIfMissing(cEs, "Server-Messages.ActionBarMessage", "&7Quedan %tiempo% de tormenta");
        changedEs |= addIfMissing(cEs, "Server-Messages.Sleep", "&eEl jugador %player% está durmiendo. (&b%actual%&7/&b%necesarios%&e)");
        
        // Entidades
        changedEs |= addIfMissing(cEs, "Entities.UltraRavager", "&6Ultra Ravager");
        changedEs |= addIfMissing(cEs, "Entities.SupernovaCat", "&6Gato Supernova");
        changedEs |= addIfMissing(cEs, "Entities.GalacticCat", "&6Gato Galáctico");
        changedEs |= addIfMissing(cEs, "Entities.QuantumCreeper", "&6Quantum Creeper");
        changedEs |= addIfMissing(cEs, "Entities.TwistedWarden", "&3Twisted Warden");
        
        // Ítems
        changedEs |= addIfMissing(cEs, "Items.LifeOrb", "&6Orbe de Vida");
        changedEs |= addIfMissing(cEs, "Items.BeginningRelic", "&6Reliquia del Comienzo");
        changedEs |= addIfMissing(cEs, "Items.EndRelic", "&6Reliquia Del Fin");
        changedEs |= addIfMissing(cEs, "Items.WaterMedal", "&bMedalla de Agua");
        changedEs |= addIfMissing(cEs, "Items.AbyssalMask", "&bMáscara del Abismo");
        
        // Dimensiones y Mecánicas
        changedEs |= addIfMissing(cEs, "Dimensions.AbyssSealed", "&cEl Abismo está sellado. Necesitas despertar el Corazón del Abismo.");
        changedEs |= addIfMissing(cEs, "Dimensions.AbyssNotDiscovered", "&cEl Abismo aún no ha sido descubierto.");
        changedEs |= addIfMissing(cEs, "Dimensions.AbyssWelcome", "&b&l¡Bienvenido al Abismo!");
        changedEs |= addIfMissing(cEs, "Dimensions.BeginningWelcome", "&eBienvenido a &b&lThe Beginning&e.");
        changedEs |= addIfMissing(cEs, "Dimensions.BeginningClosed", "&cThe Beginning está cerrado actualmente.");
        
        // Alertas
        changedEs |= addIfMissing(cEs, "Alerts.AcidWater", "&c¡El agua está altamente contaminada! Necesitas la Medalla de Agua.");
        changedEs |= addIfMissing(cEs, "Alerts.OxygenOut", "&c¡OXÍGENO AGOTADO!");
        changedEs |= addIfMissing(cEs, "Alerts.AbyssImmunity", "&bHas ganado inmunidad a la presión abisal por 2 minutos.");
        
        if (changedEs) {
            try { cEs.save(fEs); } catch (Exception e) { e.printStackTrace(); }
        }

        // Asegurar mensajes en Inglés
        File fEn = new File(instance.getDataFolder(), "lang/mensajes_EN.yml");
        new FileAPI.FileOut(instance, "mensajes_EN.yml", "lang/", false);
        FileConfiguration cEn = YamlConfiguration.loadConfiguration(fEn);
        
        boolean changedEn = false;
        // Server Messages
        changedEn |= addIfMissing(cEn, "Server-Messages.OnJoin", "&e%player% joined the game.");
        changedEn |= addIfMissing(cEn, "Server-Messages.OnLeave", "&e%player% left the game.");
        changedEn |= addIfMissing(cEn, "Server-Messages.StormEnd", "&cThe storm has ended.");
        changedEn |= addIfMissing(cEn, "Server-Messages.UHCMode", "&eUHC Mode has started!");
        changedEn |= addIfMissing(cEn, "Server-Messages.PortalGenerated", "&eA portal to &b&lThe Beginning &ehas generated at &b%coords%&e!");
        changedEn |= addIfMissing(cEn, "Server-Messages.DeathMessageChat", "&c&lThis is the beginning of &4&l%player%&c&l's eternal suffering. HAS BEEN PERMA-BANNED!");
        changedEn |= addIfMissing(cEn, "Server-Messages.DeathMessageTitle", "&cPermadeath!");
        changedEn |= addIfMissing(cEn, "Server-Messages.DeathMessageSubtitle", "%player% has died");
        changedEn |= addIfMissing(cEn, "Server-Messages.DeathTrainMessage", "&cThe Death Train begins with a duration of %tiempo%!");
        changedEn |= addIfMissing(cEn, "Server-Messages.DeathTrainIncrease", "&cThe Death Train increases to %tiempo%!");
        changedEn |= addIfMissing(cEn, "Server-Messages.ActionBarMessage", "&7%tiempo% of storm remaining");
        changedEn |= addIfMissing(cEn, "Server-Messages.Sleep", "&ePlayer %player% is sleeping. (&b%actual%&7/&b%necesarios%&e)");
        
        // Entities
        changedEn |= addIfMissing(cEn, "Entities.UltraRavager", "&6Ultra Ravager");
        changedEn |= addIfMissing(cEn, "Entities.SupernovaCat", "&6Supernova Cat");
        changedEn |= addIfMissing(cEn, "Entities.GalacticCat", "&6Galactic Cat");
        changedEn |= addIfMissing(cEn, "Entities.QuantumCreeper", "&6Quantum Creeper");
        changedEn |= addIfMissing(cEn, "Entities.TwistedWarden", "&3Twisted Warden");

        // Items
        changedEn |= addIfMissing(cEn, "Items.LifeOrb", "&6Life Orb");
        changedEn |= addIfMissing(cEn, "Items.BeginningRelic", "&6Beginning Relic");
        changedEn |= addIfMissing(cEn, "Items.EndRelic", "&6End Relic");
        changedEn |= addIfMissing(cEn, "Items.WaterMedal", "&bWater Medal");
        changedEn |= addIfMissing(cEn, "Items.AbyssalMask", "&bAbyssal Mask");

        // Dimensions
        changedEn |= addIfMissing(cEn, "Dimensions.AbyssSealed", "&cThe Abyss is sealed. You need to awaken the Heart of the Abyss.");
        changedEn |= addIfMissing(cEn, "Dimensions.AbyssNotDiscovered", "&cThe Abyss has not been discovered yet.");
        changedEn |= addIfMissing(cEn, "Dimensions.AbyssWelcome", "&b&lWelcome to The Abyss!");
        changedEn |= addIfMissing(cEn, "Dimensions.BeginningWelcome", "&eWelcome to &b&lThe Beginning&e.");
        changedEn |= addIfMissing(cEn, "Dimensions.BeginningClosed", "&cThe Beginning is currently closed.");

        // Alerts
        changedEn |= addIfMissing(cEn, "Alerts.AcidWater", "&cThe water is highly contaminated! You need the Water Medal.");
        changedEn |= addIfMissing(cEn, "Alerts.OxygenOut", "&cOXYGEN EXHAUSTED!");
        changedEn |= addIfMissing(cEn, "Alerts.AbyssImmunity", "&bYou have gained immunity to abyssal pressure for 2 minutes.");
        
        if (changedEn) {
            try { cEn.save(fEn); } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private boolean addIfMissing(FileConfiguration config, String path, Object value) {
        if (!config.contains(path)) {
            config.set(path, value);
            return true;
        }
        return false;
    }

    public String getCustomMessage(String path, Player player) {
        PlayerDataManager data = new PlayerDataManager(player.getName(), instance);
        Language lang = data.getLanguage();
        Map<String, String> langMap = cache.get(lang);
        if (langMap == null) return "Missing Lang: " + lang;
        String msg = langMap.get(path);
        return TextUtils.format(msg != null ? msg : "Missing: " + path);
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
        return new File(instance.getDataFolder(), "lang/" + fileName);
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

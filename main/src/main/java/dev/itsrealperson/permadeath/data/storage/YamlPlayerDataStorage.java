package dev.itsrealperson.permadeath.data.storage;

import dev.itsrealperson.permadeath.api.Language;
import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.api.storage.PlayerData;
import dev.itsrealperson.permadeath.api.storage.PlayerDataStorage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class YamlPlayerDataStorage implements PlayerDataStorage {

    private final Main plugin;
    private File file;
    private FileConfiguration config;

    public YamlPlayerDataStorage(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void init() throws Exception {
        this.file = new File(plugin.getDataFolder(), "jugadores.yml");
        if (!file.exists()) {
            file.createNewFile();
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public void savePlayer(PlayerData data) {
        String path = "Players." + data.getName();
        config.set(path + ".UUID", data.getUuid() != null ? data.getUuid().toString() : "");
        config.set(path + ".banDay", data.getBanDay());
        config.set(path + ".banTime", data.getBanTime());
        config.set(path + ".banCause", data.getBanCause());
        config.set(path + ".coords", data.getCoords());
        config.set(path + ".HP", data.getExtraHP());
        config.set(path + ".Idioma", data.getLanguage().name());
        config.set(path + ".LastDay", data.getLastDay());
        save();
    }

    @Override
    public Optional<PlayerData> loadPlayer(String name) {
        if (!config.contains("Players." + name)) {
            return Optional.empty();
        }

        String path = "Players." + name;
        PlayerData data = PlayerData.builder()
                .name(name)
                .uuid(config.getString(path + ".UUID", "").isEmpty() ? null : UUID.fromString(config.getString(path + ".UUID")))
                .banDay(config.getString(path + ".banDay", ""))
                .banTime(config.getString(path + ".banTime", ""))
                .banCause(config.getString(path + ".banCause", ""))
                .coords(config.getString(path + ".coords", ""))
                .extraHP(config.getInt(path + ".HP", 0))
                .language(Language.valueOf(config.getString(path + ".Idioma", "SPANISH")))
                .lastDay(config.getLong(path + ".LastDay", 0))
                .build();

        return Optional.of(data);
    }

    @Override
    public Collection<String> getSavedPlayers() {
        if (!config.contains("Players")) return Collections.emptyList();
        return config.getConfigurationSection("Players").getKeys(false);
    }

    @Override
    public void close() {
        save();
    }

    private void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

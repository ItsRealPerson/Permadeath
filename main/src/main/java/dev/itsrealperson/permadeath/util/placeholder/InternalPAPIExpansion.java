package dev.itsrealperson.permadeath.util.placeholder;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.data.DateManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class InternalPAPIExpansion extends PlaceholderExpansion {
    private final Main plugin;

    public InternalPAPIExpansion(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() { return "permadeath"; }
    @Override
    public @NotNull String getAuthor() { return "ItsRealPerson"; }
    @Override
    public @NotNull String getVersion() { return plugin.getDescription().getVersion(); }
    @Override
    public boolean persist() { return true; }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (params.equalsIgnoreCase("day")) return String.valueOf(DateManager.getInstance().getDay());
        if (params.equalsIgnoreCase("prefix")) return Main.prefix;
        if (params.equalsIgnoreCase("playtime")) return dev.itsrealperson.permadeath.util.TextUtils.formatInterval(plugin.getPlayTime());
        if (params.equalsIgnoreCase("is_death_train")) return String.valueOf(plugin.world != null && plugin.world.hasStorm());
        if (params.equalsIgnoreCase("death_train_time")) return plugin.world != null ? dev.itsrealperson.permadeath.util.TextUtils.formatInterval(plugin.world.getWeatherDuration() / 20) : "00:00";
        
        return null;
    }
}

package dev.itsrealperson.permadeath.event.impl;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.api.EventManagerAPI;
import dev.itsrealperson.permadeath.util.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Objects;

public class EventManagerImpl implements EventManagerAPI {

    private final Main plugin;

    public EventManagerImpl(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void startDeathTrain(int durationTicks) {
        plugin.world.setStorm(true);
        plugin.world.setThundering(true);
        plugin.world.setThunderDuration(durationTicks);
        plugin.world.setWeatherDuration(durationTicks);

        plugin.incrementDeathTrainVersion();
        
        // Disparar evento API
        Bukkit.getPluginManager().callEvent(new dev.itsrealperson.permadeath.api.event.DeathTrainEvent(plugin.getDeathTrainVersion(), true));

        // Notificar
        int totalSeconds = durationTicks / 20;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        String timeStr = (hours > 0 ? hours + "h " : "") + minutes + "m";

        Bukkit.broadcastMessage(TextUtils.format(Main.prefix + "&c¡Ha comenzado un Death Train manual por &f" + timeStr + "&c!"));
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), Sound.ENTITY_SKELETON_HORSE_DEATH, 10, 1);
        }
    }

    @Override
    public boolean isLifeOrbActive() {
        return plugin.getOrbEvent() != null && plugin.getOrbEvent().isRunning();
    }

    @Override
    public void setLifeOrbActive(boolean active) {
        if (plugin.getOrbEvent() == null) return;
        plugin.getOrbEvent().setRunning(active);
        if (active) {
            Bukkit.getOnlinePlayers().forEach(plugin.getOrbEvent()::addPlayer);
        } else {
            plugin.getOrbEvent().getBossBar().removeAll();
            plugin.getOrbEvent().clearPlayers();
        }
    }
}

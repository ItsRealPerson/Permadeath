package dev.itsrealperson.permadeath.world;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.api.PermadeathModule;
import dev.itsrealperson.permadeath.data.DateManager;
import dev.itsrealperson.permadeath.util.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.command.CommandSender;

import java.util.SplittableRandom;

/**
 * Módulo encargado de los eventos globales temporizados (Life Orb y Shulker Shells).
 */
public class EventModule implements PermadeathModule {

    private final Main plugin;
    private final SplittableRandom random = new SplittableRandom();
    private int tickCounter = 0;

    public EventModule(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "EventModule";
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onTick() {
        tickCounter++;
        
        // La lógica de eventos temporales se ejecuta cada 20 ticks (1 segundo)
        if (tickCounter % 20 == 0) {
            handleLifeOrbEvent();
            handleShulkerEvent();
        }
    }

    private void handleLifeOrbEvent() {
        if (plugin.getOrbEvent() == null) return;

        // Auto-inicio del evento si es día 60
        if (DateManager.getInstance().getDay() >= 60 && 
            !plugin.getConfig().getBoolean("DontTouch.Event.LifeOrbEnded") && 
            !plugin.getOrbEvent().isRunning()) {
            
            if (Main.SPEED_RUN_MODE) plugin.getOrbEvent().setTimeLeft(60 * 8);
            plugin.getOrbEvent().setRunning(true);
            Bukkit.getOnlinePlayers().forEach(plugin.getOrbEvent()::addPlayer);
        }

        if (plugin.getOrbEvent().isRunning()) {
            if (plugin.getOrbEvent().getTimeLeft() > 0) {
                plugin.getOrbEvent().reduceTime();

                // Guardar cada minuto
                if (plugin.getOrbEvent().getTimeLeft() % 60 == 0) {
                    plugin.getOrbEvent().saveTime();
                }

                String timeStr = formatTime(plugin.getOrbEvent().getTimeLeft());
                plugin.getOrbEvent().getBossBar().setColor(BarColor.values()[random.nextInt(BarColor.values().length)]);
                plugin.getOrbEvent().setTitle(TextUtils.format("&6&l" + timeStr + " para obtener el Life Orb"));
            } else {
                finishLifeOrbEvent();
            }
        }
    }

    private void finishLifeOrbEvent() {
        Bukkit.broadcastMessage(TextUtils.format(Main.prefix + "&cSe ha acabado el tiempo para obtener el Life Orb, ¡sufrid! ahora tendréis 8 contenedores de vida menos."));
        plugin.getOrbEvent().setRunning(false);
        plugin.getOrbEvent().clearPlayers();
        plugin.getOrbEvent().setTimeLeft((Main.SPEED_RUN_MODE ? 60 * 8 : 60 * 60 * 8));
        plugin.getOrbEvent().getBossBar().removeAll();

        plugin.getConfig().set("DontTouch.Event.LifeOrbEnded", true);
        plugin.saveConfig();
    }

    private void handleShulkerEvent() {
        if (plugin.getShulkerEvent() == null || !plugin.getShulkerEvent().isRunning()) return;

        if (plugin.getShulkerEvent().getTimeLeft() > 0) {
            plugin.getShulkerEvent().setTimeLeft(plugin.getShulkerEvent().getTimeLeft() - 1);
            String timeStr = formatTime(plugin.getShulkerEvent().getTimeLeft());
            plugin.getShulkerEvent().setTitle(TextUtils.format("&e&lX2 Shulker Shells: &b&n" + timeStr));
        } else {
            Bukkit.broadcastMessage(TextUtils.format(Main.prefix + "&eEl evento de &c&lX2 Shulker Shells &eha acabado."));
            plugin.getShulkerEvent().setRunning(false);
            plugin.getShulkerEvent().clearPlayers();
            plugin.getShulkerEvent().setTimeLeft(60 * 60 * 4);
            plugin.getShulkerEvent().getBossBar().removeAll();
        }
    }

    private String formatTime(int totalSeconds) {
        int hrs = totalSeconds / 3600;
        int minAndSec = totalSeconds % 3600;
        int min = minAndSec / 60;
        int sec = minAndSec % 60;
        return String.format("%02d:%02d:%02d", hrs, min, sec);
    }
}

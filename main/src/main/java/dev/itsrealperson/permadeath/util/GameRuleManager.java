package dev.itsrealperson.permadeath.util;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.World;
import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.data.DateManager;

public class GameRuleManager {

    private final Main plugin;

    public GameRuleManager(Main plugin) {
        this.plugin = plugin;
    }

    public void applyRules() {
        Runnable task = () -> {
            for (World world : Bukkit.getWorlds()) {
                // Reglas base para Permadeath
                if (world.getDifficulty() != Difficulty.HARD) {
                    world.setDifficulty(Difficulty.HARD);
                    plugin.getLogger().info("Dificultad establecida a HARD en el mundo: " + world.getName());
                }

                world.setGameRule(GameRule.KEEP_INVENTORY, false);
                world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, true);

                // Reglas UHC (Día 50+)
                if (DateManager.getInstance().getDay() >= 50) {
                    world.setGameRule(GameRule.NATURAL_REGENERATION, false);
                } else {
                    world.setGameRule(GameRule.NATURAL_REGENERATION, true);
                }
            }
        };

        if (Main.isRunningFolia()) {
            Bukkit.getGlobalRegionScheduler().execute(plugin, task);
        } else {
            task.run();
        }
    }
}

package dev.itsrealperson.permadeath;

import dev.itsrealperson.permadeath.api.PermadeathModule;
import dev.itsrealperson.permadeath.util.item.NetheriteArmor;
import dev.itsrealperson.permadeath.util.item.PermadeathItems;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/**
 * Módulo encargado de la gestión de salud, armaduras y bloqueo de slots.
 */
public class HealthModule implements PermadeathModule {

    private final Main plugin;
    private int tickCounter = 0;

    public HealthModule(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "HealthModule";
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
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getGameMode() == GameMode.SPECTATOR || !player.isOnline()) continue;

            // SlotBlock es una mecánica de inventario, la ejecutamos cada 20 ticks (1s) para ahorrar CPU
            // En el original corría cada tick, pero 1s es suficiente para detectar reliquias.
            if (tickCounter % 20 == 0) {
                PermadeathItems.slotBlock(player);
                
                // La salud también se puede actualizar cada segundo sin afectar la experiencia de juego
                NetheriteArmor.setupHealth(player);
            }
        }
    }
}

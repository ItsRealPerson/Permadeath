package dev.itsrealperson.permadeath.world;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.api.PermadeathModule;
import dev.itsrealperson.permadeath.data.DateManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Ravager;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Módulo encargado de la lógica global de entidades y mobs especiales.
 */
public class MobModule implements PermadeathModule {

    private final Main plugin;
    private final NamespacedKey ultraRavagerKey;
    private int tickCounter = 0;

    public MobModule(Main plugin) {
        this.plugin = plugin;
        this.ultraRavagerKey = new NamespacedKey(plugin, "ultra_ravager");
    }

    @Override
    public String getName() {
        return "MobModule";
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
        
        // La lógica de los UltraRavagers rompiendo bloques se ejecuta cada 5 ticks para ahorrar CPU
        // pero mantener la fluidez visual de la destrucción.
        if (tickCounter % 5 == 0) {
            handleUltraRavagers();
        }
    }

    private void handleUltraRavagers() {
        if (DateManager.getInstance().getDay() < 40) return;

        for (World world : Bukkit.getWorlds()) {
            if (world.getEnvironment() == World.Environment.THE_END) continue;

            for (Ravager ravager : world.getEntitiesByClass(Ravager.class)) {
                if (ravager.getPersistentDataContainer().has(ultraRavagerKey, PersistentDataType.BYTE)) {
                    // Intentar usar la abstracción de la API
                    dev.itsrealperson.permadeath.api.entity.PermadeathEntity custom = 
                        plugin.getNmsAccessor().getCustomEntity(ravager);
                    
                    if (custom instanceof dev.itsrealperson.permadeath.api.entity.UltraRavager ultra) {
                        ultra.performBlockDestruction();
                    } else {
                        processRavagerDestruction(ravager);
                    }
                }
            }
        }
    }

    private void processRavagerDestruction(Ravager ravager) {
        List<Block> sight = ravager.getLineOfSight(null, 5);

        for (Block block : sight) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    for (int k = -1; k <= 1; k++) {
                        Block target = block.getRelative(i, j, k);
                        if (target.getType() == Material.NETHERRACK) {
                            target.setType(Material.AIR);
                            target.getWorld().playSound(target.getLocation(), Sound.BLOCK_STONE_BREAK, 2.0F, 1.0F);
                        }
                    }
                }
            }
        }
    }
}

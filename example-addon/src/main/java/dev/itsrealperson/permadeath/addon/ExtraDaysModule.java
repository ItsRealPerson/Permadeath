package dev.itsrealperson.permadeath.addon;

import dev.itsrealperson.permadeath.api.PermadeathAPI;
import dev.itsrealperson.permadeath.api.PermadeathModule;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Ejemplo de un módulo de expansión que añade contenido para los días 61 al 120.
 */
public class ExtraDaysModule implements PermadeathModule {

    @Override
    public String getName() {
        return "ExtraDaysExpansion";
    }

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Permadeath-Addon] Lógica de días 61+ inicializada.");
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onTick() {
        long currentDay = PermadeathAPI.getDay();

        // Solo actuar si el día es mayor a 60
        if (currentDay > 60) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                applyExpansionEffects(player, currentDay);
            }
        }
    }

    private void applyExpansionEffects(Player player, long day) {
        // Ejemplo: En el día 90, los jugadores reciben hambre constante
        if (day >= 90) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 40, 0));
        }
        
        // Ejemplo: En el día 120, la debilidad es permanente
        if (day >= 120) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 1));
        }
    }
}

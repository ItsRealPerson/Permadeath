package tech.sebazcrc.permadeath.event.player;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import tech.sebazcrc.permadeath.util.item.PermadeathItems;

public class CustomEnchantmentListener implements Listener {

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        ItemStack result = event.getResult();
        if (result == null) return;

        // Comprobar si el resultado tiene el encantamiento Respiración Abisal
        NamespacedKey abyssalKey = new NamespacedKey("permadeath", "abyssal_breathing");
        
        try {
            // org.bukkit.Registry.ENCHANTMENT.get(key) es la forma correcta en 1.21
            if (result.containsEnchantment(org.bukkit.Registry.ENCHANTMENT.get(abyssalKey))) {
                // Si lo tiene, verificar si el ítem es la Máscara del Abismo
                if (!PermadeathItems.isAbyssalMask(result)) {
                    // Si no es la máscara, borrar el resultado
                    event.setResult(null);
                }
            }
        } catch (Exception ignored) {}
    }
}

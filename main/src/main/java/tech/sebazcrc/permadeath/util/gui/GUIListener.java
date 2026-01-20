package tech.sebazcrc.permadeath.util.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class GUIListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equals(RecipeGUI.GUI_NAME)) {
            e.setCancelled(true);
            
            ItemStack item = e.getCurrentItem();
            if (item == null || item.getType() == Material.AIR) return;
            
            Player p = (Player) e.getWhoClicked();
            
            if (item.getType() == Material.BARRIER) {
                p.closeInventory();
                return;
            }

            if (item.getType() == Material.BOOK && item.getItemMeta().getDisplayName().contains("Menú")) {
                RecipeGUI.openMain(p);
                p.playSound(p.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                return;
            }
            
            // Navegación de Categorías
            if (item.getItemMeta().getPersistentDataContainer().has(RecipeGUI.CAT_KEY, PersistentDataType.STRING)) {
                String cat = item.getItemMeta().getPersistentDataContainer().get(RecipeGUI.CAT_KEY, PersistentDataType.STRING);
                int page = item.getItemMeta().getPersistentDataContainer().getOrDefault(RecipeGUI.PAGE_KEY, PersistentDataType.INTEGER, 0);
                
                RecipeGUI.openCategory(p, cat, page);
                p.playSound(p.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            }
        }
    }
}

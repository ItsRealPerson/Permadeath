package dev.itsrealperson.permadeath.util.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;

/**
 * Gestor centralizado para los menús del editor de configuración.
 */
public class GUIManager implements Listener {

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof AbstractMenu menu) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            menu.handleMenu(event);
        }
    }

    @EventHandler
    public void onMenuOpen(InventoryOpenEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof AbstractMenu menu) {
            menu.handleOpen(event);
        }
    }

    @EventHandler
    public void onMenuClose(InventoryCloseEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof AbstractMenu menu) {
            menu.handleClose(event);
        }
    }
}

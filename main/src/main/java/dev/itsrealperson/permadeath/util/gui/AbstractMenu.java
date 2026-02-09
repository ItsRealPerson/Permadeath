package dev.itsrealperson.permadeath.util.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * Clase base para todos los menús del editor de configuración.
 */
public abstract class AbstractMenu implements InventoryHolder {

    protected Inventory inventory;

    public AbstractMenu(int size, String title) {
        this.inventory = Bukkit.createInventory(this, size, title);
    }

    /**
     * Define los ítems que tendrá el menú.
     */
    public abstract void setMenuItems(Player player);

    /**
     * Lógica al hacer clic en un ítem.
     */
    public abstract void handleMenu(InventoryClickEvent event);

    /**
     * Lógica opcional al abrir el menú.
     */
    public void handleOpen(InventoryOpenEvent event) {}

    /**
     * Lógica opcional al cerrar el menú.
     */
    public void handleClose(InventoryCloseEvent event) {}

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Abre el menú para el jugador.
     */
    public void open(Player player) {
        setMenuItems(player);
        player.openInventory(inventory);
    }
}

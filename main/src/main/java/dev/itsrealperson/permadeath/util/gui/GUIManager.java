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
            
            // Si el Modo Seguro está activo, solo permitimos navegar por los menús
            // (clic en ítems que lleven a otros menús o solo lectura)
            if (AbstractMenu.isSafeMode()) {
                // Comprobamos si el ítem tiene un tag especial de navegación o simplemente bloqueamos todo lo que no sea 'volver'
                // Por ahora, bloqueamos todo cambio administrativo.
                org.bukkit.entity.Player p = (org.bukkit.entity.Player) event.getWhoClicked();
                p.sendMessage(dev.itsrealperson.permadeath.util.TextUtils.format("&e&l[GUI] &cEstás en MODO SEGURO (Solo Lectura)."));
                p.sendMessage(dev.itsrealperson.permadeath.util.TextUtils.format("&7Usa &f/pdc config edit &7para habilitar cambios."));
                p.playSound(p.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
                return;
            }
            
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

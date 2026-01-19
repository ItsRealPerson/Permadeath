package tech.sebazcrc.permadeath.util.inventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import tech.sebazcrc.permadeath.util.TextUtils;
import tech.sebazcrc.permadeath.util.item.PermadeathItems;

public class AccessoryListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = e.getItem();
            if (item != null && item.getType() == Material.NETHER_STAR && item.hasItemMeta() && 
                item.getItemMeta().getDisplayName().contains("Menú de Accesorios")) {
                AccessoryInventory.open(e.getPlayer());
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        ItemStack item = e.getItemDrop().getItemStack();
        if (item.getType() == Material.NETHER_STAR && item.hasItemMeta() && 
            item.getItemMeta().getDisplayName().contains("Menú de Accesorios")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        ItemStack itemInClick = e.getCurrentItem();
        
        // Bloquear movimiento del item de menú
        if (itemInClick != null && itemInClick.getType() == Material.NETHER_STAR && itemInClick.hasItemMeta() && 
            itemInClick.getItemMeta().getDisplayName().contains("Menú de Accesorios")) {
            e.setCancelled(true);
            return;
        }

        if (e.getView().getTitle().equals(TextUtils.format("&8Inventario de Accesorios"))) {
            int slot = e.getRawSlot();
            
            // Si hace click fuera del inventario de arriba, ignorar
            if (slot >= AccessoryInventory.SIZE) return;

            // Bloquear interacción con el fondo (Cristal Gris) o los placeholders especiales
            if (itemInClick != null && itemInClick.hasItemMeta() && itemInClick.getType().name().contains("STAINED_GLASS_PANE")) {
                String name = itemInClick.getItemMeta().getDisplayName();
                if (name.equals(" ") || name.contains("Slot de Orbe") || name.contains("Slot de Medalla") || name.contains("Slot de Reliquia")) {
                    
                    ItemStack cursor = e.getCursor();
                    if (cursor != null && cursor.getType() != Material.AIR) {
                        boolean canPlace = false;
                        if (slot == AccessoryInventory.ACCESSORY_SLOTS[0] && PermadeathItems.isLifeOrb(cursor)) canPlace = true;
                        if (slot == AccessoryInventory.ACCESSORY_SLOTS[1] && PermadeathItems.isWaterMedal(cursor)) canPlace = true;
                        if (slot == AccessoryInventory.ACCESSORY_SLOTS[2] && (PermadeathItems.isEndRelic(cursor) || PermadeathItems.isBeginningRelic(cursor))) canPlace = true;

                        if (canPlace) {
                            e.setCancelled(true);
                            e.getInventory().setItem(slot, cursor.clone());
                            e.setCursor(new ItemStack(Material.AIR));
                            return;
                        }
                    }
                    
                    e.setCancelled(true);
                    return;
                }
            }

            // Verificar si el slot es interactivo
            boolean isAccessory = false;
            int accessoryIndex = -1;
            for (int i = 0; i < AccessoryInventory.ACCESSORY_SLOTS.length; i++) {
                if (AccessoryInventory.ACCESSORY_SLOTS[i] == slot) {
                    isAccessory = true;
                    accessoryIndex = i;
                    break;
                }
            }
            
            boolean isArmor = (slot == AccessoryInventory.HELMET_SLOT || slot == AccessoryInventory.CHEST_SLOT || 
                              slot == AccessoryInventory.LEGS_SLOT || slot == AccessoryInventory.BOOTS_SLOT);

            if (!isAccessory && !isArmor) {
                e.setCancelled(true);
                return;
            }

            if (isAccessory) {
                ItemStack itemToPlace = null;
                if (e.getClick().isShiftClick()) {
                    if (accessoryIndex <= 2) {
                        restorePlaceholder(e.getInventory(), slot, accessoryIndex);
                    }
                } else {
                    itemToPlace = e.getCursor();
                    
                    // Si está sacando un item
                    if ((itemToPlace == null || itemToPlace.getType() == Material.AIR) && (itemInClick != null && itemInClick.getType() != Material.AIR)) {
                        if (accessoryIndex <= 2) {
                            restorePlaceholder(e.getInventory(), slot, accessoryIndex);
                        }
                    }
                }

                if (itemToPlace != null && itemToPlace.getType() != Material.AIR) {
                    if (accessoryIndex == 0) { // Slot Orbe
                        if (!PermadeathItems.isLifeOrb(itemToPlace)) { e.setCancelled(true); return; }
                    } else if (accessoryIndex == 1) { // Slot Medalla Agua
                        if (!PermadeathItems.isWaterMedal(itemToPlace)) { e.setCancelled(true); return; }
                    } else if (accessoryIndex == 2) { // Slot Reliquias
                        if (!PermadeathItems.isEndRelic(itemToPlace) && !PermadeathItems.isBeginningRelic(itemToPlace)) { e.setCancelled(true); return; }
                    } else {
                        // Slots normales: NO permitir Orbe, Medalla de Agua o Reliquias
                        if (PermadeathItems.isLifeOrb(itemToPlace) || PermadeathItems.isWaterMedal(itemToPlace) || 
                            PermadeathItems.isEndRelic(itemToPlace) || PermadeathItems.isBeginningRelic(itemToPlace)) {
                            e.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }

        // Manejar Shift-Click desde el inventario del jugador
        if (e.isShiftClick() && e.getView().getTitle().equals(TextUtils.format("&8Inventario de Accesorios")) && e.getRawSlot() >= AccessoryInventory.SIZE) {
            ItemStack clickedItem = e.getCurrentItem();
            if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                
                int targetIndex = -1;
                if (PermadeathItems.isLifeOrb(clickedItem)) targetIndex = 0;
                else if (PermadeathItems.isWaterMedal(clickedItem)) targetIndex = 1;
                else if (PermadeathItems.isEndRelic(clickedItem) || PermadeathItems.isBeginningRelic(clickedItem)) targetIndex = 2;

                if (targetIndex != -1) {
                    int targetSlot = AccessoryInventory.ACCESSORY_SLOTS[targetIndex];
                    ItemStack current = e.getInventory().getItem(targetSlot);
                    // Solo colocar si el slot tiene un placeholder (indicado por el tipo de cristal)
                    if (current == null || current.getType().name().contains("STAINED_GLASS_PANE")) {
                        e.setCancelled(true);
                        e.getInventory().setItem(targetSlot, clickedItem.clone());
                        clickedItem.setAmount(0);
                    } else {
                        e.setCancelled(true);
                    }
                } else {
                    // Item normal, buscar desde el slot index 3 en adelante
                    boolean found = false;
                    for (int i = 3; i < AccessoryInventory.ACCESSORY_SLOTS.length; i++) {
                        int targetSlot = AccessoryInventory.ACCESSORY_SLOTS[i];
                        ItemStack s = e.getInventory().getItem(targetSlot);
                        if (s == null || s.getType() == Material.AIR) {
                            e.setCancelled(true);
                            e.getInventory().setItem(targetSlot, clickedItem.clone());
                            clickedItem.setAmount(0);
                            found = true;
                            break;
                        }
                    }
                    if (!found) e.setCancelled(true);
                }
            }
        }
    }

    private void restorePlaceholder(org.bukkit.inventory.Inventory inv, int slot, int index) {
        org.bukkit.Bukkit.getScheduler().runTask(tech.sebazcrc.permadeath.Main.getInstance(), () -> {
            Material mat = Material.BROWN_STAINED_GLASS_PANE;
            String name = "&6Slot de Orbe de Vida";
            if (index == 1) { mat = Material.BLUE_STAINED_GLASS_PANE; name = "&bSlot de Medalla de Agua"; }
            else if (index == 2) { mat = Material.LIGHT_BLUE_STAINED_GLASS_PANE; name = "&3Slot de Reliquia"; }
            
            inv.setItem(slot, new tech.sebazcrc.permadeath.util.lib.ItemBuilder(mat).setDisplayName(TextUtils.format(name)).build());
        });
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getView().getTitle().equals(TextUtils.format("&8Inventario de Accesorios"))) {
            AccessoryInventory.save((Player) e.getPlayer(), e.getInventory());
        }
    }
}








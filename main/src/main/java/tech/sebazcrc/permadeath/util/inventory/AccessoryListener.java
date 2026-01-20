package tech.sebazcrc.permadeath.util.inventory;

import org.bukkit.Bukkit;
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
            if (isAccessoryMenu(item)) {
                AccessoryInventory.open(e.getPlayer());
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        ItemStack item = e.getItemDrop().getItemStack();
        if (isAccessoryMenu(item)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        ItemStack itemInClick = e.getCurrentItem();
        
        // Bloquear movimiento del item de menú
        if (isAccessoryMenu(itemInClick)) {
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
                if (name.equals(" ") || name.contains("Slot de Orbe") || name.contains("Slot de Medalla") || name.contains("Slot de Reliquia") || name.contains("Slot de Máscara")) {
                    
                    ItemStack cursor = e.getCursor();
                    if (cursor != null && cursor.getType() != Material.AIR) {
                        boolean canPlace = false;
                        if (slot == AccessoryInventory.ACCESSORY_SLOTS[0] && PermadeathItems.isLifeOrb(cursor)) canPlace = true;
                        if (slot == AccessoryInventory.ACCESSORY_SLOTS[1] && PermadeathItems.isWaterMedal(cursor)) canPlace = true;
                        if (slot == AccessoryInventory.ACCESSORY_SLOTS[2] && (PermadeathItems.isEndRelic(cursor) || PermadeathItems.isBeginningRelic(cursor))) canPlace = true;
                        if (slot == AccessoryInventory.MASK_SLOT && PermadeathItems.isAbyssalMask(cursor)) canPlace = true;

                        if (canPlace) {
                            e.setCancelled(true);
                            e.getInventory().setItem(slot, cursor.clone());
                            e.setCursor(new ItemStack(Material.AIR));
                            Bukkit.getScheduler().runTaskLater(tech.sebazcrc.permadeath.Main.getInstance(), () -> ((Player)e.getWhoClicked()).updateInventory(), 1L);
                            return;
                        }
                    }
                    
                    e.setCancelled(true);
                    return;
                }
            }

            // Bloquear Máscara en el slot de Casco
            if (slot == AccessoryInventory.HELMET_SLOT) {
                if (PermadeathItems.isAbyssalMask(e.getCursor())) {
                    e.setCancelled(true);
                    ((Player)e.getWhoClicked()).sendMessage(TextUtils.format("&cEsta máscara solo funciona en los slots de accesorios."));
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
                    // Solo restaurar si el slot tenía algo y ahora estará vacío (sacando item)
                    if (itemInClick != null && itemInClick.getType() != Material.AIR && !itemInClick.getType().name().contains("STAINED_GLASS_PANE")) {
                        if (accessoryIndex <= 2 || slot == AccessoryInventory.MASK_SLOT) {
                            restorePlaceholder((Player) e.getWhoClicked(), e.getInventory(), slot, accessoryIndex);
                        }
                    }
                } else {
                    itemToPlace = e.getCursor();
                    
                    // Si está sacando un item (cursor aire, click item real)
                    if ((itemToPlace == null || itemToPlace.getType() == Material.AIR) && (itemInClick != null && itemInClick.getType() != Material.AIR && !itemInClick.getType().name().contains("STAINED_GLASS_PANE"))) {
                        if (accessoryIndex <= 2 || slot == AccessoryInventory.MASK_SLOT) {
                            restorePlaceholder((Player) e.getWhoClicked(), e.getInventory(), slot, accessoryIndex);
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
                    } else if (slot == AccessoryInventory.MASK_SLOT) { // Slot Máscara
                        if (!PermadeathItems.isAbyssalMask(itemToPlace)) { e.setCancelled(true); return; }
                    } else {
                        // Slots normales: NO permitir ítems especiales
                        if (PermadeathItems.isLifeOrb(itemToPlace) || PermadeathItems.isWaterMedal(itemToPlace) || 
                            PermadeathItems.isEndRelic(itemToPlace) || PermadeathItems.isBeginningRelic(itemToPlace) || PermadeathItems.isAbyssalMask(itemToPlace)) {
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

    private void restorePlaceholder(org.bukkit.entity.Player player, org.bukkit.inventory.Inventory inv, int slot, int index) {
        Runnable task = () -> {
            Material mat = Material.BROWN_STAINED_GLASS_PANE;
            String name = "&6Slot de Orbe de Vida";
            if (index == 1) { mat = Material.BLUE_STAINED_GLASS_PANE; name = "&bSlot de Medalla de Agua"; }
            else if (index == 2) { mat = Material.LIGHT_BLUE_STAINED_GLASS_PANE; name = "&3Slot de Reliquia"; }
            else if (slot == AccessoryInventory.MASK_SLOT) { mat = Material.BLACK_STAINED_GLASS_PANE; name = "&bSlot de Máscara del Abismo"; }
            
            inv.setItem(slot, new tech.sebazcrc.permadeath.util.lib.ItemBuilder(mat).setDisplayName(TextUtils.format(name)).build());
        };

        if (tech.sebazcrc.permadeath.Main.isRunningFolia()) {
            player.getScheduler().run(tech.sebazcrc.permadeath.Main.getInstance(), t -> task.run(), null);
        } else {
            org.bukkit.Bukkit.getScheduler().runTask(tech.sebazcrc.permadeath.Main.getInstance(), task);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getView().getTitle().equals(TextUtils.format("&8Inventario de Accesorios"))) {
            AccessoryInventory.save((Player) e.getPlayer(), e.getInventory());
        }
    }

    private boolean isAccessoryMenu(ItemStack item) {
        if (item == null || item.getType() != Material.NETHER_STAR) return false;
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return false;
        return item.getItemMeta().getDisplayName().contains("Menú de Accesorios");
    }
}

















package tech.sebazcrc.permadeath.util.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import tech.sebazcrc.permadeath.util.TextUtils;
import tech.sebazcrc.permadeath.util.lib.ItemBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class AccessoryInventory {

    private static final String KEY = "accessory_inventory_v2"; // V2 para el nuevo diseño
    public static final int SIZE = 54;
    
    // Slots de Accesorios (5 Izquierda, 5 Derecha)
    public static final int[] ACCESSORY_SLOTS = {10, 11, 19, 20, 28, 15, 16, 24, 25, 33};
    public static final int MASK_SLOT = 16;
    
    // Slots de Armadura (Centro)
    public static final int HELMET_SLOT = 13;
    public static final int CHEST_SLOT = 22;
    public static final int LEGS_SLOT = 31;
    public static final int BOOTS_SLOT = 40;

    public static void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, SIZE, TextUtils.format("&8Inventario de Accesorios"));
        
        // Rellenar fondo
        ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build();
        for (int i = 0; i < SIZE; i++) {
            inv.setItem(i, filler);
        }

        // Cargar accesorios guardados
        ItemStack[] items = load(player);
        if (items != null) {
            for (int i = 0; i < ACCESSORY_SLOTS.length; i++) {
                if (i < items.length) inv.setItem(ACCESSORY_SLOTS[i], items[i]);
            }
        }

        // Cargar armadura actual del jugador (Sincronización)
        inv.setItem(HELMET_SLOT, player.getInventory().getHelmet());
        inv.setItem(CHEST_SLOT, player.getInventory().getChestplate());
        inv.setItem(LEGS_SLOT, player.getInventory().getLeggings());
        inv.setItem(BOOTS_SLOT, player.getInventory().getBoots());

        // Indicadores para slots especiales
        ItemStack orbFiller = new ItemBuilder(Material.BROWN_STAINED_GLASS_PANE).setDisplayName(TextUtils.format("&6Slot de Orbe de Vida")).build();
        ItemStack medalFiller = new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE).setDisplayName(TextUtils.format("&bSlot de Medalla de Agua")).build();
        ItemStack relicFiller = new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE).setDisplayName(TextUtils.format("&3Slot de Reliquia")).build();
        ItemStack maskFiller = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName(TextUtils.format("&bSlot de Máscara del Abismo")).build();

        if (inv.getItem(ACCESSORY_SLOTS[0]) == null || inv.getItem(ACCESSORY_SLOTS[0]).isSimilar(filler)) inv.setItem(ACCESSORY_SLOTS[0], orbFiller);
        if (inv.getItem(ACCESSORY_SLOTS[1]) == null || inv.getItem(ACCESSORY_SLOTS[1]).isSimilar(filler)) inv.setItem(ACCESSORY_SLOTS[1], medalFiller);
        if (inv.getItem(ACCESSORY_SLOTS[2]) == null || inv.getItem(ACCESSORY_SLOTS[2]).isSimilar(filler)) inv.setItem(ACCESSORY_SLOTS[2], relicFiller);
        if (inv.getItem(MASK_SLOT) == null || inv.getItem(MASK_SLOT).isSimilar(filler)) inv.setItem(MASK_SLOT, maskFiller);

        // Limpiar los slots interactivos del filler
        for (int i = 0; i < ACCESSORY_SLOTS.length; i++) {
            int slot = ACCESSORY_SLOTS[i];
            ItemStack item = inv.getItem(slot);
            if (item != null) {
                if (item.isSimilar(filler)) {
                    inv.setItem(slot, null);
                } else if (i <= 2 || slot == MASK_SLOT) {
                    // Si es uno de los primeros 3 slots o la máscara y tiene su placeholder, no lo quitamos
                }
            }
        }
        if (inv.getItem(HELMET_SLOT) != null && inv.getItem(HELMET_SLOT).isSimilar(filler)) inv.setItem(HELMET_SLOT, null);
        if (inv.getItem(CHEST_SLOT) != null && inv.getItem(CHEST_SLOT).isSimilar(filler)) inv.setItem(CHEST_SLOT, null);
        if (inv.getItem(LEGS_SLOT) != null && inv.getItem(LEGS_SLOT).isSimilar(filler)) inv.setItem(LEGS_SLOT, null);
        if (inv.getItem(BOOTS_SLOT) != null && inv.getItem(BOOTS_SLOT).isSimilar(filler)) inv.setItem(BOOTS_SLOT, null);

        player.openInventory(inv);
    }

    public static void save(Player player, Inventory inventory) {
                    // Guardar solo accesorios (10 slots)
                    ItemStack[] accessoryItems = new ItemStack[ACCESSORY_SLOTS.length];
                    for (int i = 0; i < ACCESSORY_SLOTS.length; i++) {
                        ItemStack item = inventory.getItem(ACCESSORY_SLOTS[i]);
                        if (item != null && item.getType().name().contains("STAINED_GLASS_PANE") && item.hasItemMeta()) {
                            String name = item.getItemMeta().getDisplayName();
                            if (name.contains("Slot de Orbe") || name.contains("Slot de Medalla") || name.contains("Slot de Reliquia") || name.contains("Slot de Máscara")) {
                                item = null;
                            }
                        }
                        accessoryItems[i] = item;
                    }
                    saveFromItems(player, accessoryItems);
                // Sincronizar armadura de vuelta al jugador
        player.getInventory().setHelmet(inventory.getItem(HELMET_SLOT));
        player.getInventory().setChestplate(inventory.getItem(CHEST_SLOT));
        player.getInventory().setLeggings(inventory.getItem(LEGS_SLOT));
        player.getInventory().setBoots(inventory.getItem(BOOTS_SLOT));
    }

    public static void saveFromItems(Player player, ItemStack[] items) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeInt(items.length);
            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }
            dataOutput.close();
            
            String encoded = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            player.getPersistentDataContainer().set(new NamespacedKey(Bukkit.getPluginManager().getPlugin("Permadeath"), KEY), PersistentDataType.STRING, encoded);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ItemStack[] load(Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(Bukkit.getPluginManager().getPlugin("Permadeath"), KEY);
        if (!pdc.has(key, PersistentDataType.STRING)) return null;
        try {
            String encoded = pdc.get(key, PersistentDataType.STRING);
            byte[] rawData = Base64.getDecoder().decode(encoded);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(rawData);
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            int size = dataInput.readInt();
            ItemStack[] items = new ItemStack[size];
            for (int i = 0; i < size; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }
            dataInput.close();
            return items;
        } catch (Exception e) {
            return null;
        }
    }
}



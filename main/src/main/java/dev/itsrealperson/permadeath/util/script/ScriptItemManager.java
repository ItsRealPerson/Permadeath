package dev.itsrealperson.permadeath.util.script;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.api.script.ScriptItem;
import dev.itsrealperson.permadeath.api.script.ScriptItemManagerAPI;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementación central del gestor de ítems por script.
 */
public class ScriptItemManager implements ScriptItemManagerAPI {

    private final Main plugin;
    private final Map<String, ScriptItem> registeredItems = new ConcurrentHashMap<>();
    private final NamespacedKey itemKey;

    public ScriptItemManager(Main plugin) {
        this.plugin = plugin;
        this.itemKey = new NamespacedKey(plugin, "pdc_custom_item");
        loadItems();
    }

    @Override
    public void registerItem(ScriptItem item) {
        registeredItems.put(item.id().toUpperCase(), item);
        plugin.getLogger().info("[Items] Ítem registrado: " + item.id());
    }

    @Override
    public ItemStack getItem(String id) {
        ScriptItem scriptItem = registeredItems.get(id.toUpperCase());
        if (scriptItem == null) return null;

        ItemStack item = new ItemStack(scriptItem.material());
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        // Nombre y Lore
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', scriptItem.displayName()));
        List<String> coloredLore = new ArrayList<>();
        for (String s : scriptItem.lore()) {
            coloredLore.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        meta.setLore(coloredLore);

        // CustomModelData
        if (scriptItem.customModelData() > 0) {
            meta.setCustomModelData(scriptItem.customModelData());
        }

        // Metadatos Persistentes (NBT)
        meta.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING, scriptItem.id().toUpperCase());
        
        // Tags adicionales
        for (Map.Entry<String, String> entry : scriptItem.nbtTags().entrySet()) {
            meta.getPersistentDataContainer().set(new NamespacedKey(plugin, entry.getKey()), PersistentDataType.STRING, entry.getValue());
        }

        item.setItemMeta(meta);
        return item;
    }

    @Override
    public boolean isCustomItem(ItemStack item, String id) {
        if (item == null || !item.hasItemMeta()) return false;
        String itemId = item.getItemMeta().getPersistentDataContainer().get(itemKey, PersistentDataType.STRING);
        return id.toUpperCase().equalsIgnoreCase(itemId);
    }

    @Override
    public Collection<String> getRegisteredItemIds() {
        return Collections.unmodifiableCollection(registeredItems.keySet());
    }

    public void loadItems() {
        File itemsFolder = new File(plugin.getDataFolder(), "scripts/items");
        if (!itemsFolder.exists()) itemsFolder.mkdirs();

        File[] files = itemsFolder.listFiles();
        if (files == null) return;

        for (File f : files) {
            if (f.getName().endsWith(".yml")) {
                loadYamlFile(f);
            }
        }
    }

    private void loadYamlFile(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<Map<?, ?>> items = (List<Map<?, ?>>) config.get("Items");
        if (items == null) return;

        for (Map<?, ?> map : items) {
            try {
                String id = (String) map.get("Id");
                org.bukkit.Material mat = org.bukkit.Material.valueOf(((String) map.get("Material")).toUpperCase());
                String name = (String) map.get("Name");
                List<String> lore = (List<String>) map.get("Lore");
                int cmd = (int) map.getOrDefault("CustomModelData", 0);
                
                Map<String, String> tags = new HashMap<>();
                if (map.containsKey("Metadata")) {
                    Map<String, String> metaMap = (Map<String, String>) map.get("Metadata");
                    tags.putAll(metaMap);
                }

                registerItem(new ScriptItem(id, mat, name, lore != null ? lore : new ArrayList<>(), cmd, tags));
            } catch (Exception e) {
                plugin.getLogger().severe("[Items] Error cargando ítem en " + file.getName() + ": " + e.getMessage());
            }
        }
    }
}

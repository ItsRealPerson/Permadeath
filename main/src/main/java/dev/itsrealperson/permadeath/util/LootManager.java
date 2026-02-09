package dev.itsrealperson.permadeath.util;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.api.LootManagerAPI;
import dev.itsrealperson.permadeath.util.item.NetheriteArmor;
import dev.itsrealperson.permadeath.util.item.PermadeathItems;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class LootManager implements LootManagerAPI {
    private final Main plugin;
    private File file;
    private FileConfiguration config;
    private final List<DynamicLootEntry> dynamicLoot = new ArrayList<>();

    public LootManager(Main plugin) {
        this.plugin = plugin;
        setup();
    }

    @Override
    public void addAbyssLoot(ItemStack item, int chance) {
        dynamicLoot.add(new DynamicLootEntry(item, chance));
    }

    @Override
    public List<ItemStack> getDynamicAbyssLoot() {
        List<ItemStack> items = new ArrayList<>();
        for (DynamicLootEntry entry : dynamicLoot) {
            items.add(entry.item().clone());
        }
        return Collections.unmodifiableList(items);
    }

    private static record DynamicLootEntry(ItemStack item, int chance) {}

    private void setup() {
        file = new File(plugin.getDataFolder(), "loot.yml");
        if (!file.exists()) {
            try {
                plugin.saveResource("loot.yml", false);
            } catch (IllegalArgumentException e) {
                try { file.createNewFile(); } catch (IOException ex) { ex.printStackTrace(); }
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        
        if (!config.contains("AbyssCapsule")) {
            List<String> defaultLoot = new ArrayList<>();
            defaultLoot.add("NETHERITE_UPGRADE_SMITHING_TEMPLATE;1;50");
            defaultLoot.add("ECHO_SHARD;3;100");
            defaultLoot.add("ENCHANTED_GOLDEN_APPLE;1;10");
            defaultLoot.add("DIAMOND_BLOCK;2;30");
            config.set("AbyssCapsule", defaultLoot);
            save();
        }
    }

    @Override
    public List<ItemStack> generateAbyssLoot() {
        List<ItemStack> items = new ArrayList<>();
        Random random = new Random();
        
        items.add(NetheriteArmor.craftAncestralFragment());
        if (random.nextInt(100) < 40) items.add(PermadeathItems.createVoidShard());

        if (random.nextInt(100) < 5) {
            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
            org.bukkit.inventory.meta.EnchantmentStorageMeta meta = (org.bukkit.inventory.meta.EnchantmentStorageMeta) book.getItemMeta();
            org.bukkit.enchantments.Enchantment ench = org.bukkit.Registry.ENCHANTMENT.get(new org.bukkit.NamespacedKey("permadeath", "abyssal_breathing"));
            if (ench != null) {
                meta.addStoredEnchant(ench, random.nextInt(3) + 1, true);
                book.setItemMeta(meta);
                items.add(book);
            }
        }
        
        List<String> lootList = config.getStringList("AbyssCapsule");
        for (String entry : lootList) {
            try {
                String[] split = entry.split(";");
                Material mat = Material.valueOf(split[0]);
                int maxAmt = Integer.parseInt(split[1]);
                int chance = Integer.parseInt(split[2]);
                
                if (random.nextInt(100) < chance) {
                    items.add(new ItemStack(mat, random.nextInt(maxAmt) + 1));
                }
            } catch (Exception ignored) {}
        }

        for (DynamicLootEntry entry : dynamicLoot) {
            if (random.nextInt(100) < entry.chance()) {
                items.add(entry.item().clone());
            }
        }

        return items;
    }

    public List<String> getAbyssLootEntries() {
        return config.getStringList("AbyssCapsule");
    }

    public void removeAbyssLoot(int index) {
        List<String> list = getAbyssLootEntries();
        if (index >= 0 && index < list.size()) {
            list.remove(index);
            config.set("AbyssCapsule", list);
            save();
        }
    }

    public void addAbyssLootConfig(Material material, int maxAmount, int chance) {
        List<String> list = getAbyssLootEntries();
        list.add(material.name() + ";" + maxAmount + ";" + chance);
        config.set("AbyssCapsule", list);
        save();
    }

    public void updateAbyssLoot(int index, int maxAmount, int chance) {
        List<String> list = getAbyssLootEntries();
        if (index >= 0 && index < list.size()) {
            String[] split = list.get(index).split(";");
            list.set(index, split[0] + ";" + maxAmount + ";" + chance);
            config.set("AbyssCapsule", list);
            save();
        }
    }

    public void save() {
        try { config.save(file); } catch (IOException e) { e.printStackTrace(); }
    }
}
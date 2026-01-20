package tech.sebazcrc.permadeath.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import tech.sebazcrc.permadeath.Main;
import tech.sebazcrc.permadeath.util.item.NetheriteArmor;
import tech.sebazcrc.permadeath.util.item.PermadeathItems;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LootManager {
    private final Main plugin;
    private File file;
    private FileConfiguration config;

    public LootManager(Main plugin) {
        this.plugin = plugin;
        setup();
    }

    private void setup() {
        file = new File(plugin.getDataFolder(), "loot.yml");
        if (!file.exists()) {
            try {
                plugin.saveResource("loot.yml", false);
            } catch (IllegalArgumentException e) {
                // Si el recurso no existe en el JAR, creamos un archivo vacío o con valores base
                try { file.createNewFile(); } catch (IOException ex) { ex.printStackTrace(); }
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        
        // Loot por defecto si el archivo es nuevo o no tiene la sección
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

    public List<ItemStack> generateAbyssLoot() {
        List<ItemStack> items = new ArrayList<>();
        Random random = new Random();
        
        // Ítems fijos de Permadeath
        items.add(NetheriteArmor.craftAncestralFragment());
        if (random.nextInt(100) < 40) items.add(PermadeathItems.createVoidShard());
        
        // Ítems configurables
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
        return items;
    }

    public void save() {
        try { config.save(file); } catch (IOException e) { e.printStackTrace(); }
    }
}

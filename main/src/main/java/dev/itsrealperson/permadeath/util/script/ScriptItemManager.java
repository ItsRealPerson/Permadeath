package dev.itsrealperson.permadeath.util.script;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.api.script.ScriptItem;
import dev.itsrealperson.permadeath.api.script.ScriptItemManagerAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementación avanzada del gestor de ítems por script.
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
        
        // Registrar recetas en Bukkit
        for (int i = 0; i < item.recipes().size(); i++) {
            ScriptItem.ScriptRecipe scriptRecipe = item.recipes().get(i);
            NamespacedKey key = new NamespacedKey(plugin, "pdc_recipe_" + item.id().toLowerCase() + "_" + i);
            Bukkit.removeRecipe(key);

            if (scriptRecipe.type() == ScriptItem.RecipeType.SHAPED) {
                org.bukkit.inventory.ShapedRecipe shaped = new org.bukkit.inventory.ShapedRecipe(key, getItem(item.id()));
                shaped.shape(scriptRecipe.shape().toArray(new String[0]));
                
                for (Map.Entry<Character, String> entry : scriptRecipe.ingredients().entrySet()) {
                    setRecipeIngredient(shaped, entry.getKey(), entry.getValue());
                }
                Bukkit.addRecipe(shaped);
            } else if (scriptRecipe.type() == ScriptItem.RecipeType.SMITHING) {
                // Registro de Smithing Table (1.20+)
                org.bukkit.inventory.RecipeChoice tChoice = getChoice(scriptRecipe.template());
                org.bukkit.inventory.RecipeChoice bChoice = getChoice(scriptRecipe.base());
                org.bukkit.inventory.RecipeChoice aChoice = getChoice(scriptRecipe.addition());

                if (tChoice != null && bChoice != null && aChoice != null) {
                    org.bukkit.inventory.SmithingTransformRecipe smithing = new org.bukkit.inventory.SmithingTransformRecipe(
                        key, getItem(item.id()), tChoice, bChoice, aChoice
                    );
                    Bukkit.addRecipe(smithing);
                }
            }
        }
    }

    private org.bukkit.inventory.RecipeChoice getChoice(String val) {
        if (val == null) return null;
        if (val.startsWith("custom:")) {
            ItemStack custom = getItem(val.substring(7));
            return custom != null ? new org.bukkit.inventory.RecipeChoice.ExactChoice(custom) : null;
        } else {
            try {
                return new org.bukkit.inventory.RecipeChoice.MaterialChoice(org.bukkit.Material.valueOf(val.toUpperCase()));
            } catch (Exception e) { return null; }
        }
    }

    private void setRecipeIngredient(org.bukkit.inventory.ShapedRecipe recipe, char key, String val) {
        if (val.startsWith("custom:")) {
            ItemStack custom = getItem(val.substring(7));
            if (custom != null) recipe.setIngredient(key, new org.bukkit.inventory.RecipeChoice.ExactChoice(custom));
        } else {
            try {
                recipe.setIngredient(key, org.bukkit.Material.valueOf(val.toUpperCase()));
            } catch (Exception ignored) {}
        }
    }

    @Override
    public ItemStack getItem(String id) {
        ScriptItem scriptItem = registeredItems.get(id.toUpperCase());
        if (scriptItem == null) return null;

        ItemStack item = new ItemStack(scriptItem.material());
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        // Visuales básicos
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', scriptItem.displayName()));
        if (!scriptItem.lore().isEmpty()) {
            List<String> coloredLore = new ArrayList<>();
            for (String s : scriptItem.lore()) {
                coloredLore.add(ChatColor.translateAlternateColorCodes('&', s));
            }
            meta.setLore(coloredLore);
        }

        meta.setUnbreakable(scriptItem.unbreakable());
        if (scriptItem.customModelData() > 0) meta.setCustomModelData(scriptItem.customModelData());

        // Colores (Cuero o Pociones)
        if (scriptItem.color() != null && !scriptItem.color().isEmpty()) {
            try {
                int hex = Integer.parseInt(scriptItem.color().replace("#", ""), 16);
                Color c = Color.fromRGB(hex);
                if (meta instanceof LeatherArmorMeta lam) lam.setColor(c);
                else if (meta instanceof PotionMeta pm) pm.setColor(c);
            } catch (Exception ignored) {}
        }

        // Encantamientos
        for (Map.Entry<String, Integer> entry : scriptItem.enchantments().entrySet()) {
            try {
                Enchantment ench = Enchantment.getByKey(NamespacedKey.minecraft(entry.getKey().toLowerCase()));
                if (ench != null) meta.addEnchant(ench, entry.getValue(), true);
            } catch (Exception ignored) {}
        }

        // Atributos (Ej: generic.attack_damage)
        for (Map.Entry<String, Double> entry : scriptItem.attributes().entrySet()) {
            try {
                String attrName = entry.getKey().toUpperCase().replace(".", "_");
                Attribute attr = Attribute.valueOf(attrName);
                meta.addAttributeModifier(attr, new AttributeModifier(UUID.randomUUID(), "pdc_modifier", entry.getValue(), AttributeModifier.Operation.ADD_NUMBER));
            } catch (Exception ignored) {}
        }

        // Metadatos Persistentes
        meta.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING, scriptItem.id().toUpperCase());
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
            if (f.getName().endsWith(".yml")) loadYamlFile(f);
        }
    }

    private void loadYamlFile(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<?> items = config.getList("Items");
        if (items == null) return;

        for (Object obj : items) {
            if (!(obj instanceof Map<?, ?> map)) continue;
            try {
                String id = (String) map.get("Id");
                org.bukkit.Material mat = org.bukkit.Material.valueOf(((String) map.get("Material")).toUpperCase());
                String name = (String) map.get("Name");
                List<String> lore = (List<String>) map.get("Lore");
                int cmd = map.containsKey("CustomModelData") ? (int) map.get("CustomModelData") : 0;
                boolean unb = map.containsKey("Unbreakable") && (boolean) map.get("Unbreakable");
                String color = (String) map.get("Color");

                Map<String, Integer> enchants = new HashMap<>();
                if (map.containsKey("Enchantments")) {
                    Map<?, ?> eMap = (Map<?, ?>) map.get("Enchantments");
                    for (Map.Entry<?, ?> entry : eMap.entrySet()) {
                        enchants.put(entry.getKey().toString(), (Integer) entry.getValue());
                    }
                }

                Map<String, Double> attrs = new HashMap<>();
                if (map.containsKey("Attributes")) {
                    Map<?, ?> aMap = (Map<?, ?>) map.get("Attributes");
                    for (Map.Entry<?, ?> entry : aMap.entrySet()) {
                        attrs.put(entry.getKey().toString(), Double.valueOf(entry.getValue().toString()));
                    }
                }

                Map<String, String> tags = new HashMap<>();
                if (map.containsKey("Metadata")) {
                    Map<?, ?> metaMap = (Map<?, ?>) map.get("Metadata");
                    for (Map.Entry<?, ?> entry : metaMap.entrySet()) {
                        tags.put(entry.getKey().toString(), entry.getValue().toString());
                    }
                }

                // Cargar Recetas
                List<ScriptItem.ScriptRecipe> recipes = new ArrayList<>();
                if (map.containsKey("Recipes")) {
                    List<?> recList = (List<?>) map.get("Recipes");
                    for (Object recObj : recList) {
                        if (!(recObj instanceof Map<?, ?> recMap)) continue;
                        
                        List<String> shape = (List<String>) recMap.get("Shape");
                        String template = (String) recMap.get("Template");
                        
                        ScriptItem.RecipeType type = (shape != null) ? ScriptItem.RecipeType.SHAPED : ScriptItem.RecipeType.SMITHING;
                        
                        Map<Character, String> ingredients = new HashMap<>();
                        if (type == ScriptItem.RecipeType.SHAPED) {
                            String ingStr = (String) recMap.get("Ingredients");
                            if (ingStr != null) {
                                for (String part : ingStr.split(",")) {
                                    String[] kv = part.split(":");
                                    if (kv.length == 2) ingredients.put(kv[0].trim().charAt(0), kv[1].trim());
                                }
                            }
                        }
                        
                        recipes.add(new ScriptItem.ScriptRecipe(
                            type, 
                            shape, ingredients, 
                            template, (String) recMap.get("Base"), (String) recMap.get("Addition")
                        ));
                    }
                }

                registerItem(new ScriptItem(id, mat, name, lore != null ? lore : new ArrayList<>(), cmd, unb, color, enchants, attrs, tags, recipes));
            } catch (Exception e) {
                plugin.getLogger().severe("[Items] Error en " + file.getName() + ": " + e.getMessage());
            }
        }
    }
}

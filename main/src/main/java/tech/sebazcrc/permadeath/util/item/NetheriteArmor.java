package tech.sebazcrc.permadeath.util.item;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tech.sebazcrc.permadeath.Main;
import tech.sebazcrc.permadeath.util.lib.ItemBuilder;
import tech.sebazcrc.permadeath.util.lib.LeatherArmorBuilder;
import tech.sebazcrc.permadeath.util.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public final class NetheriteArmor implements Listener {
    private static Color color = Color.fromRGB(6116957);

    private static String helmetName = TextUtils.format("&5Netherite Helmet");
    private static String chestName = TextUtils.format("&5Netherite Chestplate");
    private static String legName = TextUtils.format("&5Netherite Leggings");
    private static String bootName = TextUtils.format("&5Netherite Boots");

    public static ItemStack craftAncestralFragment() {
        return new ItemBuilder(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
                .setDisplayName(TextUtils.format("&6&lFragmento de Herrería Ancestral"))
                .setLore(java.util.Arrays.asList(
                        TextUtils.format("&7Se utiliza en la mesa de herrería"),
                        TextUtils.format("&7para forjar armaduras especiales.")
                ))
                .setUnbrekeable(true)
                .build();
    }

    public static ItemStack craftTemplate(String type) {
        String name = "";
        Material mat = Material.PAPER;
        
        switch (type.toLowerCase()) {
            case "helmet" -> { name = "&dMolde de Casco Netherite"; mat = Material.COAST_ARMOR_TRIM_SMITHING_TEMPLATE; }
            case "chestplate" -> { name = "&dMolde de Pechera Netherite"; mat = Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE; }
            case "leggings" -> { name = "&dMolde de Grebas Netherite"; mat = Material.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE; }
            case "boots" -> { name = "&dMolde de Botas Netherite"; mat = Material.WILD_ARMOR_TRIM_SMITHING_TEMPLATE; }
        }

        return new ItemBuilder(mat)
                .setDisplayName(TextUtils.format(name))
                .setLore(java.util.Arrays.asList(
                        TextUtils.format("&7Combina este molde con una pieza"),
                        TextUtils.format("&7de netherite y un fragmento ancestral.")
                ))
                .setUnbrekeable(true)
                .build();
    }

    public static ItemStack craftNetheriteHelmet() {

        ItemStack item = new LeatherArmorBuilder(Material.LEATHER_HELMET, 1)
                .setColor(color)
                .setDisplayName(helmetName)
                .build();

        ItemMeta meta = item.getItemMeta();

        // CASCO 3, PECHERA 8, PANTALONES 6, BOTAS 3

        AttributeModifier modifier = new AttributeModifier(new NamespacedKey("permadeath", "armor"), 4, AttributeModifier.Operation.ADD_NUMBER, org.bukkit.inventory.EquipmentSlotGroup.HEAD);
        meta.addAttributeModifier(Attribute.ARMOR, modifier);

        AttributeModifier modifier2 = new AttributeModifier(new NamespacedKey("permadeath", "armor_toughness"), 4, AttributeModifier.Operation.ADD_NUMBER, org.bukkit.inventory.EquipmentSlotGroup.HEAD);
        meta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS, modifier2);

        meta.setUnbreakable(true);

        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack craftNetheriteChest() {

        ItemStack item = new LeatherArmorBuilder(Material.LEATHER_CHESTPLATE, 1)
                .setColor(color)
                .setDisplayName(chestName)
                .build();

        ItemMeta meta = item.getItemMeta();

        // CASCO 3, PECHERA 8, PANTALONES 6, BOTAS 3

        AttributeModifier modifier = new AttributeModifier(new NamespacedKey("permadeath", "armor"), 9, AttributeModifier.Operation.ADD_NUMBER, org.bukkit.inventory.EquipmentSlotGroup.CHEST);
        meta.addAttributeModifier(Attribute.ARMOR, modifier);

        AttributeModifier modifier2 = new AttributeModifier(new NamespacedKey("permadeath", "armor_toughness"), 4, AttributeModifier.Operation.ADD_NUMBER, org.bukkit.inventory.EquipmentSlotGroup.CHEST);
        meta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS, modifier2);


        AttributeModifier modifier3 = new AttributeModifier(new NamespacedKey("permadeath", "max_health"), 2, AttributeModifier.Operation.ADD_NUMBER, org.bukkit.inventory.EquipmentSlotGroup.CHEST);
        meta.addAttributeModifier(Attribute.MAX_HEALTH, modifier3);

        meta.setUnbreakable(true);

        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack craftNetheriteLegs() {

        ItemStack item = new LeatherArmorBuilder(Material.LEATHER_LEGGINGS, 1)
                .setColor(color)
                .setDisplayName(legName)
                .build();

        ItemMeta meta = item.getItemMeta();

        // CASCO 3, PECHERA 8, PANTALONES 6, BOTAS 3

        AttributeModifier modifier = new AttributeModifier(new NamespacedKey("permadeath", "armor"), 7, AttributeModifier.Operation.ADD_NUMBER, org.bukkit.inventory.EquipmentSlotGroup.LEGS);
        meta.addAttributeModifier(Attribute.ARMOR, modifier);

        AttributeModifier modifier2 = new AttributeModifier(new NamespacedKey("permadeath", "armor_toughness"), 4, AttributeModifier.Operation.ADD_NUMBER, org.bukkit.inventory.EquipmentSlotGroup.LEGS);
        meta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS, modifier2);

        AttributeModifier modifier3 = new AttributeModifier(new NamespacedKey("permadeath", "max_health"), 2, AttributeModifier.Operation.ADD_NUMBER, org.bukkit.inventory.EquipmentSlotGroup.LEGS);
        meta.addAttributeModifier(Attribute.MAX_HEALTH, modifier3);

        meta.setUnbreakable(true);

        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack craftNetheriteBoots() {

        ItemStack item = new LeatherArmorBuilder(Material.LEATHER_BOOTS, 1)
                .setColor(color)
                .setDisplayName(bootName)
                .build();

        ItemMeta meta = item.getItemMeta();

        // CASCO 3, PECHERA 8, PANTALONES 6, BOTAS 3

        AttributeModifier modifier = new AttributeModifier(new NamespacedKey("permadeath", "armor"), 4, AttributeModifier.Operation.ADD_NUMBER, org.bukkit.inventory.EquipmentSlotGroup.FEET);
        meta.addAttributeModifier(Attribute.ARMOR, modifier);

        AttributeModifier modifier2 = new AttributeModifier(new NamespacedKey("permadeath", "armor_toughness"), 4, AttributeModifier.Operation.ADD_NUMBER, org.bukkit.inventory.EquipmentSlotGroup.FEET);
        meta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS, modifier2);

        meta.setUnbreakable(true);

        item.setItemMeta(meta);

        return item;
    }

    public static boolean isNetheritePiece(ItemStack s) {
        if (s == null) return false;

        if (s.hasItemMeta()) {

            if (s.getItemMeta().isUnbreakable() && ChatColor.stripColor(s.getItemMeta().getDisplayName()).startsWith("Netherite")) {

                return true;
            }
        }

        return false;
    }

    public static boolean isInfernalPiece(ItemStack s) {
        if (s == null) return false;

        if (s.hasItemMeta()) {
            ItemMeta m = s.getItemMeta();

            if (s.getType() == Material.ELYTRA && m.hasCustomModelData() && m.getCustomModelData() == 1) {
                return true;
            }

            if (s.getItemMeta().isUnbreakable() && ChatColor.stripColor(s.getItemMeta().getDisplayName()).startsWith("Infernal")) {
                return true;
            }
        }

        return false;
    }

    public static void setupHealth(Player p) {
        Double maxHealth = getAvailableMaxHealth(p);
        p.getAttribute(Attribute.MAX_HEALTH).setBaseValue(maxHealth);
    }

    public static Double getAvailableMaxHealth(Player p) {

        int currentNetheritePieces = 0;
        int currentInfernalPieces = 0;
        
        // CHECK ALL ITEMS (Main + Accessories)
        List<ItemStack> allItems = new ArrayList<>(Arrays.asList(p.getInventory().getContents()));
        ItemStack[] accessories = tech.sebazcrc.permadeath.util.inventory.AccessoryInventory.load(p);
        if (accessories != null) allItems.addAll(Arrays.asList(accessories));

        boolean hasOrb = Main.getInstance().getOrbEvent().isRunning();
        if (!hasOrb) {
            for (ItemStack s : allItems) {
                if (s != null && s.getType() == Material.BROWN_DYE && s.getItemMeta() != null && s.getItemMeta().isUnbreakable()) {
                    hasOrb = true;
                    break;
                }
            }
        }

        boolean doPlayerAteOne = p.getPersistentDataContainer().has(new NamespacedKey(Main.getInstance(), "hyper_one"), PersistentDataType.BYTE);
        boolean doPlayerAteTwo = p.getPersistentDataContainer().has(new NamespacedKey(Main.getInstance(), "hyper_two"), PersistentDataType.BYTE);

        for (ItemStack contents : p.getInventory().getArmorContents()) {
            if (isNetheritePiece(contents)) {
                currentNetheritePieces++;
            }
            if (isInfernalPiece(contents)) {
                currentInfernalPieces++;
            }
        }

        Double maxHealth = 20.0D;

        if (doPlayerAteOne) {
            maxHealth += 4.0;
        }
        if (doPlayerAteTwo) {
            maxHealth += 4.0;
        }

        if (currentNetheritePieces >= 4) {
            maxHealth += 8.0D;
        }

        if (currentInfernalPieces >= 4) {
            maxHealth += 10.0D;
            p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20 * 3, 0));
        }

        if (Main.getInstance().getDay() >= 40) {
            maxHealth -= 8.0D; // 12HP - 6 corazones día 40
            if (Main.getInstance().getDay() >= 60) {
                maxHealth -= 8.0D; // 4HP - 2 corazones Día 60

                if (!hasOrb) {
                    maxHealth -= 16.0D;
                }
            }
        }

        return Math.max(maxHealth, 0.000001D);
    }

    public static boolean checkForOrb(Player p) {
        if (Main.getInstance().getOrbEvent().isRunning()) {
            return true;
        } else {
            for (ItemStack stack : p.getInventory().getContents()) {
                if (stack != null) {
                    if (stack.getItemMeta() != null && stack.getType() == Material.BROWN_DYE && stack.getItemMeta().isUnbreakable()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}










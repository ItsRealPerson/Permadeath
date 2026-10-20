package tech.sebazcrc.permadeath.util.item;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tech.sebazcrc.permadeath.util.lib.LeatherArmorBuilder;
import tech.sebazcrc.permadeath.util.TextUtils;

public final class InfernalNetherite implements Listener {
    private static Color color = Color.fromRGB(16711680);

    private static String helmetName = TextUtils.format("&5Infernal Netherite Helmet");
    private static String chestName = TextUtils.format("&5Infernal Netherite Chestplate");
    private static String legName = TextUtils.format("&5Infernal Netherite Leggings");
    private static String bootName = TextUtils.format("&5Infernal Netherite Boots");

    public static ItemStack craftNetheriteHelmet() {

        ItemStack item = new LeatherArmorBuilder(Material.LEATHER_HELMET, 1)
                .setColor(color)
                .setDisplayName(helmetName)
                .build();

        ItemMeta meta = item.getItemMeta();

        // CASCO 3, PECHERA 8, PANTALONES 6, BOTAS 3

        AttributeModifier modifier = new AttributeModifier(new NamespacedKey("permadeath", "infernal_helmet_armor"), 5, AttributeModifier.Operation.ADD_NUMBER, org.bukkit.inventory.EquipmentSlotGroup.HEAD);
        meta.addAttributeModifier(Attribute.ARMOR, modifier);

        AttributeModifier modifier2 = new AttributeModifier(new NamespacedKey("permadeath", "infernal_helmet_armor_toughness"), 5, AttributeModifier.Operation.ADD_NUMBER, org.bukkit.inventory.EquipmentSlotGroup.HEAD);
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

        AttributeModifier modifier = new AttributeModifier(new NamespacedKey("permadeath", "infernal_chest_armor"), 10, AttributeModifier.Operation.ADD_NUMBER, org.bukkit.inventory.EquipmentSlotGroup.CHEST);
        meta.addAttributeModifier(Attribute.ARMOR, modifier);

        AttributeModifier modifier2 = new AttributeModifier(new NamespacedKey("permadeath", "infernal_chest_armor_toughness"), 5, AttributeModifier.Operation.ADD_NUMBER, org.bukkit.inventory.EquipmentSlotGroup.CHEST);
        meta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS, modifier2);


        AttributeModifier modifier3 = new AttributeModifier(new NamespacedKey("permadeath", "infernal_chest_max_health"), 2, AttributeModifier.Operation.ADD_NUMBER, org.bukkit.inventory.EquipmentSlotGroup.CHEST);
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

        AttributeModifier modifier = new AttributeModifier(new NamespacedKey("permadeath", "infernal_legs_armor"), 8, AttributeModifier.Operation.ADD_NUMBER, org.bukkit.inventory.EquipmentSlotGroup.LEGS);
        meta.addAttributeModifier(Attribute.ARMOR, modifier);

        AttributeModifier modifier2 = new AttributeModifier(new NamespacedKey("permadeath", "infernal_legs_armor_toughness"), 5, AttributeModifier.Operation.ADD_NUMBER, org.bukkit.inventory.EquipmentSlotGroup.LEGS);
        meta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS, modifier2);

        AttributeModifier modifier3 = new AttributeModifier(new NamespacedKey("permadeath", "infernal_legs_max_health"), 2, AttributeModifier.Operation.ADD_NUMBER, org.bukkit.inventory.EquipmentSlotGroup.LEGS);
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

        AttributeModifier modifier = new AttributeModifier(new NamespacedKey("permadeath", "infernal_boots_armor"), 5, AttributeModifier.Operation.ADD_NUMBER, org.bukkit.inventory.EquipmentSlotGroup.FEET);
        meta.addAttributeModifier(Attribute.ARMOR, modifier);

        AttributeModifier modifier2 = new AttributeModifier(new NamespacedKey("permadeath", "infernal_boots_armor_toughness"), 5, AttributeModifier.Operation.ADD_NUMBER, org.bukkit.inventory.EquipmentSlotGroup.FEET);
        meta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS, modifier2);

        meta.setUnbreakable(true);

        item.setItemMeta(meta);

        return item;
    }
}


















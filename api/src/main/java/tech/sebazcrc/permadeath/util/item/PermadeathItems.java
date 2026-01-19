package tech.sebazcrc.permadeath.util.item;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import tech.sebazcrc.permadeath.api.PermadeathAPI;
import tech.sebazcrc.permadeath.util.TextUtils;
import tech.sebazcrc.permadeath.util.inventory.AccessoryInventory;
import tech.sebazcrc.permadeath.util.lib.HiddenStringUtils;
import tech.sebazcrc.permadeath.util.lib.ItemBuilder;

import java.util.Arrays;
import java.util.UUID;

public class PermadeathItems {
    private static final int[] beginningRelicLockedSlots = {40, 34, 33, 32, 30, 29, 28, 27, 26, 25, 24, 23, 21, 20, 19, 18, 17, 16, 15, 14, 12, 11, 10, 9, 8, 7};

    public static ItemStack crearReliquia() {
        ItemStack s = new ItemBuilder(Material.LIGHT_BLUE_DYE).setCustomModelData(1, !PermadeathAPI.optifineItemsEnabled()).setDisplayName(TextUtils.format("&6Reliquia Del Fin")).build();
        ItemMeta meta = s.getItemMeta();
        meta.setUnbreakable(true);
        meta.setLore(Arrays.asList(HiddenStringUtils.encodeString("{" + UUID.randomUUID().toString() + ": 0}")));
        s.setItemMeta(meta);
        return s;
    }

    public static ItemStack createLifeOrb() {
        return new ItemBuilder(Material.BROWN_DYE).setCustomModelData(1, !PermadeathAPI.optifineItemsEnabled()).setUnbrekeable(true).setDisplayName(TextUtils.format("&6Orbe de Vida")).build();
    }

    public static ItemStack createBeginningRelic() {
        return new ItemBuilder(Material.CYAN_DYE).setCustomModelData(1, !PermadeathAPI.optifineItemsEnabled()).setUnbrekeable(true).setDisplayName(TextUtils.format("&6Reliquia del Comienzo")).build();
    }

    public static ItemStack createWaterMedal() {
        ItemStack s = new ItemBuilder(Material.HEART_OF_THE_SEA).setDisplayName(TextUtils.format("&b&lMedalla de Protección Acuática")).setUnbrekeable(true).build();
        ItemMeta meta = s.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(Bukkit.getPluginManager().getPlugin("Permadeath"), "water_medal"), PersistentDataType.BYTE, (byte) 1);
        meta.setLore(Arrays.asList(TextUtils.format("&7Inmunidad al ahogamiento."), TextUtils.format("&eDisponible desde el día 30.")));
        s.setItemMeta(meta);
        return s;
    }

    public static ItemStack craftInfernalElytra() {
        ItemStack s = new ItemBuilder(Material.ELYTRA).setCustomModelData(1).setDisplayName(TextUtils.format("&6Elytras de Netherite Infernal")).build();
        ItemMeta meta = s.getItemMeta();
        AttributeModifier m = new AttributeModifier(UUID.randomUUID(), "generic.armor", 8, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST);
        AttributeModifier m2 = new AttributeModifier(UUID.randomUUID(), "generic.armorToughness", 3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST);
        assert meta != null;
        meta.addAttributeModifier(Attribute.ARMOR, m);
        meta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS, m2);
        s.setItemMeta(meta);
        return s;
    }

    public static ItemStack craftNetheriteSword() {
        ItemStack s = new ItemBuilder(Material.DIAMOND_SWORD).setCustomModelData(1, !PermadeathAPI.optifineItemsEnabled()).setDisplayName(TextUtils.format("&6Espada de Netherite")).build();
        ItemMeta meta = s.getItemMeta();
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 8.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", -2.4D, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        assert meta != null;
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, modifier);
        meta.addAttributeModifier(Attribute.ATTACK_SPEED, modifier2);
        meta.setUnbreakable(true);
        s.setItemMeta(meta);
        return s;
    }

    public static ItemStack craftNetheritePickaxe() {
        ItemStack s = new ItemBuilder(Material.DIAMOND_PICKAXE).setCustomModelData(1, !PermadeathAPI.optifineItemsEnabled()).setDisplayName(TextUtils.format("&6Pico de Netherite")).build();
        ItemMeta meta = s.getItemMeta();
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 6.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", 1.2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, modifier);
        meta.addAttributeModifier(Attribute.ATTACK_SPEED, modifier2);
        meta.setUnbreakable(true);
        s.setItemMeta(meta);
        return s;
    }

    public static ItemStack craftNetheriteHoe() {
        ItemStack s = new ItemBuilder(Material.DIAMOND_HOE).setCustomModelData(1, !PermadeathAPI.optifineItemsEnabled()).setDisplayName(TextUtils.format("&6Azada de Netherite")).build();
        ItemMeta meta = s.getItemMeta();
        meta.setUnbreakable(true);
        s.setItemMeta(meta);
        return s;
    }

    public static ItemStack craftNetheriteAxe() {
        ItemStack s = new ItemBuilder(Material.DIAMOND_AXE).setCustomModelData(1, !PermadeathAPI.optifineItemsEnabled()).setDisplayName(TextUtils.format("&6Hacha de Netherite")).build();
        ItemMeta meta = s.getItemMeta();
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 10.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", 1.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, modifier);
        meta.addAttributeModifier(Attribute.ATTACK_SPEED, modifier2);
        meta.setUnbreakable(true);
        s.setItemMeta(meta);
        return s;
    }

    public static ItemStack craftNetheriteShovel() {
        ItemStack s = new ItemBuilder(Material.DIAMOND_SHOVEL).setCustomModelData(1, !PermadeathAPI.optifineItemsEnabled()).setDisplayName(TextUtils.format("&6Pala de Netherite")).build();
        ItemMeta meta = s.getItemMeta();
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 6.5, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", 1.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, modifier);
        meta.addAttributeModifier(Attribute.ATTACK_SPEED, modifier2);
        meta.setUnbreakable(true);
        s.setItemMeta(meta);
        return s;
    }

    public static ItemStack craftInfernalNetheriteIngot() {
        ItemStack s = new ItemBuilder(Material.DIAMOND).setCustomModelData(1, !PermadeathAPI.optifineItemsEnabled()).setDisplayName(TextUtils.format("&6Infernal Netherite Block")).build();
        ItemMeta meta = s.getItemMeta();
        meta.setUnbreakable(true);
        meta.setLore(Arrays.asList(HiddenStringUtils.encodeString("{" + UUID.randomUUID() + ": 0}")));
        s.setItemMeta(meta);
        return s;
    }

    public static ItemStack createAccessoryTrigger() {
        return new ItemBuilder(Material.NETHER_STAR)
                .setDisplayName(TextUtils.format("&6&lMenú de Accesorios &7(Click Derecho)"))
                .setLore(java.util.Arrays.asList(
                        TextUtils.format("&7Usa este ítem para gestionar"),
                        TextUtils.format("&7tus accesorios especiales."),
                        " ",
                        TextUtils.format("&cNo se puede tirar ni perder.")
                ))
                .setUnbrekeable(true)
                .build();
    }

    public static void slotBlock(Player p) {
        if (PermadeathAPI.getDay() < 40) return;
        if (p.getGameMode() == GameMode.SPECTATOR || p.isDead() || !p.isOnline()) return;

        boolean hasEndRelic = false;
        boolean hasBeginningRelic = false;

        int[] endRelicLockedSlots;
        if (PermadeathAPI.getDay() < 60) {
            endRelicLockedSlots = new int[]{40, 13, 22, 31, 4};
        } else {
            endRelicLockedSlots = new int[]{13, 22, 31, 4};
        }

        for (ItemStack contents : p.getInventory().getContents()) {
            if (contents == null) continue;
            
            if (!hasBeginningRelic && isBeginningRelic(contents)) {
                hasBeginningRelic = true;
                hasEndRelic = true;
            } else if (!hasEndRelic && isEndRelic(contents)) {
                hasEndRelic = true;
            }
        }
        
        // Check accessories too
        if (!hasEndRelic || !hasBeginningRelic) {
            ItemStack[] acc = AccessoryInventory.load(p);
            if (acc != null) {
                for (ItemStack contents : acc) {
                    if (contents == null) continue;
                    if (!hasBeginningRelic && isBeginningRelic(contents)) {
                        hasBeginningRelic = true;
                        hasEndRelic = true;
                    } else if (!hasEndRelic && isEndRelic(contents)) {
                        hasEndRelic = true;
                    }
                }
            }
        }

        int slot;
        if (PermadeathAPI.getDay() >= 40) {
            for (int i = 0; i < endRelicLockedSlots.length; i++) {
                slot = endRelicLockedSlots[i];
                if (hasEndRelic) {
                    unlockSlot(p, slot);
                } else {
                    lockSlot(p, slot);
                }
            }
        }

        if (PermadeathAPI.getDay() >= 60) {
            for (int i = 0; i < beginningRelicLockedSlots.length; i++) {
                slot = beginningRelicLockedSlots[i];
                // SKIP SLOT 8 (Accessory Menu)
                if (slot == 8) continue; 
                
                if (hasBeginningRelic) {
                    unlockSlot(p, slot);
                } else {
                    lockSlot(p, slot);
                }
            }
        }
    }

    private static void lockSlot(Player p, int slot) {
        ItemStack item = p.getInventory().getItem(slot);

        if (item != null && item.getType() != Material.AIR) {
            if (item.getType() == Material.STRUCTURE_VOID) return;
            
            p.getWorld().dropItem(p.getLocation(), item.clone());
        }
        
        ItemStack lock = new ItemBuilder(Material.STRUCTURE_VOID)
                .setDisplayName(TextUtils.format("&c&lSLOT BLOQUEADO"))
                .setLore(Arrays.asList(TextUtils.format("&7Necesitas una reliquia"), TextUtils.format("&7para usar este espacio.")))
                .setCustomModelData(666)
                .build();
        
        p.getInventory().setItem(slot, lock);
    }

    private static void unlockSlot(Player p, int slot) {
        ItemStack item = p.getInventory().getItem(slot);
        if (item != null && item.getType() == Material.STRUCTURE_VOID) {
            p.getInventory().setItem(slot, null);
        }
    }

    public static boolean isEndRelic(ItemStack stack) {
        if (stack == null) return false;
        if (!stack.hasItemMeta()) return false;
        if (stack.getType() == Material.LIGHT_BLUE_DYE && stack.getItemMeta().getDisplayName().endsWith(TextUtils.format("&6Reliquia Del Fin"))) {
            return true;
        }
        return false;
    }

    public static boolean isBeginningRelic(ItemStack stack) {
        if (stack == null) return false;
        if (!stack.hasItemMeta()) return false;
        if (stack.getType() == Material.CYAN_DYE && stack.getItemMeta().getDisplayName().endsWith(TextUtils.format("&6Reliquia del Comienzo"))) {
            return true;
        }
        return false;
    }
    
    // Métodos auxiliares para no romper otras clases que los usan, pero slotBlock ya no los usa
    public static boolean isLifeOrb(ItemStack stack) {
        if (stack == null) return false;
        if (!stack.hasItemMeta()) return false;
        return stack.getType() == Material.BROWN_DYE && stack.getItemMeta().isUnbreakable() && stack.getItemMeta().getDisplayName().endsWith(TextUtils.format("&6Orbe de Vida"));
    }

    public static boolean isWaterMedal(ItemStack stack) {
        if (stack == null) return false;
        if (!stack.hasItemMeta()) return false;
        return stack.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(Bukkit.getPluginManager().getPlugin("Permadeath"), "water_medal"), PersistentDataType.BYTE);
    }

    public static boolean isSurvivorMedal(ItemStack stack) {
        if (stack == null) return false;
        if (!stack.hasItemMeta()) return false;
        return stack.getType() == Material.TOTEM_OF_UNDYING && stack.getItemMeta().getDisplayName().endsWith(TextUtils.format("&6&lMedalla de Superviviente"));
    }
    
    public static boolean isLockItem(ItemStack stack) {
        return stack != null && stack.getType() == Material.STRUCTURE_VOID;
    }
}

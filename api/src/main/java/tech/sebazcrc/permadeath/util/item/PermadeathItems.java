package tech.sebazcrc.permadeath.util.item;

import org.bukkit.GameMode;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
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
        ItemStack s = new ItemBuilder(Material.HEART_OF_THE_SEA)
                .setDisplayName(TextUtils.format("&bMedalla de Agua"))
                .setLore(Arrays.asList(TextUtils.format("&7Otorga respiración infinita"), TextUtils.format("&7mientras esté en el inventario.")))
                .setUnbrekeable(true)
                .addEnchant(Enchantment.INFINITY, 1)
                .addItemFlag(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS)
                .build();
        ItemMeta meta = s.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey("permadeath", "water_medal"), PersistentDataType.BYTE, (byte) 1);
        s.setItemMeta(meta);
        return s;
    }

    public static ItemStack createAbyssalHeart() {
        return new ItemBuilder(Material.RECOVERY_COMPASS)
                .setDisplayName(TextUtils.format("&3Corazón del Abismo"))
                .setLore(Arrays.asList(
                        TextUtils.format("&7Un núcleo de energía oscura extraído"),
                        TextUtils.format("&7de las profundidades del Abismo."),
                        "",
                        TextUtils.format("&eClick derecho para despertar al mundo..."),
                        TextUtils.format("&c(Requiere estar en el Día 60)")
                ))
                .addEnchant(Enchantment.INFINITY, 1)
                .addItemFlag(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS)
                .build();
    }

    public static ItemStack createAbyssalMask() {
        ItemStack s = new tech.sebazcrc.permadeath.util.lib.LeatherArmorBuilder(Material.LEATHER_HELMET, 1)
                .setColor(org.bukkit.Color.fromRGB(0x1A1A1A))
                .setDisplayName(TextUtils.format("&bMáscara del Abismo"))
                .setLore(Arrays.asList(
                        TextUtils.format("&7Protege contra la presión del Abismo."),
                        TextUtils.format("&7Se desgasta mientras estás en la dimensión."),
                        "",
                        TextUtils.format("&eUsa un Filtro Abisal para recuperarte.")
                ))
                .build();
        
        ItemMeta meta = s.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey("permadeath", "abyssal_item"), PersistentDataType.STRING, "mask");
        s.setItemMeta(meta);
        return s;
    }

    public static ItemStack createAbyssalFilter() {
        ItemStack item = new ItemBuilder(Material.NETHER_WART)
                .setDisplayName(TextUtils.format("&bFiltro Abisal"))
                .setLore(Arrays.asList(
                        TextUtils.format("&7Un purificador de aire diseñado"),
                        TextUtils.format("&7para entornos de vacío."),
                        "",
                        TextUtils.format("&eClick derecho con la máscara en la"),
                        TextUtils.format("&emano para restaurar el oxígeno.")
                ))
                .setCustomModelData(101)
                .build();

        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey("permadeath", "abyssal_item"), PersistentDataType.STRING, "filter");
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createVoidShard() {
        return new ItemBuilder(Material.AMETHYST_SHARD)
                .setDisplayName(TextUtils.format("&3Fragmento de Vacío"))
                .setLore(Arrays.asList(TextUtils.format("&7Materia cristalizada del Abismo.")))
                .setCustomModelData(102)
                .build();
    }

    public static ItemStack createAbyssalOre() {
        return new ItemBuilder(Material.DEEPSLATE_EMERALD_ORE)
                .setDisplayName(TextUtils.format("&bMineral Abisal"))
                .setLore(Arrays.asList(
                        TextUtils.format("&7Un mineral imbuido con"),
                        TextUtils.format("&7la energía del vacío."),
                        "",
                        TextUtils.format("&7Puede ser procesado para obtener"),
                        TextUtils.format("&7materiales de Netherite Infernal.")
                ))
                .setCustomModelData(103)
                .build();
    }

    public static ItemStack createAbyssalPotion() {
        ItemStack potion = new ItemStack(Material.POTION);
        org.bukkit.inventory.meta.PotionMeta meta = (org.bukkit.inventory.meta.PotionMeta) potion.getItemMeta();
        meta.setDisplayName(TextUtils.format("&bPoción de Respiración Abisal"));
        meta.setLore(Arrays.asList(
                TextUtils.format("&7Proporciona inmunidad a la"),
                TextUtils.format("&7presión del Abismo."),
                "",
                TextUtils.format("&fDuración: &b2:00"),
                "",
                TextUtils.format("&eClick derecho para beber.")
        ));
        meta.setColor(Color.fromRGB(0x001A33)); // Azul muy oscuro
        // Marcador persistente
        meta.getPersistentDataContainer().set(new NamespacedKey("permadeath", "abyssal_potion"), PersistentDataType.BYTE, (byte) 1);
        
        meta.addCustomEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.WATER_BREATHING, 20 * 120, 0, false, false, true), true);
        potion.setItemMeta(meta);
        return potion;
    }

    public static boolean isAbyssalPotion(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR) return false;
        if (!stack.hasItemMeta()) return false;
        NamespacedKey key = new NamespacedKey("permadeath", "abyssal_potion");
        return stack.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE);
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

    public static ItemStack createInfernalNetheriteBlock() {
        ItemStack s = new ItemStack(Material.DIAMOND);
        ItemMeta meta = s.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6Bloque de Netherite Infernal");
            meta.setUnbreakable(true);
            s.setItemMeta(meta);
        }
        return s;
    }

    public static java.util.List<ItemStack> createNetheriteTools() {
        return java.util.Arrays.asList(
                craftNetheriteSword(),
                craftNetheritePickaxe(),
                craftNetheriteAxe(),
                craftNetheriteShovel(),
                craftNetheriteHoe()
        );
    }

    public static ItemStack craftInfernalNetheriteIngot() {
        ItemStack s = new ItemBuilder(Material.DIAMOND).setCustomModelData(1, !PermadeathAPI.optifineItemsEnabled()).setDisplayName(TextUtils.format("&6Lingote de Netherite Infernal")).build();
        ItemMeta meta = s.getItemMeta();
        meta.setUnbreakable(true);
        meta.setLore(Arrays.asList(HiddenStringUtils.encodeString("{" + UUID.randomUUID() + ": 0}")));
        s.setItemMeta(meta);
        return s;
    }

    public static ItemStack createAccessoryTrigger() {
        return new ItemBuilder(Material.NETHER_STAR)
                .setDisplayName(TextUtils.format("&6Menú de Accesorios &7(Click Derecho)"))
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
        
        // También revisar el CURSOR (Importante para evitar bloqueos mientras se mueve la reliquia)
        ItemStack cursor = p.getItemOnCursor();
        if (cursor != null && cursor.getType() != Material.AIR) {
            if (!hasBeginningRelic && isBeginningRelic(cursor)) {
                hasBeginningRelic = true;
                hasEndRelic = true;
            } else if (!hasEndRelic && isEndRelic(cursor)) {
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
            // Si ya está bloqueado, no hacer nada
            if (item.getType() == Material.STRUCTURE_VOID) return;
            
            // Si hay un item real, expulsarlo
            p.getWorld().dropItem(p.getLocation(), item.clone());
            
            // Feedback visual y sonoro (solo al expulsar)
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.5f, 2.0f);
            p.spawnParticle(Particle.SMOKE, p.getLocation().add(0, 1, 0), 5, 0.2, 0.2, 0.2, 0.05);
        }
        
        // Bloquear el slot
        ItemStack lock = new ItemBuilder(Material.STRUCTURE_VOID)
                .setDisplayName(TextUtils.format("&cSLOT BLOQUEADO"))
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

    public static boolean isAbyssalMask(ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return false;
        String tag = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey("permadeath", "abyssal_item"), PersistentDataType.STRING);
        return "mask".equals(tag);
    }

    public static boolean isAbyssalHeart(ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return false;
        return item.getType() == Material.RECOVERY_COMPASS && item.getItemMeta().hasEnchant(Enchantment.INFINITY);
    }

    public static boolean isAbyssalFilter(ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return false;
        String tag = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey("permadeath", "abyssal_item"), PersistentDataType.STRING);
        return "filter".equals(tag);
    }
    
    // Métodos auxiliares para no romper otras clases que los usan, pero slotBlock ya no los usa
    public static boolean isLifeOrb(ItemStack stack) {
        if (stack == null) return false;
        if (!stack.hasItemMeta()) return false;
        return stack.getType() == Material.BROWN_DYE && stack.getItemMeta().isUnbreakable() && stack.getItemMeta().getDisplayName().endsWith(TextUtils.format("&6Orbe de Vida"));
    }

    public static boolean isWaterMedal(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR) return false;
        if (!stack.hasItemMeta()) return false;
        return stack.getItemMeta().getPersistentDataContainer().has(new NamespacedKey("permadeath", "water_medal"), PersistentDataType.BYTE);
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

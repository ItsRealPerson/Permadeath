package dev.itsrealperson.permadeath.nms.v1_21_R3.utils;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {

    /**
     * Equipa una armadura completa a una entidad.
     * @param entity La entidad.
     * @param helmet Casco (puede ser null).
     * @param chestplate Pechera (puede ser null).
     * @param leggings Pantalones (puede ser null).
     * @param boots Botas (puede ser null).
     */
    public static void equipArmor(LivingEntity entity, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) {
        EntityEquipment equipment = entity.getEquipment();
        if (equipment != null) {
            if (helmet != null) equipment.setHelmet(helmet);
            if (chestplate != null) equipment.setChestplate(chestplate);
            if (leggings != null) equipment.setLeggings(leggings);
            if (boots != null) equipment.setBoots(boots);
        }
    }

    /**
     * Establece el objeto en la mano principal de la entidad.
     */
    public static void setMainHand(LivingEntity entity, Material material) {
        EntityEquipment equipment = entity.getEquipment();
        if (equipment != null) {
            equipment.setItemInMainHand(material == null ? null : new ItemStack(material));
        }
    }

    /**
     * Establece el objeto en la mano secundaria (off-hand).
     */
    public static void setOffHand(LivingEntity entity, Material material) {
        EntityEquipment equipment = entity.getEquipment();
        if (equipment != null) {
            equipment.setItemInOffHand(material == null ? null : new ItemStack(material));
        }
    }

    /**
     * Define la probabilidad de drop de todo el equipamiento a 0.
     * Útil para mobs personalizados cuyos items no deben ser obtenidos por jugadores.
     */
    public static void clearDropChances(LivingEntity entity) {
        EntityEquipment eq = entity.getEquipment();
        if (eq == null) return;

        eq.setHelmetDropChance(0f);
        eq.setChestplateDropChance(0f);
        eq.setLeggingsDropChance(0f);
        eq.setBootsDropChance(0f);
        eq.setItemInMainHandDropChance(0f);
        eq.setItemInOffHandDropChance(0f);
    }
}

package dev.itsrealperson.permadeath.world.beginning;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.util.TextUtils;
import dev.itsrealperson.permadeath.util.item.PermadeathItems;
import dev.itsrealperson.permadeath.util.lib.ItemBuilder;
import dev.itsrealperson.permadeath.util.lib.LeatherArmorBuilder;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.SplittableRandom;

public class BeginningMobs {

    private static final SplittableRandom random = new SplittableRandom();

    public static void spawnMob(Location location) {
        Main plugin = Main.getInstance();
        int p = random.nextInt(101);

        if (p <= 60) {
            WitherSkeleton skeleton = (WitherSkeleton) plugin.getNmsHandler().spawnNMSEntity("SkeletonWither", EntityType.WITHER_SKELETON, location, CreatureSpawnEvent.SpawnReason.CUSTOM);

            skeleton.getEquipment().setChestplate(new LeatherArmorBuilder(Material.LEATHER_CHESTPLATE, 1).setColor(Color.fromRGB(255, 182, 193)).build());
            skeleton.getEquipment().setBoots(new LeatherArmorBuilder(Material.LEATHER_BOOTS, 1).setColor(Color.fromRGB(255, 182, 193)).build());

            int enchantLevel = random.nextInt(5) + 1;
            skeleton.getEquipment().setItemInMainHand(new ItemBuilder(PermadeathItems.craftNetheriteSword()).addEnchant(Enchantment.SHARPNESS, enchantLevel).build());

            skeleton.getEquipment().setChestplateDropChance(0);
            skeleton.getEquipment().setBootsDropChance(0);
            skeleton.getEquipment().setItemInMainHandDropChance(0);

            skeleton.setCustomName(TextUtils.format("&6Wither Skeleton Rosáceo"));
            skeleton.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
            plugin.getNmsAccessor().setMaxHealth(skeleton, 100.0D, true);
        }

        if (p > 60 && p <= 75) {
            Vex vex = location.getWorld().spawn(location, Vex.class);
            vex.getEquipment().setHelmet(new ItemBuilder(Material.HONEY_BLOCK).addEnchant(Enchantment.PROTECTION, 4).build());
            vex.getEquipment().setItemInMainHand(new ItemBuilder(Material.END_CRYSTAL).addEnchant(Enchantment.SHARPNESS, 15).addEnchant(Enchantment.KNOCKBACK, 10).build());
            vex.getEquipment().setHelmetDropChance(0);
            vex.getEquipment().setItemInMainHandDropChance(0);

            vex.setCustomName(TextUtils.format("&6Vex Definitivo"));
        }

        if (p > 75 && p <= 79) {
            Ghast ghast = (Ghast) plugin.getNmsHandler().spawnCustomGhast(location.clone().add(0, 5, 0), CreatureSpawnEvent.SpawnReason.CUSTOM, true);
            plugin.getNmsAccessor().setMaxHealth(ghast, 150.0D, true);
            ghast.setCustomName(TextUtils.format("&6Ender Ghast Definitivo"));
        }

        if (p >= 80) {
            Creeper c = plugin.getFactory().spawnEnderQuantumCreeper(location, null);
            plugin.getNmsAccessor().setMaxHealth(c, 100.0D, true);
            c.setExplosionRadius(7);
        }
    }
}

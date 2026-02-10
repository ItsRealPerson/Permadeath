package dev.itsrealperson.permadeath.nms.v1_21_R3.entity;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.SpawnerMinecart;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import dev.itsrealperson.permadeath.api.interfaces.DeathModule;

public class DeathModuleImpl implements DeathModule {
    @Override
    public void spawn(Location where) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Permadeath");
        if (plugin == null) return;

        // Base estática: ArmorStand invisible
        ArmorStand base = where.getWorld().spawn(where, ArmorStand.class);
        base.setVisible(false);
        base.setGravity(false);
        base.setSmall(true);
        base.setMarker(true);
        base.setAI(false);
        base.setInvulnerable(true);
        base.getPersistentDataContainer().set(new NamespacedKey(plugin, "death_module_part"), PersistentDataType.BYTE, (byte) 1);

        // Shulker: El cuerpo del módulo
        Shulker shulker = where.getWorld().spawn(where, Shulker.class);
        shulker.setColor(DyeColor.RED);
        shulker.setAI(false);
        shulker.setRemoveWhenFarAway(false);
        shulker.getPersistentDataContainer().set(new NamespacedKey(plugin, "death_module_shulker"), PersistentDataType.BYTE, (byte) 1);
        shulker.getPersistentDataContainer().set(new NamespacedKey(plugin, "death_module_part"), PersistentDataType.BYTE, (byte) 1);

        // Minecart con Spawner: En la cima
        SpawnerMinecart spawnerMinecart = where.getWorld().spawn(where, SpawnerMinecart.class);
        spawnerMinecart.setSpawnedType(EntityType.SPLASH_POTION);
        spawnerMinecart.setMinSpawnDelay(60);
        spawnerMinecart.setMaxSpawnDelay(150);
        spawnerMinecart.setSpawnRange(5);
        spawnerMinecart.setRequiredPlayerRange(32);
        spawnerMinecart.setSpawnCount(4);
        
        spawnerMinecart.getPersistentDataContainer().set(new NamespacedKey(plugin, "module_minecart"), PersistentDataType.BYTE, (byte) 1);
        spawnerMinecart.getPersistentDataContainer().set(new NamespacedKey(plugin, "death_module_part"), PersistentDataType.BYTE, (byte) 1);

        // Montar el stack: ArmorStand -> Shulker -> SpawnerMinecart
        base.addPassenger(shulker);
        shulker.addPassenger(spawnerMinecart);
    }
}




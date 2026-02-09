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
        SpawnerMinecart spawnerMinecart = where.getWorld().spawn(where, SpawnerMinecart.class);
        
        // Configuramos el spawner usando la API de Bukkit
        spawnerMinecart.setSpawnedType(EntityType.SPLASH_POTION);
        spawnerMinecart.setMinSpawnDelay(60);
        spawnerMinecart.setMaxSpawnDelay(150);
        spawnerMinecart.setDelay(0);
        spawnerMinecart.setSpawnRange(5);
        spawnerMinecart.setRequiredPlayerRange(32);
        spawnerMinecart.setSpawnCount(4);

        // Marcamos el minecart
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Permadeath");
        if (plugin != null) {
            spawnerMinecart.getPersistentDataContainer().set(new NamespacedKey(plugin, "module_minecart"), PersistentDataType.BYTE, (byte)1);
        }

        // Creamos la estructura de pasajeros: CaveSpider -> Shulker -> SpawnerMinecart
        CaveSpider spider = where.getWorld().spawn(where, CaveSpider.class);
        Shulker shulker = where.getWorld().spawn(where, Shulker.class);
        shulker.setColor(DyeColor.RED);
        
        shulker.addPassenger(spawnerMinecart);
        spider.addPassenger(shulker);
    }
}




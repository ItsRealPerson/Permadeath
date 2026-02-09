package dev.itsrealperson.permadeath.util.gui;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.util.TextUtils;
import dev.itsrealperson.permadeath.util.lib.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

/**
 * Menú de Toggles (Interruptores).
 */
public class TogglesMenu extends AbstractMenu {

    private static final NamespacedKey PATH_KEY = new NamespacedKey(Main.instance, "config_path");

    public TogglesMenu() {
        super(54, TextUtils.format("&8PDC Editor: Toggles"));
    }

    @Override
    public void setMenuItems(Player player) {
        inventory.clear();

        // Los botones ahora son los iconos mismos para evitar solapamientos
        addToggle(10, "ban-enabled", "Baneos al Morir", Material.IRON_DOOR);
        addToggle(11, "anti-afk-enabled", "Anti-AFK", Material.CLOCK);
        addToggle(12, "Toggles.Op-Ban", "Baneo a Operadores", Material.COMMAND_BLOCK);
        
        addToggle(19, "Toggles.Hostile-Mobs", "Mobs Hostiles (Día 20+)", Material.ZOMBIE_HEAD);
        addToggle(20, "Toggles.Player-Skulls", "Cabezas de Jugador", Material.PLAYER_HEAD);
        addToggle(21, "Toggles.Spider-Effect", "Efectos en Arañas", Material.SPIDER_EYE);
        
        addToggle(28, "Toggles.Mike-Creeper-Spawn", "Creepers en Luz (Día 60+)", Material.CREEPER_HEAD);
        addToggle(29, "Toggles.Doble-Mob-Cap", "Doblar Mob-Cap", Material.SPAWNER);
        addToggle(30, "Toggles.OptifineItems", "Ítems de Optifine", Material.SPYGLASS);
        
        addToggle(37, "Toggles.DefaultDeathSoundsEnabled", "Sonidos de Muerte", Material.NOTE_BLOCK);
        addToggle(38, "Toggles.Optimizar-Mob-Spawns", "Optimizar Spawns", Material.REPEATER);
        addToggle(39, "Toggles.Replace-Mobs-On-Chunk-Load", "Reemplazo de Mobs", Material.HOPPER);

        // Modo Debug
        addToggle(43, "Toggles.Debug", "Modo Debug", Material.BLAZE_POWDER);

        // Volver
        inventory.setItem(49, new ItemBuilder(Material.ARROW)
                .setDisplayName("&aVolver")
                .build());
    }

    private void addToggle(int slot, String path, String name, Material icon) {
        boolean enabled = Main.instance.getConfig().getBoolean(path, false);
        List<String> lore = new ArrayList<>();
        lore.add("&7Estado: " + (enabled ? "&aACTIVADO" : "&cDESACTIVADO"));
        lore.add(" ");
        lore.add("&eHaz clic para cambiar.");

        ItemBuilder builder = new ItemBuilder(icon)
                .setDisplayName((enabled ? "&a" : "&c") + name)
                .setLore(lore);
        
        if (enabled) {
            try {
                builder.addEnchant(org.bukkit.enchantments.Enchantment.getByKey(NamespacedKey.minecraft("luck")), 1);
            } catch (Exception ignored) {}
            builder.addItemFlag(ItemFlag.HIDE_ENCHANTS);
        }

        ItemStack item = builder.build();

        // Guardar el path en el ítem
        var meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(PATH_KEY, PersistentDataType.STRING, path);
        item.setItemMeta(meta);

        inventory.setItem(slot, item);
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        
        if (event.getSlot() == 49) {
            new ConfigMenu().open(player);
            return;
        }

        if (item != null && item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(PATH_KEY, PersistentDataType.STRING)) {
            String path = item.getItemMeta().getPersistentDataContainer().get(PATH_KEY, PersistentDataType.STRING);
            boolean current = Main.instance.getConfig().getBoolean(path, false);
            
            Main.instance.getConfig().set(path, !current);
            Main.instance.saveConfig();
            
            // Si es el toggle de Debug, actualizar la variable estática
            if (path.equalsIgnoreCase("Toggles.Debug")) {
                Main.DEBUG = !current;
            }
            
            player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            setMenuItems(player); // Refrescar menú
        }
    }
}
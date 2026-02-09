package dev.itsrealperson.permadeath.util.gui;

import dev.itsrealperson.permadeath.util.TextUtils;
import dev.itsrealperson.permadeath.util.lib.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * Menú principal del Editor de Configuración.
 */
public class ConfigMenu extends AbstractMenu {

    public ConfigMenu() {
        super(27, TextUtils.format("&8PDC Editor: Categorías"));
    }

    @Override
    public void setMenuItems(Player player) {
        // Fondo
        ItemStack glass = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build();
        for (int i = 0; i < 27; i++) inventory.setItem(i, glass);

        // 1. Toggles
        inventory.setItem(10, new ItemBuilder(Material.LEVER)
                .setDisplayName("&e&l⚙ Toggles")
                .setLore(Arrays.asList("&7Configura los interruptores globales", "&7del plugin (Mecánicas, Mobs, etc)."))
                .build());

        // 2. Progresión
        inventory.setItem(11, new ItemBuilder(Material.EXPERIENCE_BOTTLE)
                .setDisplayName("&b&l📈 Progresión")
                .setLore(Arrays.asList("&7Gestiona el avance de los días,", "&7hitos mágicos y dificultad."))
                .build());

        // 3. Loot Editor
        inventory.setItem(12, new ItemBuilder(Material.CHEST)
                .setDisplayName("&6&l📦 Loot Editor")
                .setLore(Arrays.asList("&7Ajusta las probabilidades de loot", "&7y gestiona ítems personalizados."))
                .build());

        // 4. Player Admin
        inventory.setItem(14, new ItemBuilder(Material.PLAYER_HEAD)
                .setDisplayName("&d&l💀 Player Admin")
                .setLore(Arrays.asList("&7Gestión de vida, idiomas y", "&7perfiles de jugadores."))
                .build());

        // 5. Seguridad
        inventory.setItem(15, new ItemBuilder(Material.SHIELD)
                .setDisplayName("&c&l🛡 Seguridad")
                .setLore(Arrays.asList("&7Factory Reset, Snapshots y", "&7registros administrativos."))
                .build());

        // 6. Archivos
        inventory.setItem(16, new ItemBuilder(Material.PAPER)
                .setDisplayName("&f&l📂 Archivos")
                .setLore(Arrays.asList("&7Gestión directa de archivos YAML", "&7y estado de la base de datos."))
                .build());

        // Cerrar
        inventory.setItem(22, new ItemBuilder(Material.BARRIER)
                .setDisplayName("&cCerrar Editor")
                .build());
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        switch (slot) {
            case 10 -> new TogglesMenu().open(player);
            case 11 -> new ProgressionMenu().open(player);
            case 12 -> new LootEditorMenu().open(player);
            case 14 -> new PlayerAdminMenu().open(player);
            case 15 -> new SecurityMenu().open(player);
            case 16 -> new FileMenu().open(player);
            case 22 -> player.closeInventory();
        }
    }
}

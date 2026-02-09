package dev.itsrealperson.permadeath.util.gui;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.util.TextUtils;
import dev.itsrealperson.permadeath.util.lib.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.io.File;
import java.util.Arrays;

/**
 * Menú de Gestión de Archivos.
 */
public class FileMenu extends AbstractMenu {

    public FileMenu() {
        super(27, TextUtils.format("&8PDC Editor: Archivos"));
    }

    @Override
    public void setMenuItems(Player player) {
        inventory.clear();

        // 1. config.yml
        addFileItem(10, "config.yml", Material.WRITABLE_BOOK);
        // 2. mensajes_ES.yml
        addFileItem(11, "mensajes_ES.yml", Material.PAPER);
        // 3. loot.yml
        addFileItem(12, "loot.yml", Material.CHEST);
        // 4. fecha.yml
        addFileItem(13, "fecha.yml", Material.CLOCK);

        // Volver
        inventory.setItem(22, new ItemBuilder(Material.ARROW).setDisplayName("&aVolver").build());
    }

    private void addFileItem(int slot, String fileName, Material icon) {
        File file = new File(Main.instance.getDataFolder(), fileName);
        boolean exists = file.exists();

        inventory.setItem(slot, new ItemBuilder(icon)
                .setDisplayName("&f" + fileName)
                .setLore(Arrays.asList(
                        "&7Estado: " + (exists ? "&aCargado" : "&cNo encontrado"),
                        "&7Tamaño: &f" + (exists ? (file.length() / 1024) + " KB" : "0 KB"),
                        " ",
                        "&eHaz clic para recargar este archivo."))
                .build());
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        if (slot == 22) {
            new ConfigMenu().open(player);
            return;
        }

        if (slot >= 10 && slot <= 13) {
            player.sendMessage(TextUtils.format(Main.prefix + "&eRecargando archivos..."));
            Main.instance.reloadConfig();
            player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            setMenuItems(player);
        }
    }
}

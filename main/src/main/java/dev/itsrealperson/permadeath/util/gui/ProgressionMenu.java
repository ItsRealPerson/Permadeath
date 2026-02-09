package dev.itsrealperson.permadeath.util.gui;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.data.DateManager;
import dev.itsrealperson.permadeath.util.TextUtils;
import dev.itsrealperson.permadeath.util.lib.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * Menú de Gestión de Progresión.
 */
public class ProgressionMenu extends AbstractMenu {

    public ProgressionMenu() {
        super(45, TextUtils.format("&8PDC Editor: Progresión"));
    }

    @Override
    public void setMenuItems(Player player) {
        inventory.clear();
        long day = DateManager.getInstance().getDay();

        // Día Actual
        inventory.setItem(13, new ItemBuilder(Material.CLOCK)
                .setDisplayName("&b&lDía Actual: &f" + day)
                .setLore(Arrays.asList("&7El tiempo fluye según la fecha del sistema.", "&7SpeedRun Mode: " + (Main.SPEED_RUN_MODE ? "&aON" : "&cOFF")))
                .build());

        // Controles de Tiempo
        inventory.setItem(21, new ItemBuilder(Material.RED_WOOL).setDisplayName("&c&l-10 Días").build());
        inventory.setItem(22, new ItemBuilder(Material.ORANGE_WOOL).setDisplayName("&6&l-1 Día").build());
        inventory.setItem(23, new ItemBuilder(Material.LIME_WOOL).setDisplayName("&a&l+1 Día").build());
        inventory.setItem(24, new ItemBuilder(Material.GREEN_WOOL).setDisplayName("&2&l+10 Días").build());

        // Hitos (Visualización/Edición básica)
        addMilestone(31, "Progression.BeginningStartDay", "The Beginning (Aparición)", Material.END_PORTAL_FRAME);
        addMilestone(32, "Progression.AbyssStartDay", "The Abyss (Aparición)", Material.SCULK_SHRIEKER);

        // Volver
        inventory.setItem(40, new ItemBuilder(Material.ARROW).setDisplayName("&aVolver").build());
    }

    private void addMilestone(int slot, String path, String name, Material icon) {
        int day = Main.instance.getConfig().getInt(path);
        inventory.setItem(slot, new ItemBuilder(icon)
                .setDisplayName("&e" + name)
                .setLore(Arrays.asList("&7Día configurado: &f" + day, " ", "&8(Usa /pdc config progression [dia] para cambiarlo)"))
                .build());
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        long currentDay = DateManager.getInstance().getDay();

        switch (slot) {
            case 21 -> changeDay(player, (int) (currentDay - 10));
            case 22 -> changeDay(player, (int) (currentDay - 1));
            case 23 -> changeDay(player, (int) (currentDay + 1));
            case 24 -> changeDay(player, (int) (currentDay + 10));
            case 40 -> new ConfigMenu().open(player);
        }
    }

    private void changeDay(Player player, int newDay) {
        if (newDay < 1) newDay = 1;
        DateManager.getInstance().setDay(player, String.valueOf(newDay));
        player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.0f);
        setMenuItems(player); // Refrescar
    }
}

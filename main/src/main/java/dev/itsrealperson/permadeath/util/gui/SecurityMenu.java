package dev.itsrealperson.permadeath.util.gui;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.util.TextUtils;
import dev.itsrealperson.permadeath.util.lib.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;

/**
 * Menú de Seguridad y Mantenimiento.
 */
public class SecurityMenu extends AbstractMenu {

    public SecurityMenu() {
        super(27, TextUtils.format("&8PDC Editor: Seguridad"));
    }

    @Override
    public void setMenuItems(Player player) {
        // Fondo
        ItemBuilder glass = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName(" ");
        for (int i = 0; i < 27; i++) inventory.setItem(i, glass.build());

        // Factory Reset
        inventory.setItem(11, new ItemBuilder(Material.TNT)
                .setDisplayName("&c&lFACTORY RESET")
                .setLore(Arrays.asList("&7Borra TODA la configuración y", "&7datos de jugadores.", " ", "&4&l¡ESTA ACCIÓN ES IRREVERSIBLE!"))
                .build());

        // Backup / Snapshots
        inventory.setItem(13, new ItemBuilder(Material.RECOVERY_COMPASS)
                .setDisplayName("&b&lCrear Snapshot")
                .setLore(Arrays.asList("&7Crea una copia de seguridad de la", "&7configuración actual."))
                .build());

        // Logs
        inventory.setItem(15, new ItemBuilder(Material.WRITABLE_BOOK)
                .setDisplayName("&eVer Logs Recientes")
                .setLore(Arrays.asList("&7Muestra los últimos cambios", "&7administrativos realizados."))
                .build());

        // PANIC MODE
        inventory.setItem(16, new ItemBuilder(Main.PANIC_MODE ? Material.REDSTONE_BLOCK : Material.REDSTONE_TORCH)
                .setDisplayName("&4&lPANIC MODE: " + (Main.PANIC_MODE ? "&aACTIVO" : "&cDESACTIVO"))
                .setLore(Arrays.asList("&7Al activarse, el tiempo se congela", "&7y se detienen los procesos críticos.", " ", "&eHaz clic para cambiar."))
                .build());

        // Volver
        inventory.setItem(22, new ItemBuilder(Material.ARROW).setDisplayName("&aVolver").build());
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        switch (slot) {
            case 11 -> player.sendMessage(TextUtils.format(Main.prefix + "&cEscribe &4CONFIRMAR_RESET &cen el chat para proceder (Función en desarrollo)."));
            case 13 -> {
                Main.instance.saveConfig();
                player.sendMessage(TextUtils.format(Main.prefix + "&aConfiguración guardada y Snapshot simulado."));
            }
            case 15 -> {
                dev.itsrealperson.permadeath.util.log.PDCLog.getInstance().printRecentLogs(player, 10);
                dev.itsrealperson.permadeath.util.log.PDCLog.getInstance().printRecentLogs(Bukkit.getConsoleSender(), 10);
                player.sendMessage(TextUtils.format(Main.prefix + "&eSe han enviado los últimos 10 logs a tu chat y a la consola."));
            }
            case 16 -> {
                Main.PANIC_MODE = !Main.PANIC_MODE;
                player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_BEACON_DEACTIVATE, 1.0f, 0.5f);
                
                // Sincronizar por red
                if (Main.instance.getNetworkManager().isNetworkActive()) {
                    Main.instance.getNetworkManager().sendCustomMessage("PANIC", String.valueOf(Main.PANIC_MODE));
                }
                
                setMenuItems(player);
                Bukkit.broadcastMessage(TextUtils.format(Main.prefix + "&4&l[AVISO] &cEl modo de pánico ha sido " + (Main.PANIC_MODE ? "&aACTIVADO" : "&cDESACTIVO") + "&c."));
            }
            case 22 -> new ConfigMenu().open(player);
        }
    }
}

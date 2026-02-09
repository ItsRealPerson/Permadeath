package dev.itsrealperson.permadeath.util.gui;

import dev.itsrealperson.permadeath.util.TextUtils;
import dev.itsrealperson.permadeath.util.lib.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * Menú de Selección de Jugadores para Administración.
 */
public class PlayerAdminMenu extends AbstractMenu {

    public PlayerAdminMenu() {
        super(54, TextUtils.format("&8PDC Editor: Jugadores"));
    }

    @Override
    public void setMenuItems(Player player) {
        inventory.clear();

        int slot = 0;
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (slot >= 45) break;

            ItemStack head = new ItemBuilder(Material.PLAYER_HEAD)
                    .setDisplayName("&b" + online.getName())
                    .setLore(java.util.Arrays.asList("&7Haz clic para gestionar", "&7el perfil de este jugador."))
                    .build();
            
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwningPlayer(online);
            head.setItemMeta(meta);

            inventory.setItem(slot++, head);
        }

        // Volver
        inventory.setItem(49, new ItemBuilder(Material.ARROW).setDisplayName("&aVolver").build());
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player admin = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();

        if (event.getSlot() == 49) {
            new ConfigMenu().open(admin);
            return;
        }

        if (item != null && item.getType() == Material.PLAYER_HEAD) {
            String targetName = TextUtils.stripColor(item.getItemMeta().getDisplayName());
            new PlayerProfileMenu(targetName).open(admin);
            admin.playSound(admin.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
        }
    }
}

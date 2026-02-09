package dev.itsrealperson.permadeath.util.gui;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.api.Language;
import dev.itsrealperson.permadeath.data.PlayerDataManager;
import dev.itsrealperson.permadeath.util.TextUtils;
import dev.itsrealperson.permadeath.util.lib.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

/**
 * Menú de gestión de perfil individual de un jugador.
 */
public class PlayerProfileMenu extends AbstractMenu {

    private final String targetName;
    private final PlayerDataManager dataManager;

    public PlayerProfileMenu(String targetName) {
        super(45, TextUtils.format("&8PDC Perfil: " + targetName));
        this.targetName = targetName;
        this.dataManager = new PlayerDataManager(targetName, Main.instance);
    }

    @Override
    public void setMenuItems(Player player) {
        inventory.clear();

        // Fondo
        ItemStack glass = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build();
        for (int i = 0; i < 45; i++) inventory.setItem(i, glass);

        // Cabezal
        ItemStack head = new ItemBuilder(Material.PLAYER_HEAD)
                .setDisplayName("&b&l" + targetName)
                .setLore(Arrays.asList("&7Último día activo: &f" + dataManager.getLastDay()))
                .build();
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwner(targetName);
        head.setItemMeta(meta);
        inventory.setItem(4, head);

        // 1. Salud Extra
        int hp = dataManager.getExtraHP();
        inventory.setItem(19, new ItemBuilder(Material.APPLE)
                .setDisplayName("&c&lSalud Extra: &f" + hp + " HP")
                .setLore(Arrays.asList("&7Añade o quita vida máxima extra.", " ", "&eClick Izquierdo: &a+2 HP", "&eClick Derecho: &c-2 HP"))
                .build());

        // 2. Idioma
        Language lang = dataManager.getLanguage();
        inventory.setItem(22, new ItemBuilder(Material.WRITABLE_BOOK)
                .setDisplayName("&e&lIdioma: &f" + lang.name())
                .setLore(Arrays.asList("&7Cambia el idioma de los mensajes", "&7del jugador.", " ", "&eHaz clic para rotar."))
                .build());

        // 3. Resetear Perfil
        inventory.setItem(25, new ItemBuilder(Material.LAVA_BUCKET)
                .setDisplayName("&4&lRESETEAR DATOS")
                .setLore(Arrays.asList("&7Borra las estadísticas de este jugador.", " ", "&c¡NO SE PUEDE DESHACER!"))
                .build());

        // Volver
        inventory.setItem(40, new ItemBuilder(Material.ARROW).setDisplayName("&aVolver a la lista").build());
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player admin = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        switch (slot) {
            case 19 -> {
                int current = dataManager.getExtraHP();
                if (event.isLeftClick()) {
                    dataManager.setExtraHP(current + 2);
                } else if (event.isRightClick()) {
                    dataManager.setExtraHP(Math.max(0, current - 2));
                }
                
                // Actualizar vida al instante si el jugador está online
                Player target = org.bukkit.Bukkit.getPlayer(targetName);
                if (target != null && target.isOnline()) {
                    dev.itsrealperson.permadeath.util.item.NetheriteArmor.setupHealth(target);
                }
                
                admin.playSound(admin.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                setMenuItems(admin);
            }
            case 22 -> {
                Language current = dataManager.getLanguage();
                Language next = (current == Language.SPANISH) ? Language.ENGLISH : Language.SPANISH;
                dataManager.setLanguage(next);
                admin.playSound(admin.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                setMenuItems(admin);
            }
            case 25 -> admin.sendMessage(TextUtils.format(Main.prefix + "&cFunción de reset individual en desarrollo."));
            case 40 -> new PlayerAdminMenu().open(admin);
        }
    }
}

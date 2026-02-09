package dev.itsrealperson.permadeath.util.gui;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.util.LootManager;
import dev.itsrealperson.permadeath.util.TextUtils;
import dev.itsrealperson.permadeath.util.lib.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;

/**
 * Menú para gestionar el loot de las cápsulas del Abismo.
 */
public class LootEditorMenu extends AbstractMenu {

    private static final NamespacedKey INDEX_KEY = new NamespacedKey(Main.instance, "loot_index");

    public LootEditorMenu() {
        super(54, TextUtils.format("&8PDC Editor: Loot (Abismo)"));
    }

    @Override
    public void setMenuItems(Player player) {
        inventory.clear();
        LootManager manager = (LootManager) Main.instance.getLootManager();
        List<String> entries = manager.getAbyssLootEntries();

        int slot = 0;
        for (int i = 0; i < entries.size(); i++) {
            if (slot >= 45) break;

            String[] split = entries.get(i).split(";");
            try {
                Material mat = Material.valueOf(split[0]);
                int maxAmt = Integer.parseInt(split[1]);
                int chance = Integer.parseInt(split[2]);

                ItemStack item = new ItemBuilder(mat)
                        .setDisplayName("&b&l" + mat.name())
                        .setLore(Arrays.asList(
                                "&7Cantidad Máxima: &f" + maxAmt,
                                "&7Probabilidad: &f" + chance + "%",
                                " ",
                                "&eClick Izquierdo: &aEditar",
                                "&eShift + Click Derecho: &cEliminar"
                        ))
                        .build();

                var meta = item.getItemMeta();
                meta.getPersistentDataContainer().set(INDEX_KEY, PersistentDataType.INTEGER, i);
                item.setItemMeta(meta);

                inventory.setItem(slot++, item);
            } catch (Exception ignored) {}
        }

        // Botón para añadir nuevo (Usa el ítem en la mano)
        inventory.setItem(48, new ItemBuilder(Material.NETHER_STAR)
                .setDisplayName("&a&lAñadir Ítem en Mano")
                .setLore(Arrays.asList("&7Añade el ítem que sostienes", "&7actualmente a la tabla de loot."))
                .build());

        // Volver
        inventory.setItem(49, new ItemBuilder(Material.ARROW).setDisplayName("&aVolver").build());
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack current = event.getCurrentItem();
        LootManager manager = (LootManager) Main.instance.getLootManager();

        if (event.getSlot() == 49) {
            new ConfigMenu().open(player);
            return;
        }

        if (event.getSlot() == 48) {
            ItemStack inHand = player.getInventory().getItemInMainHand();
            if (inHand.getType() == Material.AIR) {
                player.sendMessage(TextUtils.format(Main.prefix + "&cDebes tener un ítem en la mano."));
                return;
            }
            manager.addAbyssLootConfig(inHand.getType(), inHand.getAmount(), 50);
            player.sendMessage(TextUtils.format(Main.prefix + "&aÍtem añadido con valores por defecto (50%)."));
            setMenuItems(player);
            return;
        }

        if (current != null && current.getItemMeta().getPersistentDataContainer().has(INDEX_KEY, PersistentDataType.INTEGER)) {
            int index = current.getItemMeta().getPersistentDataContainer().get(INDEX_KEY, PersistentDataType.INTEGER);

            if (event.isShiftClick() && event.isRightClick()) {
                manager.removeAbyssLoot(index);
                player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_LAVA_EXTINGUISH, 1.0f, 1.0f);
                setMenuItems(player);
            } else {
                new LootEntryEditMenu(index).open(player);
            }
        }
    }
}

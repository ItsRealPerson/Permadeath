package dev.itsrealperson.permadeath.util.gui;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.util.LootManager;
import dev.itsrealperson.permadeath.util.TextUtils;
import dev.itsrealperson.permadeath.util.lib.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * Menú para editar un ítem de loot específico.
 */
public class LootEntryEditMenu extends AbstractMenu {

    private final int index;
    private int maxAmount;
    private int chance;
    private Material material;

    public LootEntryEditMenu(int index) {
        super(27, TextUtils.format("&8PDC Editor: Editando Loot"));
        this.index = index;
        loadData();
    }

    private void loadData() {
        LootManager manager = (LootManager) Main.instance.getLootManager();
        String entry = manager.getAbyssLootEntries().get(index);
        String[] split = entry.split(";");
        this.material = Material.valueOf(split[0]);
        this.maxAmount = Integer.parseInt(split[1]);
        this.chance = Integer.parseInt(split[2]);
    }

    @Override
    public void setMenuItems(Player player) {
        inventory.clear();

        // Icono Central
        inventory.setItem(4, new ItemBuilder(material)
                .setDisplayName("&b&l" + material.name())
                .setLore(Arrays.asList("&7Ítem siendo editado."))
                .build());

        // 1. Cantidad Máxima
        inventory.setItem(11, new ItemBuilder(Material.CHEST_MINECART)
                .setDisplayName("&e&lCantidad Máxima: &f" + maxAmount)
                .setLore(Arrays.asList("&7Aumenta o disminuye la cantidad", "&7máxima que puede aparecer.", " ", "&eClick Izquierdo: &a+1", "&eClick Derecho: &c-1"))
                .build());

        // 2. Probabilidad
        inventory.setItem(15, new ItemBuilder(Material.ENDER_EYE)
                .setDisplayName("&6&lProbabilidad: &f" + chance + "%")
                .setLore(Arrays.asList("&7Probabilidad de aparición (0-100).", " ", "&eClick Izquierdo: &a+5%", "&eClick Derecho: &c-5%"))
                .build());

        // Volver / Guardar
        inventory.setItem(22, new ItemBuilder(Material.ARROW).setDisplayName("&aGuardar y Volver").build());
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        LootManager manager = (LootManager) Main.instance.getLootManager();

        switch (slot) {
            case 11 -> {
                if (event.isLeftClick()) maxAmount = Math.min(64, maxAmount + 1);
                else if (event.isRightClick()) maxAmount = Math.max(1, maxAmount - 1);
                setMenuItems(player);
            }
            case 15 -> {
                if (event.isLeftClick()) chance = Math.min(100, chance + 5);
                else if (event.isRightClick()) chance = Math.max(0, chance - 5);
                setMenuItems(player);
            }
            case 22 -> {
                manager.updateAbyssLoot(index, maxAmount, chance);
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                new LootEditorMenu().open(player);
            }
        }
    }
}

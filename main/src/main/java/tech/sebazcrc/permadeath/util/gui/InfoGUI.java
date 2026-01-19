package tech.sebazcrc.permadeath.util.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.sebazcrc.permadeath.Main;
import tech.sebazcrc.permadeath.api.PermadeathAPI;
import tech.sebazcrc.permadeath.util.TextUtils;
import tech.sebazcrc.permadeath.util.lib.ItemBuilder;

import java.util.ArrayList;
import java.util.List;

public class InfoGUI {

    public static void open(Player player) {
        long day = PermadeathAPI.getDay();
        Inventory inv = Bukkit.createInventory(null, 27, TextUtils.formatComponent("&8Estado de Permadeath - Día " + day));

        // Fondo
        ItemStack glass = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build();
        for (int i = 0; i < 27; i++) inv.setItem(i, glass);

        // Ítem de Día Actual
        List<String> worldInfo = new ArrayList<>();
        worldInfo.add("&7Estado general del mundo.");
        worldInfo.add(" ");
        if (day >= 20) worldInfo.add("&e✔ Mobs pacíficos son hostiles.");
        if (day >= 30) worldInfo.add("&e✔ El End está habilitado.");
        if (day >= 40) worldInfo.add("&e✔ PVP habilitado.");
        if (day >= 50) worldInfo.add("&e✔ Modo UHC (Sin reg. natural).");
        if (day >= 60) worldInfo.add("&e✔ El evento Life Orb ha comenzado.");
        if (PermadeathAPI.isExtended()) worldInfo.add("&d✔ Dificultad Extendida: &fActiva (Día 90)");

        inv.setItem(10, new ItemBuilder(Material.CLOCK)
                .setDisplayName("&b&lDía Actual: &f" + day)
                .setLore(worldInfo)
                .build());

        // Ítem de Bloqueos
        List<String> slotInfo = new ArrayList<>();
        if (day < 40) {
            slotInfo.add("&aTodos los slots están disponibles.");
        } else if (day < 60) {
            slotInfo.add("&c5 slots bloqueados (Reliquia del Fin necesaria).");
        } else {
            slotInfo.add("&cMúltiples slots bloqueados.");
            slotInfo.add("&7Requiere Reliquia del Comienzo.");
        }
        
        inv.setItem(13, new ItemBuilder(Material.STRUCTURE_VOID)
                .setDisplayName("&c&lRestricciones de Inventario")
                .setLore(slotInfo)
                .build());

        // Ítem de Mundos
        List<String> dimensionInfo = new ArrayList<>();
        dimensionInfo.add("&7Dimensiones especiales:");
        dimensionInfo.add("&8- &7The Beginning: " + (day >= 40 ? "&aHabilitado" : "&cBloqueado"));
        dimensionInfo.add("&8- &7The Abyss: " + (PermadeathAPI.isExtended() ? "&aHabilitado" : "&cBloqueado"));
        
        inv.setItem(16, new ItemBuilder(Material.END_PORTAL_FRAME)
                .setDisplayName("&d&lDimensiones")
                .setLore(dimensionInfo)
                .build());

        player.openInventory(inv);
    }
}

package dev.itsrealperson.permadeath.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import dev.itsrealperson.permadeath.util.TextUtils;

public class AdvancementManager {

    public enum PDA {
        SURVIVOR_60("&6Sobreviviente Legendario", "&7Llega al Día 60 con vida."),
        VOID_EXPLORER("&3Explorador del Vacío", "&7Entra a la dimensión del Abismo."),
        ABYSS_HEART("&5El Corazón de las Tinieblas", "&7Despierta el Corazón del Abismo.");

        private final String title;
        private final String description;

        PDA(String title, String description) {
            this.title = title;
            this.description = description;
        }
    }

    public static void grantAdvancement(Player player, PDA adv) {
        String key = "pdc_adv_" + adv.name().toLowerCase();
        if (player.getPersistentDataContainer().has(new org.bukkit.NamespacedKey("permadeath", key), org.bukkit.persistence.PersistentDataType.BYTE)) {
            return;
        }

        player.getPersistentDataContainer().set(new org.bukkit.NamespacedKey("permadeath", key), org.bukkit.persistence.PersistentDataType.BYTE, (byte) 1);
        
        // Simular Toast
        player.sendMessage("");
        player.sendMessage(TextUtils.format("&a&l¡Logro de Permadeath Obtenido!"));
        player.sendMessage(TextUtils.format("&e&l" + adv.title));
        player.sendMessage(TextUtils.format("&f" + adv.description));
        player.sendMessage("");
        
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
        Bukkit.broadcastMessage(TextUtils.format("&7" + player.getName() + " ha completado el desafío: [" + adv.title + "&7]"));
    }
}

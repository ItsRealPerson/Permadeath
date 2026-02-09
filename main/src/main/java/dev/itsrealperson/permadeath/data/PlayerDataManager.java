package dev.itsrealperson.permadeath.data;

import dev.itsrealperson.permadeath.api.Language;
import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.api.storage.PlayerData;
import dev.itsrealperson.permadeath.util.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

/**
 * Wrapper para gestionar los datos de un jugador delegando en el sistema de almacenamiento.
 */
public class PlayerDataManager {

    private final String name;
    private final Main instance;
    private PlayerData data;

    public PlayerDataManager(String playerName, Main instance) {
        this.name = playerName;
        this.instance = instance;
        this.data = instance.getPlayerStorage().loadPlayer(playerName)
                .orElseGet(() -> PlayerData.createDefault(playerName));
        
        // Sincronizar UUID si es posible
        if (Bukkit.getPlayer(playerName) != null && data.getUuid() == null) {
            data.setUuid(Bukkit.getPlayer(playerName).getUniqueId());
            save();
        }
    }

    public Language getLanguage() {
        return data.getLanguage();
    }

    public void setLanguage(Language language) {
        data.setLanguage(language);
        save();
    }

    public void generateDayData() {
        if (data.getLastDay() > 0) return;
        setLastDay(instance.getDay());
    }

    public void setLastDay(long days) {
        data.setLastDay(days);
        save();
    }

    public long getLastDay() {
        generateDayData();
        return data.getLastDay();
    }

    public void setExtraHP(int hp) {
        data.setExtraHP(hp);
        save();
    }

    public int getExtraHP() {
        return data.getExtraHP();
    }

    public void setDeathDay() {
        java.time.LocalDate fechaActual = java.time.LocalDate.now();
        data.setBanDay(fechaActual.toString());
        save();
    }

    public void setDeathTime() {
        java.time.LocalTime fechaActual = java.time.LocalTime.now();
        data.setBanTime(String.format("%02d:%02d:%02d", fechaActual.getHour(), fechaActual.getMinute(), fechaActual.getSecond()));
        save();
    }

    public void setAutoDeathCause(EntityDamageEvent.DamageCause lastDamage) {
        String s = switch (lastDamage) {
            case WITHER -> "&0Efecto Wither";
            case BLOCK_EXPLOSION, ENTITY_EXPLOSION -> "Explosión";
            case DRAGON_BREATH -> "&dEnder Dragon (Breath)";
            case ENTITY_ATTACK -> "Mobs";
            case DROWNING -> "Ahogamiento";
            case FALL -> "Caída";
            case FIRE, FIRE_TICK -> "Fuego";
            case HOT_FLOOR, LAVA -> "Lava";
            case LIGHTNING -> "Trueno";
            case POISON -> "Veneno";
            case VOID -> "Vacío";
            case SUFFOCATION -> "Sofocado";
            case SUICIDE -> "Suicidio";
            case THORNS -> "Espinas";
            case PROJECTILE -> "Proyectil";
            default -> "Causa desconocida.";
        };
        setBanCause(s);
    }

    public void setBanDay(String banDay) {
        data.setBanDay(banDay);
        save();
    }

    public void setDeathCoords(org.bukkit.Location where) {
        data.setCoords((int) where.getX() + " " + (int) where.getY() + " " + (int) where.getZ());
        save();
    }

    public void setBanTime(String banTime) {
        data.setBanTime(banTime);
        save();
    }

    public void setBanCause(String banCause) {
        data.setBanCause(banCause);
        save();
    }

    private void save() {
        // Guardado asíncrono delegado al sistema de almacenamiento
        instance.getPlayerStorage().savePlayer(data);
    }

    public String getName() { return name; }
    public String getBanDay() { return data.getBanDay(); }
    public String getBanTime() { return data.getBanTime(); }
    public String getBanCause() { return data.getBanCause(); }

    public ItemStack craftHead() {
        ItemStack s = new ItemStack(Material.PLAYER_HEAD);
        return craftHead(s);
    }

    public ItemStack craftHead(ItemStack s) {
        ItemMeta meta = s.getItemMeta();
        meta.setDisplayName(TextUtils.format("&c&l" + name));
        meta.setLore(Arrays.asList(TextUtils.format("&cHA SIDO PERMABANEADO"), TextUtils.format(" "), 
                TextUtils.format("&7Fecha del Baneo: &c" + getBanDay()), 
                TextUtils.format("&7Hora del Baneo: &c" + getBanTime()), 
                TextUtils.format("&7Causa del Baneo: " + getBanCause())));
        s.setItemMeta(meta);
        return s;
    }
}
package tech.sebazcrc.permadeath.data;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tech.sebazcrc.permadeath.Main;
import tech.sebazcrc.permadeath.Language;
import tech.sebazcrc.permadeath.util.TextUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

public class PlayerDataManager {

    private String name;
    private String banDay;
    private String banTime;
    private String banCause;
    private String coords;
    private File playersFile;
    private FileConfiguration config;
    private Main instance;

    public PlayerDataManager(String playerName, Main instance) {
        this.name = playerName;
        this.instance = instance;
        this.playersFile = new File(instance.getDataFolder(), "jugadores.yml");
        this.config = YamlConfiguration.loadConfiguration(playersFile);

        if (!playersFile.exists()) {
            try {
                playersFile.createNewFile();
            } catch (IOException e) {
                System.out.println("[ERROR] Ha ocurrido un error al crear el archivo 'jugadores.yml'");
            }
        }

        if (config.contains("Players." + playerName)) {
            this.banDay = config.getString("Players." + playerName + ".banDay");
            this.banTime = config.getString("Players." + playerName + ".banTime");
            this.banCause = config.getString("Players." + playerName + ".banCause");
            this.coords = config.getString("Players." + playerName + ".coords");
        } else {
            this.banTime = "";
            this.banDay = "";
            this.banCause = "";
            this.coords = "";
        }

        if (Bukkit.getPlayer(playerName) != null) {
            addDefault("Players." + getName() + ".UUID", Bukkit.getPlayer(playerName).getUniqueId().toString());
        }

        if (!config.contains("Players." + getName() + ".HP")) {
            config.set("Players." + getName() + ".HP", 0);
        }

        saveFile();
    }

    private void addDefault(String path, Object value) {
        if (!config.contains(path)) {
            config.set(path, value);
        } else {
            if (path.equalsIgnoreCase("Players." + getName() + ".Idioma")) {
                String idioma = config.getString("Players." + getName() + ".Idioma");
                if (!idioma.equalsIgnoreCase("SPANISH") && !idioma.equalsIgnoreCase("ENGLISH")) {
                    config.set("Players." + getName() + ".Idioma", "SPANISH");
                    saveFile();
                }
            }
        }
    }

    public Language getLanguage() {
        addDefault("Players." + getName() + ".Idioma", "SPANISH");
        return Language.valueOf(config.getString("Players." + getName() + ".Idioma"));
    }

    public void setLanguage(Language language) {
        config.set("Players." + name + ".Idioma", language.toString());
        saveFile();
    }

    public void generateDayData() {
        long days = instance.getDay();
        if (config.contains("Players." + name + ".LastDay")) return;
        setLastDay(days);
    }

    public void setLastDay(long days) {
        config.set("Players." + name + ".LastDay", days);
        saveFile();
    }

    public long getLastDay() {
        generateDayData();
        return config.getLong("Players." + name + ".LastDay");
    }

    public void setExtraHP(int hp) {
        config.set("Players." + getName() + ".HP", hp);
        saveFile();
    }

    public int getExtraHP() {
        return config.getInt("Players." + getName() + ".HP");
    }

    public void setDeathDay() {
        LocalDate fechaActual = LocalDate.now();
        int month = fechaActual.getMonthValue();
        int day = fechaActual.getDayOfMonth();
        String s = fechaActual.getYear() + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day);
        setBanDay(s);
    }

    public void setDeathTime() {
        LocalDateTime fechaActual = LocalDateTime.now();
        String s = String.format("%02d:%02d:%02d", fechaActual.getHour(), fechaActual.getMinute(), fechaActual.getSecond());
        setBanTime(s);
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
        this.banDay = banDay;
        config.set("Players." + getName() + ".banDay", banDay);
    }

    public void setDeathCoords(org.bukkit.Location where) {
        String s = (int) where.getX() + " " + (int) where.getY() + " " + (int) where.getZ();
        this.coords = s;
        config.set("Players." + getName() + ".coords", s);
    }

    public void setBanTime(String banTime) {
        this.banTime = banTime;
        config.set("Players." + getName() + ".banTime", banTime);
    }

    public void setBanCause(String banCause) {
        this.banCause = banCause;
        config.set("Players." + getName() + ".banCause", banCause);
    }

    public void saveFile() {
        // Clonar la configuración para evitar ConcurrentModificationException
        final String data;
        synchronized (this.config) {
            data = this.config.saveToString();
        }
        
        final File fileToSave = this.playersFile;
        Runnable task = () -> {
            try {
                java.nio.file.Files.writeString(fileToSave.toPath(), data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        if (Main.isRunningFolia()) {
            Bukkit.getAsyncScheduler().runNow(instance, t -> task.run());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(instance, task);
        }
    }

    public String getName() { return name; }
    public String getBanDay() { return banDay; }
    public String getBanTime() { return banTime; }
    public String getBanCause() { return banCause; }

    public ItemStack craftHead() {
        ItemStack s = new ItemStack(Material.PLAYER_HEAD);
        return craftHead(s);
    }

    public ItemStack craftHead(ItemStack s) {
        ItemMeta meta = s.getItemMeta();
        meta.setDisplayName(TextUtils.format("&c&l" + name));
        meta.setLore(Arrays.asList(TextUtils.format("&c&lHA SIDO PERMABANEADO"), TextUtils.format(" "), TextUtils.format("&7Fecha del Baneo: &c" + banDay), TextUtils.format("&7Hora del Baneo: &c" + banTime), TextUtils.format("&7Causa del Baneo: " + banCause)));
        s.setItemMeta(meta);
        return s;
    }
}
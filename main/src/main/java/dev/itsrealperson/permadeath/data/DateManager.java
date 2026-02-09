package dev.itsrealperson.permadeath.data;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.util.log.PDCLog;
import dev.itsrealperson.permadeath.util.TextUtils;
import dev.itsrealperson.permadeath.discord.DiscordPortal;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class DateManager {

    private final Main instance;
    private static DateManager dai;

    private File f;
    private FileConfiguration c;

    public String date;
    public LocalDate startDate;
    public LocalDate currentDate;

    // Milestones
    private int beginningDay;
    private int beginningAccessDay;
    private int abyssDay;
    private int acidWaterDay;
    private int netherPigZombieDay;
    private int miningFatigueDay;
    private int weatherEffectsDay;
    private int witherSpawnDay;
    private int phantomBombingDay;

    public DateManager() {
        this.instance = Main.getInstance();

        this.currentDate = LocalDate.now();
        this.prepareFile();
        this.date = c.getString("Fecha");

        loadMilestones();

        try {
            this.startDate = LocalDate.parse(date);
        } catch (DateTimeParseException ex) {
            // ... (resto del código)

            Bukkit.getConsoleSender().sendMessage(TextUtils.format(Main.prefix + "&4&lERROR: &eLa fecha en config.yml estaba mal configurada &7(" + c.getString("Fecha") + ")&e."));
            Bukkit.getConsoleSender().sendMessage(TextUtils.format(Main.prefix + "&eSe ha establecido el día: &b1"));
            this.startDate = LocalDate.parse(getDateForDayOne());

            c.set("Fecha", getDateForDayOne());
            saveFile();
            reloadFile();
        }
    }

    public void tick() {

        if (Main.PANIC_MODE) return; // Congelar tiempo

        LocalDate now = LocalDate.now();

        if (this.currentDate.isBefore(now)) {
            long oldDay = getDay();
            this.currentDate = now;
            long newDay = getDay();
            
            // Disparar evento API
            Bukkit.getPluginManager().callEvent(new dev.itsrealperson.permadeath.api.event.PermadeathDayChangeEvent(oldDay, newDay));
            
            DiscordPortal.onDayChange();
            
            // Efecto de sonido épico para todos
            Bukkit.getOnlinePlayers().forEach(p -> {
                p.playSound(p.getLocation(), Sound.EVENT_RAID_HORN, 1.0f, 0.8f);
                p.sendMessage(TextUtils.format(Main.prefix + "&eEl mundo ha envejecido... el día &b" + getDay() + " &eha comenzado."));
            });
        }
    }

    public void reloadDate() {
        this.date = this.c.getString("Fecha");
        this.startDate = LocalDate.parse(this.date);
        this.currentDate = LocalDate.now();
        loadMilestones();
    }

    private void loadMilestones() {
        FileConfiguration config = instance.getConfig();
        this.beginningDay = config.getInt("Progression.BeginningStartDay", 40);
        this.beginningAccessDay = config.getInt("Progression.BeginningAccessDay", 50);
        this.abyssDay = config.getInt("Progression.AbyssStartDay", 60);
        this.acidWaterDay = config.getInt("Progression.AcidWaterStartDay", 30);
        this.netherPigZombieDay = config.getInt("Progression.NetherPigZombieStartDay", 50);
        this.miningFatigueDay = config.getInt("Progression.MiningFatigueStartDay", 50);
        this.weatherEffectsDay = config.getInt("Progression.WeatherEffectsStartDay", 40);
        this.witherSpawnDay = config.getInt("Progression.WitherSpawnStartDay", 60);
        this.phantomBombingDay = config.getInt("Progression.PhantomBombingStartDay", 70);
    }

    public int getBeginningDay() { return beginningDay; }
    public int getBeginningAccessDay() { return beginningAccessDay; }
    public int getAbyssDay() { return abyssDay; }
    public int getAcidWaterDay() { return acidWaterDay; }
    public int getNetherPigZombieDay() { return netherPigZombieDay; }
    public int getMiningFatigueDay() { return miningFatigueDay; }
    public int getWeatherEffectsDay() { return weatherEffectsDay; }
    public int getWitherSpawnDay() { return witherSpawnDay; }
    public int getPhantomBombingDay() { return phantomBombingDay; }

    public void setDay(CommandSender sender, String args1) {
        int nD;

        try {
            int d = Integer.parseInt(args1);
            if (d > 120 || d < 0) {
                nD = 0;
            } else {
                nD = d;
            }
        } catch (NumberFormatException ex) {
            sender.sendMessage(TextUtils.format("&cNecesitas ingresar un número válido."));
            return;
        }
        if (nD == 0) {
            sender.sendMessage(TextUtils.format("&cHas ingresado un número no válido, o ni siquiera un número."));
            return;
        }

        long oldDay = getDay();
        LocalDate add = currentDate.minusDays(nD);
        String dateStr = String.format(add.getYear() + "-%02d-%02d", add.getMonthValue(), add.getDayOfMonth());
        
        this.startDate = add; // ACTUALIZAR EN MEMORIA
        this.date = dateStr;
        setNewDate(dateStr);
        
        long newDay = getDay();
        
        // Disparar evento API
        Bukkit.getPluginManager().callEvent(new dev.itsrealperson.permadeath.api.event.PermadeathDayChangeEvent(oldDay, newDay));

        sender.sendMessage(TextUtils.format("&eSe han actualizado los días a: &7" + nD));
        sender.sendMessage(TextUtils.format("&c&lNota importante: &7Algunos cambios pueden requerir un reinicio y la fecha puede no ser exacta."));

        PDCLog.getInstance().log("Día cambiado a: " + nD);

        Runnable reloadTask = () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pdc reload");
        if (Main.isRunningFolia()) {
            Bukkit.getGlobalRegionScheduler().execute(instance, reloadTask);
        } else {
            reloadTask.run();
        }

        if (Bukkit.getOnlinePlayers() != null && Bukkit.getOnlinePlayers().size() >= 1) {
            for (OfflinePlayer off : Bukkit.getOfflinePlayers()) {

                if (off == null) return;

                if (off.isBanned()) return;

                PlayerDataManager manager = new PlayerDataManager(off.getName(), instance);
                manager.setLastDay(getDay());
            }
        }
    }

    public long getDay() {
        long d;
        if (Main.SPEED_RUN_MODE) {
            d = instance.getPlayTime() / 3600;
        } else {
            d = startDate.until(currentDate, ChronoUnit.DAYS);
        }
        // if (Main.DEBUG) Bukkit.getLogger().info("Current Day: " + d);
        return d;
    }

    public void setNewDate(String value) {
        this.c.set("Fecha", value);
        saveFile();
        reloadFile();
    }

    public void setDayNetwork(long days) {
        LocalDate add = LocalDate.now().minusDays(days);
        setNewDate(String.format(add.getYear() + "-%02d-%02d", add.getMonthValue(), add.getDayOfMonth()));
        
        // El reload disparará el tick() y el evento en el siguiente tick de Bukkit
        // Marcamos que el NetworkListener debe ignorar este cambio para evitar bucles
        dev.itsrealperson.permadeath.event.NetworkListener.IGNORE_NEXT_DAY_CHANGE = true;
        
        Bukkit.broadcastMessage(TextUtils.format(Main.prefix + "&eSincronización de red: El día se ha actualizado a &b" + days));
    }

    public String getDateForDayOne() {
        LocalDate w = currentDate.minusDays(1);

        return String.format(w.getYear() + "-%02d-%02d", w.getMonthValue(), w.getDayOfMonth());
    }

    private void prepareFile() {
        this.f = new File(this.instance.getDataFolder(), "fecha.yml");
        this.c = YamlConfiguration.loadConfiguration(f);

        if (!f.exists()) {

            this.instance.saveResource("fecha.yml", false);

            c.set("Fecha", getDateForDayOne());

            saveFile();
            reloadFile();
        }

        if (c.getString("Fecha").isEmpty()) {

            c.set("Fecha", getDateForDayOne());
            saveFile();
            reloadFile();
        }
    }

    private void saveFile() {
        try {
            this.c.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void reloadFile() {
        try {
            this.c.load(f);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static DateManager getInstance() {
        if (dai == null) dai = new DateManager();
        return dai;
    }
}



















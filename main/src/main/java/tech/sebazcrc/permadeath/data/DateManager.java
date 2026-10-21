package tech.sebazcrc.permadeath.data;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import tech.sebazcrc.permadeath.Main;
import tech.sebazcrc.permadeath.util.log.PDCLog;
import tech.sebazcrc.permadeath.util.TextUtils;
import tech.sebazcrc.permadeath.discord.DiscordPortal;

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

    public DateManager() {
        this.instance = Main.getInstance();

        this.currentDate = LocalDate.now();
        this.prepareFile();
        this.date = c.getString("Fecha");

        try {
            this.startDate = LocalDate.parse(date);
        } catch (DateTimeParseException ex) {

            Bukkit.getConsoleSender().sendMessage(TextUtils.format(Main.prefix + "&4&lERROR: &eLa fecha en config.yml estaba mal configurada &7(" + c.getString("Fecha") + ")&e."));
            Bukkit.getConsoleSender().sendMessage(TextUtils.format(Main.prefix + "&eSe ha establecido el día: &b1"));
            this.startDate = LocalDate.parse(getDateForDayOne());

            c.set("Fecha", getDateForDayOne());
            saveFile();
            reloadFile();
        }
    }

    public void tick() {

        LocalDate now = LocalDate.now();

        if (this.currentDate.isBefore(now)) {
            this.currentDate = now;
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
    }

    public void setDay(CommandSender sender, String args1) {
        int nD;

        try {
            int d = Integer.parseInt(args1);
            if (d > 120 || d < 0) {
                sender.sendMessage(TextUtils.format("&cEl día debe estar entre 0 y 120."));
                return;
            } else {
                nD = d;
            }
        } catch (NumberFormatException ex) {
            sender.sendMessage(TextUtils.format("&cNecesitas ingresar un número válido."));
            return;
        }

        LocalDate add = currentDate.minusDays(nD);
        this.startDate = add;
        DateManager.getInstance().setNewDate(String.format(add.getYear() + "-%02d-%02d", add.getMonthValue(), add.getDayOfMonth()));

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

                if (off == null) continue;

                if (off.isBanned()) continue;

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
            Main.getInstance().getLogger().log(java.util.logging.Level.SEVERE, "Error al guardar fecha.yml", e);
        }
    }

    private void reloadFile() {
        try {
            this.c.load(f);
            this.date = this.c.getString("Fecha");
            if (this.date != null && !this.date.isEmpty()) {
                this.startDate = LocalDate.parse(this.date);
            }
        } catch (IOException | InvalidConfigurationException | DateTimeParseException e) {
            Main.getInstance().getLogger().log(java.util.logging.Level.SEVERE, "Error al recargar fecha.yml", e);
        }
    }

    public static DateManager getInstance() {
        if (dai == null) dai = new DateManager();
        return dai;
    }
}



















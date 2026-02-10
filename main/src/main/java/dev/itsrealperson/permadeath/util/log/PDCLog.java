package dev.itsrealperson.permadeath.util.log;

import org.bukkit.Bukkit;
import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.util.TextUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PDCLog {

    private static PDCLog logs;
    private Main instance;

    private File file;

    public PDCLog() {
        this.instance = Main.getInstance();
        this.file = new File(instance.getDataFolder(), "logs.txt");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void disable(String reason) {
        log("El plugin ha sido apagado: " + reason);
    }

    public void log(String log) {
        log(log, false);
    }

    public void log(String log, boolean print) {
        LocalDate date = LocalDate.now();
        LocalDateTime time = LocalDateTime.now();
        String message = String.format("[%02d/%02d/%02d] ", date.getDayOfMonth(), date.getMonthValue(), date.getYear()) + String.format("%02d:%02d:%02d ", time.getHour(), time.getMinute(), time.getSecond()) + log;

        add(message + "\n");
        if (print) {
            Bukkit.getConsoleSender().sendMessage(TextUtils.format(Main.prefix + "&7[LOG] " + message));
        }
    }

    public void printRecentLogs(org.bukkit.command.CommandSender sender, int lines) {
        if (!file.exists()) {
            sender.sendMessage(TextUtils.format(Main.prefix + "&cNo hay logs registrados todavía."));
            return;
        }

        try {
            java.util.List<String> allLines = java.nio.file.Files.readAllLines(file.toPath());
            int start = Math.max(0, allLines.size() - lines);
            
            sender.sendMessage(TextUtils.format("&8&m------------------------------------------"));
            sender.sendMessage(TextUtils.format("         &e&lÚLTIMOS LOGS PDC"));
            for (int i = start; i < allLines.size(); i++) {
                sender.sendMessage(TextUtils.format("&7> " + allLines.get(i)));
            }
            sender.sendMessage(TextUtils.format("&8&m------------------------------------------"));
        } catch (IOException e) {
            sender.sendMessage(TextUtils.format(Main.prefix + "&cError al leer los logs: " + e.getMessage()));
        }
    }

    private void add(String msg) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            bw.append(msg);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PDCLog getInstance() {
        if (logs == null) logs = new PDCLog();
        return logs;
    }
}


















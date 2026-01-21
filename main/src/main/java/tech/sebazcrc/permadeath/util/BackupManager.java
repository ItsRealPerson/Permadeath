package tech.sebazcrc.permadeath.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import tech.sebazcrc.permadeath.Main;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class BackupManager {

    private final Main plugin;

    public BackupManager(Main plugin) {
        this.plugin = plugin;
    }

    public void createBackup(CommandSender sender) {
        sender.sendMessage(Main.prefix + ChatColor.YELLOW + "Iniciando backup del servidor... (Guardando mundos)");

        // 1. Forzar guardado de mundos
        if (Main.isRunningFolia()) {
            Bukkit.getGlobalRegionScheduler().execute(plugin, () -> {
                saveAllWorlds();
                startAsyncBackup(sender);
            });
        } else {
            saveAllWorlds();
            startAsyncBackup(sender);
        }
    }

    private void saveAllWorlds() {
        for (World world : Bukkit.getWorlds()) {
            world.save();
        }
    }

    private void startAsyncBackup(CommandSender sender) {
        CompletableFuture.runAsync(() -> {
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            File backupDir = new File(plugin.getDataFolder(), "backups");
            if (!backupDir.exists()) backupDir.mkdirs();

            File zipFile = new File(backupDir, "PDC_Backup_" + timeStamp + ".zip");

            try (FileOutputStream fos = new FileOutputStream(zipFile);
                 ZipOutputStream zos = new ZipOutputStream(fos)) {

                for (World world : Bukkit.getWorlds()) {
                    File worldFolder = world.getWorldFolder();
                    zipDirectory(worldFolder, worldFolder.getName(), zos);
                }

                sender.sendMessage(Main.prefix + ChatColor.GREEN + "Backup completado con éxito: " + ChatColor.GRAY + zipFile.getName());
            } catch (IOException e) {
                sender.sendMessage(Main.prefix + ChatColor.RED + "Error al crear el backup: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void zipDirectory(File folder, String parentFolder, ZipOutputStream zos) throws IOException {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                // Ignorar carpetas temporales o de sesión si es necesario
                if (file.getName().equals("session.lock")) continue;
                zipDirectory(file, parentFolder + "/" + file.getName(), zos);
                continue;
            }

            if (file.getName().equals("session.lock")) continue;

            try (FileInputStream fis = new FileInputStream(file)) {
                ZipEntry zipEntry = new ZipEntry(parentFolder + "/" + file.getName());
                zos.putNextEntry(zipEntry);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) >= 0) {
                    zos.write(buffer, 0, length);
                }
                zos.closeEntry();
            } catch (IOException e) {
                // Algunos archivos pueden estar bloqueados por el sistema, los saltamos
            }
        }
    }
}

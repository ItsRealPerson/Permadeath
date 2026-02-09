package dev.itsrealperson.permadeath.util;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.data.DateManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Comparator;

public class ResetManager {

    private final Main plugin;

    public ResetManager(Main plugin) {
        this.plugin = plugin;
    }

    public void resetAll(CommandSender sender) {
        sender.sendMessage(Main.prefix + ChatColor.YELLOW + "Iniciando reinicio total del servidor...");

        // 1. Desbanear a todos
        Bukkit.getBanList(BanList.Type.NAME).getEntries().forEach(entry -> {
            Bukkit.getBanList(BanList.Type.NAME).pardon(entry.getTarget());
        });
        sender.sendMessage(Main.prefix + ChatColor.GREEN + "Todos los jugadores han sido desbaneados.");

        // 2. Resetear fecha al día 1
        DateManager.getInstance().setNewDate(DateManager.getInstance().getDateForDayOne());
        sender.sendMessage(Main.prefix + ChatColor.GREEN + "El contador de días ha vuelto al Día 1.");

        // 3. Borrar datos de jugadores
        File playersFile = new File(plugin.getDataFolder(), "jugadores.yml");
        if (playersFile.exists()) {
            playersFile.delete();
            sender.sendMessage(Main.prefix + ChatColor.GREEN + "Se han borrado los datos de jugadores (jugadores.yml).");
        }

        // 4. Resetear mundos custom (opcionalmente)
        resetCustomWorlds(sender);

        sender.sendMessage(Main.prefix + ChatColor.GOLD + "Reinicio completado. Se recomienda reiniciar el servidor.");
    }

    private void resetCustomWorlds(CommandSender sender) {
        String[] customWorlds = {"pdc_the_beginning", "pdc_the_abyss", "world_permadeath_beginning", "world_permadeath_abyss"};
        for (String worldName : customWorlds) {
            World world = Bukkit.getWorld(worldName);
            if (world != null) {
                if (!Main.isRunningFolia()) {
                    sender.sendMessage(Main.prefix + ChatColor.YELLOW + "Descargando " + worldName + " para intentar borrarlo...");
                    Bukkit.unloadWorld(world, false);
                } else {
                    sender.sendMessage(Main.prefix + ChatColor.YELLOW + "Marcando " + worldName + " para regeneración tras reinicio...");
                    // En Folia no podemos descargar el mundo dinámicamente de forma segura aún
                }
            }
            
            File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
            if (worldFolder.exists()) {
                if (deleteDirectory(worldFolder)) {
                    sender.sendMessage(Main.prefix + ChatColor.GREEN + "Mundo borrado exitosamente: " + worldName);
                } else {
                    sender.sendMessage(Main.prefix + ChatColor.RED + "No se pudo borrar " + worldName + " completamente (archivos en uso). Se borrará al reiniciar el servidor.");
                    worldFolder.deleteOnExit();
                }
            }
        }
        sender.sendMessage(Main.prefix + ChatColor.GOLD + "NOTA: El Overworld no se puede borrar automáticamente. Por favor, bórralo manualmente si deseas un mapa nuevo.");
    }

    private boolean deleteDirectory(File directory) {
        try {
            Files.walk(directory.toPath())
                .sorted(Comparator.reverseOrder())
                .map(java.nio.file.Path::toFile)
                .forEach(File::delete);
            return !directory.exists();
        } catch (IOException e) {
            return false;
        }
    }
}

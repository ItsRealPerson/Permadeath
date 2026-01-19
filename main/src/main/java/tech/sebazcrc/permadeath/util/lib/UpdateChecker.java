package tech.sebazcrc.permadeath.util.lib;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Consumer;
import tech.sebazcrc.permadeath.util.Utils;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class UpdateChecker {
    private final Plugin plugin;
    private boolean hasInternetConnection = true;

    public UpdateChecker(Plugin plugin) {
        this.plugin = plugin;
    }

    public void getVersion(final Consumer<String> consumer) {
        Runnable checkTask = () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + Utils.RESOURCE_ID).openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                this.hasInternetConnection = false;
            }
        };

        if (tech.sebazcrc.permadeath.Main.isRunningFolia()) {
            Bukkit.getAsyncScheduler().runNow(this.plugin, t -> checkTask.run());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, checkTask);
        }
    }
}








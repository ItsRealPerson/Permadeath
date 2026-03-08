package dev.itsrealperson.permadeath.pdcentities;

import dev.itsrealperson.permadeath.pdcentities.script.MobManager;
import dev.itsrealperson.permadeath.pdcentities.script.PdcMobTemplate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.Random;

public class NaturalSpawnerTask {

    private final Random random = new Random();

    public void start(org.bukkit.plugin.Plugin plugin) {
        Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, t -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Bukkit.getRegionScheduler().run(plugin, player.getLocation(), task -> {
                    checkSpawnForPlayer(player);
                });
            }
        }, 200L, 200L); // Ejecutar cada 10 segundos
    }

    private void checkSpawnForPlayer(Player player) {
        for (PdcMobTemplate template : MobManager.getTemplates().values()) {
            if (template.getNaturalChance() <= 0) continue;

            double roll = random.nextDouble() * 100.0;
            if (roll <= template.getNaturalChance()) {
                Location loc = player.getLocation().clone().add(
                    random.nextInt(30) - 15, 
                    random.nextInt(6) - 3, 
                    random.nextInt(30) - 15
                );

                if (loc.getBlock().getType().isAir()) {
                    PdcEntitiesAddon.debug("§b[Natural] Intentando spawn de " + template.getId() + " cerca de " + player.getName());
                    MobManager.spawnMob(template.getId(), loc, PdcEntitiesAddon.getApi().getDay());
                }
            }
        }
    }
}

package dev.itsrealperson.permadeath.world;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.api.PermadeathModule;
import dev.itsrealperson.permadeath.data.DateManager;
import dev.itsrealperson.permadeath.util.VersionManager;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.SplittableRandom;

public class EnvironmentModule implements PermadeathModule {

    private final Main plugin;
    private final SplittableRandom random = new SplittableRandom();
    private int tickCounter = 0;

    public EnvironmentModule(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "EnvironmentModule";
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onTick() {
        tickCounter++;
        
        // Ejecutamos la lógica cada segundo (20 ticks)
        if (tickCounter % 20 == 0) {
            long day = DateManager.getInstance().getDay();
            
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getGameMode() == GameMode.SPECTATOR) continue;

                if (Main.isRunningFolia()) {
                    // En Folia, debemos ejecutar el acceso al mundo en el hilo del jugador
                    player.getScheduler().run(plugin, task -> {
                        applyWeatherEffects(player, day);
                        applyMiningFatigueFix(player, day);
                        applyNetherSpawns(player, day);
                    }, null);
                } else {
                    applyWeatherEffects(player, day);
                    applyMiningFatigueFix(player, day);
                    applyNetherSpawns(player, day);
                }
            }
        }
    }

    private void applyWeatherEffects(Player player, long day) {
        DateManager dm = DateManager.getInstance();
        if (day >= dm.getWeatherEffectsDay() && player.getWorld().hasStorm()) {
            Location blockLoc = player.getWorld().getHighestBlockAt(player.getLocation()).getLocation();
            if (blockLoc.getBlockY() < player.getLocation().getY()) {
                
                // Multiplicamos las probabilidades originales por 20 para compensar el tick rate de 1s
                int probability = random.nextInt(10000) + 1;
                int blindChance = (day < 50 ? 20 : 6000); // TODO: Hacer este 50 dinámico si es necesario

                if (probability <= blindChance) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 60, 0));
                }

                if (day >= 50 && (probability >= 6001 && probability <= 6020)) { // Rango equivalente al "301" original
                    int duration = random.nextInt(17) + 3;
                    player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, duration * 20, 0));
                }
            }
        }
    }

    private void applyMiningFatigueFix(Player player, long day) {
        if (day >= DateManager.getInstance().getMiningFatigueDay()) {
            PotionEffect effect = player.getPotionEffect(PotionEffectType.MINING_FATIGUE);
            if (effect != null) {
                if (effect.getDuration() >= 4 * 60 * 20 && !plugin.getDoneEffectPlayers().contains(player)) {
                    player.removePotionEffect(PotionEffectType.MINING_FATIGUE);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 10 * 60 * 20, 2));
                    plugin.getDoneEffectPlayers().add(player);
                }
            } else {
                plugin.getDoneEffectPlayers().remove(player);
            }
        }
    }

    private void applyNetherSpawns(Player player, long day) {
        DateManager dm = DateManager.getInstance();
        if (day >= dm.getNetherPigZombieDay() && day < dm.getAbyssDay() && player.getWorld().getEnvironment() == World.Environment.NETHER) {
            // Probabilidad original 10/4500 por tick -> ~200/4500 por segundo
            if (random.nextInt(4500) < 200 && player.getWorld().getLivingEntities().size() < 110) {
                Location ploc = player.getLocation().clone();
                ArrayList<Location> spawns = new ArrayList<>();
                spawns.add(ploc.clone().add(10, 25, -5));
                spawns.add(ploc.clone().add(5, 25, 5));
                spawns.add(ploc.clone().add(-5, 25, 5));

                for (Location l : spawns) {
                    if (l.getBlock().getType() == Material.AIR && l.clone().add(0, 1, 0).getBlock().getType() == Material.AIR) {
                        int amount = random.nextInt(3) + 1;
                        for (int i = 0; i < amount; i++) {
                            plugin.getNmsHandler().spawnNMSEntity("PigZombie", 
                                EntityType.valueOf(VersionManager.isRunningPostNetherUpdate() ? "ZOMBIFIED_PIGLIN" : "PIG_ZOMBIE"), 
                                l, CreatureSpawnEvent.SpawnReason.CUSTOM);
                        }
                    }
                }
            }
        }
    }
}
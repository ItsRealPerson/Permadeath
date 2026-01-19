package tech.sebazcrc.permadeath.world.abyss;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import tech.sebazcrc.permadeath.Main;

public class AbyssManager implements Listener {

    private static final String WORLD_NAME = "pdc_the_abyss";
    private final Main plugin;
    private World abyssWorld;

    public AbyssManager(Main plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.abyssWorld = Bukkit.getWorld(WORLD_NAME);
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        if (event.getWorld().getName().equalsIgnoreCase(WORLD_NAME)) {
            this.abyssWorld = event.getWorld();
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Permadeath] Dimensión " + WORLD_NAME + " vinculada.");
            
            if (Main.isRunningFolia()) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[Permadeath] AVISO: El soporte de dimensiones en Folia es experimental.");
            }
        }
    }

    public void loadWorld() {
        this.abyssWorld = Bukkit.getWorld(WORLD_NAME);
        if (this.abyssWorld == null && Main.isRunningFolia()) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Permadeath] Folia requiere que '" + WORLD_NAME + "' esté en bukkit.yml para cargar.");
        }
    }

    public void teleportToAbyss(Player player) {
        if (abyssWorld == null) abyssWorld = Bukkit.getWorld(WORLD_NAME);

        if (abyssWorld != null) {
            Location spawn = abyssWorld.getSpawnLocation();
            if (spawn.getY() <= 0) spawn.setY(abyssWorld.getHighestBlockYAt(spawn) + 1);
            
            player.teleportAsync(spawn).thenAccept(success -> {
                if (success) {
                    player.sendMessage(ChatColor.DARK_GRAY + "Has descendido al Abismo Profundo...");
                    player.playSound(player.getLocation(), Sound.BLOCK_SCULK_SHRIEKER_SHRIEK, 1.0f, 0.5f);
                }
            });
        } else {
            player.sendMessage(ChatColor.RED + "El Abismo no está disponible actualmente en este software de servidor (Folia).");
        }
    }

    public World getAbyssWorld() { return abyssWorld; }

    public void onAbyssSpawn(org.bukkit.event.entity.CreatureSpawnEvent event) {
        if (abyssWorld == null || !event.getLocation().getWorld().equals(abyssWorld)) return;
        if (event.getSpawnReason() == org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.CUSTOM) return;

        Location loc = event.getLocation();
        java.util.Random random = new java.util.Random();
        if (random.nextInt(100) < 40) {
            event.setCancelled(true);
            String[] deepDarkMobs = {"PaleParagon", "ArcaneEvoker", "SilentSeeker", "SculkParasite", "EchoArcher", "HollowGuard"};
            String selected = deepDarkMobs[random.nextInt(deepDarkMobs.length)];
            plugin.getNmsHandler().spawnNMSCustomEntity(selected, null, loc, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.CUSTOM);
        }
    }
}

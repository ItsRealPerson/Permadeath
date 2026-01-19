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
        
        if (this.abyssWorld == null) {
            // Intento de carga dinámica para Paper/Spigot
            if (!Main.isRunningFolia()) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Permadeath] Intentando generar dimensión " + WORLD_NAME + "...");
                WorldCreator wc = new WorldCreator(WORLD_NAME);
                wc.environment(World.Environment.THE_END); // Cielo oscuro
                wc.generator(new tech.sebazcrc.permadeath.world.abyss.generator.DeepDarkAbyssGenerator());
                this.abyssWorld = Bukkit.createWorld(wc);
            }
            
            if (this.abyssWorld == null) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Permadeath] La dimensión '" + WORLD_NAME + "' no está cargada.");
                if (Main.isRunningFolia()) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Permadeath] En Folia, asegúrate de añadirla a bukkit.yml.");
                }
            }
        }
    }

    public void teleportToAbyss(Player player) {
        if (!tech.sebazcrc.permadeath.api.PermadeathAPI.isExtended()) {
            player.sendMessage(ChatColor.RED + "El Abismo está sellado. Necesitas despertar el Corazón del Abismo para entrar.");
            player.playSound(player.getLocation(), Sound.BLOCK_CHEST_LOCKED, 1.0f, 1.0f);
            return;
        }

        if (abyssWorld == null) loadWorld(); // Intentar cargar si es null

        if (abyssWorld != null) {
            Location spawn = abyssWorld.getSpawnLocation();
            // Ajuste de altura seguro
            int highestY = abyssWorld.getHighestBlockYAt(spawn.getBlockX(), spawn.getBlockZ());
            if (highestY > -60) spawn.setY(highestY + 1);
            else spawn.setY(0);
            
            if (Main.isRunningFolia()) {
                player.teleportAsync(spawn).thenAccept(success -> {
                    if (success) playAbyssSound(player);
                });
            } else {
                player.teleport(spawn);
                playAbyssSound(player);
            }
        } else {
            player.sendMessage(ChatColor.RED + "Error: La dimensión del Abismo no se ha podido cargar.");
        }
    }

    public void tickAbyssEffects(Player player) {
        if (abyssWorld == null || !player.getWorld().equals(abyssWorld)) return;

        // 1. Niebla de Vacío (Partículas)
        Location loc = player.getLocation();
        player.spawnParticle(Particle.ASH, loc, 50, 8, 4, 8, 0.02);
        player.spawnParticle(Particle.SQUID_INK, loc, 10, 5, 3, 5, 0.01);

        // 2. Sonidos ambientales ocasionales
        if (new java.util.Random().nextInt(40) == 0) {
            Sound[] abyssSounds = {Sound.BLOCK_SCULK_SHRIEKER_SHRIEK, Sound.ENTITY_WARDEN_HEARTBEAT, Sound.AMBIENT_CAVE, Sound.BLOCK_SCULK_CATALYST_BLOOM};
            player.playSound(loc, abyssSounds[new java.util.Random().nextInt(abyssSounds.length)], 0.4f, 0.5f);
        }
    }

    private void playAbyssSound(Player player) {
        player.sendMessage(ChatColor.DARK_GRAY + "Has descendido al Abismo Profundo...");
        player.playSound(player.getLocation(), Sound.BLOCK_SCULK_SHRIEKER_SHRIEK, 1.0f, 0.5f);
    }

    public World getAbyssWorld() { return abyssWorld; }

    public void onAbyssSpawn(org.bukkit.event.entity.CreatureSpawnEvent event) {
        if (abyssWorld == null || !event.getLocation().getWorld().equals(abyssWorld)) return;
        
        // Bloquear todos los spawns naturales que no sean nuestros custom
        if (event.getSpawnReason() != org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.CUSTOM) {
            event.setCancelled(true);
            
            // Reemplazar aleatoriamente algunos spawns naturales con nuestros mobs
            // Reducimos la probabilidad para no saturar, ya que cancelamos todo lo demás
            Location loc = event.getLocation();
            java.util.Random random = new java.util.Random();
            
            if (random.nextInt(100) < 15) { // 15% de probabilidad de reemplazo
                String[] deepDarkMobs = {"SilentSeeker", "SculkParasite", "EchoArcher", "HollowGuard", "TwistedWarden"};
                String selected = deepDarkMobs[random.nextInt(deepDarkMobs.length)];
                
                // Probabilidad extra baja para el jefe
                if (selected.equals("TwistedWarden") && random.nextInt(10) != 0) {
                    selected = "SilentSeeker"; // Reemplazo si no sale el 10% de ese 15% (1.5% total)
                }
                
                plugin.getNmsHandler().spawnNMSCustomEntity(selected, null, loc, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.CUSTOM);
            }
            return;
        }
    }
}










package tech.sebazcrc.permadeath.world.beginning;

import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import tech.sebazcrc.permadeath.Main;
import tech.sebazcrc.permadeath.data.BeginningDataManager;
import tech.sebazcrc.permadeath.util.TextUtils;
import tech.sebazcrc.permadeath.world.WorldEditPortal;
import tech.sebazcrc.permadeath.world.beginning.generator.BeginningLootTable;

import java.util.Objects;

public class BeginningManager implements Listener {

    private static final String WORLD_NAME = "permadeath/beginning";
    private Main main;
    private World beginningWorld;
    private BeginningDataManager data;
    
    @Getter
    private boolean closed = false;

    public BeginningManager(Main main) {
        this.main = main;
        this.data = main.getBeData();
        this.beginningWorld = Bukkit.getWorld(WORLD_NAME);

        main.getServer().getPluginManager().registerEvents(this, main);
        
        if (main.getDay() >= 40) {
            loadWorld();
        }
    }

    @EventHandler
    public void onWorldInit(WorldInitEvent event) {
        if (event.getWorld().getName().endsWith("permadeath_beginning") || event.getWorld().getName().endsWith("permadeath/beginning")) {
            if (event.getWorld().getPopulators().stream().noneMatch(p -> p instanceof BeginningPopulator)) {
                event.getWorld().getPopulators().add(new BeginningPopulator());
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Permadeath] Poblador de The Beginning inyectado.");
            }
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        String name = event.getWorld().getName();
        if (name.endsWith("permadeath_beginning") || name.endsWith("permadeath/beginning")) {
            this.beginningWorld = event.getWorld();
            setupWorldDefaults();
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Permadeath] Dimensión The Beginning vinculada.");
        }
    }

    public void loadWorld() {
        this.beginningWorld = Bukkit.getWorld(WORLD_NAME);
        
        if (this.beginningWorld == null) {
            for (World w : Bukkit.getWorlds()) {
                if (w.getName().endsWith("permadeath_beginning")) {
                    this.beginningWorld = w;
                    break;
                }
            }
        }

        if (this.beginningWorld == null) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Permadeath] Cargando The Beginning...");
            try {
                WorldCreator creator = new WorldCreator(WORLD_NAME);
                creator.environment(World.Environment.THE_END);
                creator.generateStructures(false);
                this.beginningWorld = Bukkit.createWorld(creator);
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Permadeath] Error al cargar The Beginning: " + e.getMessage());
            }
        }
    }

    private void setupWorldDefaults() {
        if (beginningWorld == null) return;
        beginningWorld.setGameRule(GameRule.MOB_GRIEFING, false);
        if (main.getConfig().getBoolean("Toggles.Doble-Mob-Cap")) {
            beginningWorld.setMonsterSpawnLimit(140);
        }
    }

    public void closeBeginning() {
        if (beginningWorld == null) return;
        beginningWorld.getPlayers().forEach(p -> {
            p.teleport(main.world.getSpawnLocation());
            p.playSound(p.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 1.0F, 1.0F);
        });
        Bukkit.broadcastMessage(TextUtils.format(main.prefix + "&eThe Beginning ha cerrado temporalmente (DeathTrain)."));
        this.closed = true;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public World getBeginningWorld() {
        return beginningWorld;
    }

    // --- MANEJO DE CHESTS Y ESTRUCTURAS ---
    @EventHandler
    public void onInteract(org.bukkit.event.player.PlayerInteractEvent e) {
        if (beginningWorld == null || !e.getPlayer().getWorld().equals(beginningWorld)) return;
        if (e.getClickedBlock() != null && e.getClickedBlock().getState() instanceof Chest chest) {
            populateChest(chest);
        }
    }

    private void populateChest(Chest chest) {
        if (!data.hasPopulatedChest(chest.getLocation())) {
            if (main.getDay() < 60) {
                new BeginningLootTable(this).populateChest(chest);
            }
            data.addPopulatedChest(chest.getLocation());
        }
    }

    public void generatePortal(boolean overworld, Location location) {
        WorldEditPortal.generatePortal(overworld, location);
    }
}
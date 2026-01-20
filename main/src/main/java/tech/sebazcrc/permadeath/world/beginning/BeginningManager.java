package tech.sebazcrc.permadeath.world.beginning;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import tech.sebazcrc.permadeath.Main;
import tech.sebazcrc.permadeath.data.BeginningDataManager;
import tech.sebazcrc.permadeath.util.TextUtils;
import tech.sebazcrc.permadeath.world.WorldEditPortal;
import tech.sebazcrc.permadeath.world.beginning.generator.BeginningLootTable;

public class BeginningManager implements Listener {

    private static final String WORLD_NAME = "permadeath/beginning";
    private Main main;
    private World beginningWorld;
    private BeginningDataManager data;
    private boolean closed = false;

    public boolean isClosed() {
        return closed;
    }

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

    @EventHandler
    public void onPortal(PlayerPortalEvent e) {
        if (beginningWorld == null) return;
        if (e.getCause() != PlayerTeleportEvent.TeleportCause.END_GATEWAY) return;

        Player p = e.getPlayer();
        e.setCancelled(true); // Cancelar teletransporte Vanilla de Gateway

        if (isClosed()) {
            p.sendMessage(ChatColor.RED + "The Beginning está cerrado actualmente.");
            return;
        }

        if (main.getDay() < 50) {
            if (p.getWorld().equals(main.world) || p.getWorld().equals(beginningWorld)) {
                p.setNoDamageTicks(p.getMaximumNoDamageTicks());
                p.damage(p.getHealth() + 1.0D);
                Bukkit.broadcastMessage(TextUtils.format("&c&lEl jugador &4&l" + p.getName() + " &c&lentró a The Beginning antes de tiempo."));
            }
            return;
        }

        // Teleport al Beginning
        if (p.getWorld().equals(main.world)) {
            Location to = beginningWorld.getSpawnLocation();
            if (Main.isRunningFolia()) {
                p.teleportAsync(to).thenAccept(success -> {
                    if (success) p.sendMessage(TextUtils.format("&eBienvenido a &b&lThe Beginning&e."));
                });
            } else {
                p.teleport(to);
                p.sendMessage(TextUtils.format("&eBienvenido a &b&lThe Beginning&e."));
            }
            return;
        }

        // Teleport de vuelta al Overworld
        if (p.getWorld().equals(beginningWorld)) {
            Location to = main.world.getSpawnLocation();
            if (Main.isRunningFolia()) {
                p.teleportAsync(to);
            } else {
                p.teleport(to);
            }
        }
    }

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

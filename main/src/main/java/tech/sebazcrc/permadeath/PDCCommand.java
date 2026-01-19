package tech.sebazcrc.permadeath;

import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tech.sebazcrc.permadeath.util.NMS;
import tech.sebazcrc.permadeath.util.item.InfernalNetherite;
import tech.sebazcrc.permadeath.util.item.NetheriteArmor;
import tech.sebazcrc.permadeath.util.item.PermadeathItems;
import tech.sebazcrc.permadeath.util.lib.ItemBuilder;
import tech.sebazcrc.permadeath.data.DateManager;
import tech.sebazcrc.permadeath.data.PlayerDataManager;
import tech.sebazcrc.permadeath.util.TextUtils;
import tech.sebazcrc.permadeath.discord.DiscordPortal;
import tech.sebazcrc.permadeath.util.VersionManager;
import tech.sebazcrc.permadeath.world.beginning.generator.EmptyGenerator;

import java.time.LocalTime;
import java.util.Random;
import java.util.stream.Collectors;

public class PDCCommand implements CommandExecutor {

    private final Main instance;
    private final Random random = new Random();

    public PDCCommand(Main instance) {
        this.instance = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "awake" -> handleAwake(sender);
            case "duracion" -> handleDuracion(sender);
            case "idioma" -> handleIdioma(sender, args);
            case "cambiardia" -> handleCambiarDia(sender, args);
            case "setupbeginning" -> handleSetupBeginning(sender);
            case "reload" -> handleReload(sender);
            case "debug" -> handleDebug(sender, args);
            case "spawn" -> handleSpawn(sender, args);
            case "mensaje" -> handleMensaje(sender, args);
            case "dias" -> handleDias(sender);
            case "info" -> handleInfo(sender);
            case "discord" -> sender.sendMessage(instance.prefix + ChatColor.BLUE + "https://discord.gg/w58wzrcJU8 | https://discord.gg/infernalcore");
            case "cambios" -> handleCambios(sender);
            case "beginning" -> handleBeginning(sender, args);
            case "boss" -> handleBoss(sender, args);
            case "speedrun" -> handleSpeedRun(sender, args);
            case "event" -> handleEvent(sender, args);
            case "locate" -> handleLocate(sender, args);
            case "give" -> handleGive(sender, args);
            case "afk" -> handleAFK(sender, args);
            case "storm" -> handleStorm(sender, args);
            case "accesorios" -> handleAccesorios(sender);
            case "abyss" -> handleAbyss(sender, args);
            default -> sendHelp(sender);
        }

        return true;
    }

    private void handleAbyss(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) return;
        
        if (args.length > 1 && args[1].equalsIgnoreCase("force") && p.isOp()) {
            p.sendMessage(ChatColor.YELLOW + "Forzando carga del Abismo...");
            instance.getAbyssManager().loadWorld();
            return;
        }
        
        instance.getAbyssManager().teleportToAbyss(p);
    }

    private void handleAccesorios(CommandSender sender) {
        if (!(sender instanceof Player p)) return;
        tech.sebazcrc.permadeath.util.inventory.AccessoryInventory.open(p);
    }

    private void handleAwake(CommandSender sender) {
        if (!(sender instanceof Player p)) return;
        int timeAwake = p.getStatistic(Statistic.TIME_SINCE_REST) / 20;
        long days = timeAwake / 86400;
        String time = LocalTime.ofSecondOfDay(timeAwake % 86400).toString();
        sender.sendMessage(instance.prefix + ChatColor.RED + "Tiempo despierto: " + ChatColor.GRAY + (days >= 1 ? days + " días " : "") + time);
    }

    private void handleDuracion(CommandSender sender) {
        World world = instance.world;
        if (!world.hasStorm()) {
            sender.sendMessage(instance.prefix + ChatColor.RED + "¡No hay ninguna tormenta en marcha!");
            return;
        }
        int seconds = world.getWeatherDuration() / 20;
        int days = seconds / 86400;
        String time = LocalTime.ofSecondOfDay(seconds % 86400).toString();
        sender.sendMessage(instance.prefix + ChatColor.RED + "Quedan " + ChatColor.GRAY + (days >= 1 ? days + "d " : "") + time);
    }

    private void handleIdioma(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) return;
        if (args.length < 2) {
            sender.sendMessage(TextUtils.format("&ePor favor ingresa un idioma. &7Ejemplo: &b/pdc idioma es &e<es, en>"));
            return;
        }
        String lang = args[1].toLowerCase();
        PlayerDataManager data = new PlayerDataManager(p.getName(), instance);
        if (lang.equals("es")) {
            if (data.getLanguage() == Language.SPANISH) { p.sendMessage(TextUtils.format("&c¡Ya estás usando el idioma español!")); return; }
            data.setLanguage(Language.SPANISH);
            p.sendMessage(TextUtils.format("&eHas cambiado tu idioma a: &bEspañol"));
        } else if (lang.equals("en")) {
            if (data.getLanguage() == Language.ENGLISH) { p.sendMessage(TextUtils.format("&cYour language is already set to english")); return; }
            data.setLanguage(Language.ENGLISH);
            p.sendMessage(TextUtils.format("&eYour language has been set to: &bEnglish"));
        }
    }

    private void handleCambiarDia(CommandSender sender, String[] args) {
        if (!sender.hasPermission("permadeathcore.cambiardia")) { sender.sendMessage(TextUtils.format("&cNo tienes permiso.")); return; }
        if (Main.SPEED_RUN_MODE) { sender.sendMessage(TextUtils.format("&cNo puedes hacer esto en modo SpeedRun.")); return; }
        if (args.length < 2) { sender.sendMessage(TextUtils.format("&cNecesitas agregar un día. &eEjemplo: &7/pdc cambiarDia <día>")); return; }
        DateManager.getInstance().setDay(sender, args[1]);
    }

    private void handleSetupBeginning(CommandSender sender) {
        if (!sender.isOp()) { sender.sendMessage(ChatColor.RED + "No tienes permiso."); return; }
        instance.setupFoliaWorldConfig(sender);
    }

    private void handleReload(CommandSender sender) {
        if (!sender.isOp()) { sender.sendMessage(ChatColor.RED + "No tienes permiso."); return; }
        instance.reload(sender);
    }

    private void handleDebug(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p) || !p.hasPermission("permadeathcore.admin")) return;
        if (args.length < 2) {
            p.sendMessage(TextUtils.format(instance.prefix + "&eSub comandos debug: &7info, generate_beginning, toggle, emptyWorld, module, health, events, hasOrb, hyper, removegaps, withertime, testtotems"));
            return;
        }
        switch (args[1].toLowerCase()) {
            case "info" -> {
                p.sendMessage(TextUtils.format(instance.prefix + "&6&lInformación Debug:"));
                p.sendMessage(TextUtils.format("&fDía: &a" + DateManager.getInstance().getDay()));
                p.sendMessage(TextUtils.format("&fServer: &a" + VersionManager.getFormattedVersion()));
                p.sendMessage(TextUtils.format("&fMundos: &a" + instance.world.getName() + " / " + instance.endWorld.getName()));
            }
            case "toggle" -> { Main.DEBUG = !Main.DEBUG; p.sendMessage("Debug: " + Main.DEBUG); }
            case "health" -> p.sendMessage("Vida máxima: " + NetheriteArmor.getAvailableMaxHealth(p));
            case "module" -> NMS.spawnDeathModule(p.getLocation());
            case "removegaps" -> {
                p.getPersistentDataContainer().remove(new NamespacedKey(instance, "hyper_one"));
                p.getPersistentDataContainer().remove(new NamespacedKey(instance, "hyper_two"));
                p.sendMessage("Gaps eliminadas.");
            }
        }
    }

    private void handleSpawn(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p) || !p.hasPermission("permadeathcore.admin")) return;
        if (args.length < 2) {
            p.sendMessage(TextUtils.format("&eIntroduce el mob: &bUltraRavager, CustomGiant, CustomCreeper, QuantumCreeper, EnderQuantumCreeper, CustomGhast, DeathModule..."));
            return;
        }
        String mob = args[1];
        if (mob.equalsIgnoreCase("DeathModule")) NMS.spawnDeathModule(p.getLocation());
        else if (mob.equalsIgnoreCase("CustomGhast")) NMS.getHandler().spawnCustomGhast(p.getLocation(), null, false);
        else if (mob.equalsIgnoreCase("QuantumCreeper")) instance.getFactory().spawnQuantumCreeper(p.getLocation(), null);
        else if (mob.equalsIgnoreCase("EnderQuantumCreeper")) instance.getFactory().spawnEnderQuantumCreeper(p.getLocation(), null);
        else {
            if (NMS.getHandler().spawnNMSCustomEntity(mob, null, p.getLocation(), null) != null) p.sendMessage(TextUtils.format("&a¡" + mob + " spawneado!"));
            else p.sendMessage(TextUtils.format("&cNo se encontró el mob: " + mob));
        }
    }

    private void handleGive(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p) || !p.hasPermission("permadeathcore.give")) return;
        if (args.length < 2) {
            p.sendMessage(TextUtils.format("&eItems: &7medalla, netheriteArmor, infernalArmor, netheriteTools, lifeOrb, ancestralFragment, moldes..."));
            return;
        }
        switch (args[1].toLowerCase()) {
            case "netheritearmor" -> {
                p.getInventory().addItem(NetheriteArmor.craftNetheriteHelmet(), NetheriteArmor.craftNetheriteChest(), NetheriteArmor.craftNetheriteLegs(), NetheriteArmor.craftNetheriteBoots());
                p.sendMessage(TextUtils.format("&eArmadura entregada."));
            }
            case "ancestralfragment" -> p.getInventory().addItem(NetheriteArmor.craftAncestralFragment());
            case "moldes" -> p.getInventory().addItem(NetheriteArmor.craftTemplate("helmet"), NetheriteArmor.craftTemplate("chestplate"), NetheriteArmor.craftTemplate("leggings"), NetheriteArmor.craftTemplate("boots"));
            case "medalla" -> p.getInventory().addItem(new ItemBuilder(Material.TOTEM_OF_UNDYING).setUnbrekeable(true).setDisplayName(TextUtils.format("&6&lMedalla de Superviviente")).build());
            case "infernalarmor" -> p.getInventory().addItem(InfernalNetherite.craftNetheriteHelmet(), InfernalNetherite.craftNetheriteChest(), InfernalNetherite.craftNetheriteLegs(), InfernalNetherite.craftNetheriteBoots());
            case "netheritetools" -> p.getInventory().addItem(PermadeathItems.craftNetheritePickaxe(), PermadeathItems.craftNetheriteSword(), PermadeathItems.craftNetheriteAxe(), PermadeathItems.craftNetheriteShovel(), PermadeathItems.craftNetheriteHoe());
            case "lifeorb" -> p.getInventory().addItem(PermadeathItems.createLifeOrb());
            case "endrelic" -> p.getInventory().addItem(PermadeathItems.crearReliquia());
            case "beginningrelic" -> p.getInventory().addItem(PermadeathItems.createBeginningRelic());
            case "watermedal" -> p.getInventory().addItem(PermadeathItems.createWaterMedal());
            case "abyssalheart" -> p.getInventory().addItem(PermadeathItems.createAbyssalHeart());
        }
    }

    private void handleStorm(CommandSender sender, String[] args) {
        if (!sender.hasPermission("permadeathcore.admin")) return;
        if (args.length < 3) { sender.sendMessage(TextUtils.format("&c/pdc storm <addHours/removeHours> <cantidad>")); return; }
        try {
            int hours = Math.max(1, Integer.parseInt(args[2]));
            String op = args[1];
            for (World w : Bukkit.getWorlds().stream().filter(world -> world.getEnvironment() == World.Environment.NORMAL).toList()) {
                int current = w.getWeatherDuration() / 20;
                int newValue = op.equalsIgnoreCase("addHours") ? current + (hours * 3600) : Math.max(1, current - (hours * 3600));
                
                if (Main.isRunningFolia()) {
                    Bukkit.getGlobalRegionScheduler().execute(instance, () -> {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "weather thunder");
                        w.setWeatherDuration(newValue * 20);
                    });
                } else {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "weather thunder");
                    w.setWeatherDuration(newValue * 20);
                }
            }
            sender.sendMessage(TextUtils.format("&aTormenta actualizada."));
        } catch (Exception e) { sender.sendMessage(TextUtils.format("&cError: Cantidad inválida.")); }
    }

    private void handleDias(CommandSender sender) {
        if (instance.getDay() < 1) sender.sendMessage(instance.prefix + "&cError al cargar el día.");
        else sender.sendMessage(instance.prefix + ChatColor.RED + (Main.SPEED_RUN_MODE ? "Hora: " : "Día: ") + ChatColor.GRAY + instance.getDay());
    }

    private void handleInfo(CommandSender sender) {
        if (sender instanceof Player p) {
            tech.sebazcrc.permadeath.util.gui.InfoGUI.open(p);
        } else {
            sender.sendMessage(instance.prefix + ChatColor.RED + "Version Info:");
            sender.sendMessage(ChatColor.GRAY + "- Plugin: " + ChatColor.GREEN + "PermaDeathCore.jar v" + instance.getDescription().getVersion());
            sender.sendMessage(ChatColor.GRAY + "- Dificultad: " + ChatColor.GREEN + "Días 1-60");
            sender.sendMessage(ChatColor.GRAY + "- Autor: " + ChatColor.GREEN + "InfernalCore Team");
        }
    }

    private void handleCambios(CommandSender sender) {
        sender.sendMessage(TextUtils.format("&eCambios de dificultad: &f&lhttps://permadeath.fandom.com/es/wiki/Cambios_de_dificultad"));
    }

    private void handleMensaje(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) return;
        if (args.length < 2) {
            String current = instance.getConfig().getString("Server-Messages.CustomDeathMessages." + p.getName(), instance.getConfig().getString("Server-Messages.DefaultDeathMessage"));
            p.sendMessage(TextUtils.format("&eTu mensaje actual: &7" + current));
            return;
        }
        String msg = String.join(" ", args).substring(args[0].length()).trim();
        if (msg.contains("&")) { p.sendMessage(ChatColor.RED + "No se permite el uso de colores."); return; }
        instance.getConfig().set("Server-Messages.CustomDeathMessages." + p.getName(), "&7" + msg);
        instance.saveConfig();
        p.sendMessage(TextUtils.format("&eMensaje actualizado a: &7" + msg));
    }

    private void handleBeginning(CommandSender sender, String[] args) {
        if (!sender.hasPermission("permadeathcore.admin") || args.length < 3) return;
        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) { sender.sendMessage(TextUtils.format("&cJugador no encontrado.")); return; }
        if (args[1].equalsIgnoreCase("bendicion")) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20 * 60 * 60 * 12, 1));
            Bukkit.broadcastMessage(TextUtils.format(Main.prefix + "&d&l" + target.getName() + " ha recibido la bendición!"));
        } else if (args[1].equalsIgnoreCase("maldicion")) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 60 * 60 * 12, 0));
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 60 * 60 * 12, 0));
            Bukkit.broadcastMessage(TextUtils.format(Main.prefix + "&d&l" + target.getName() + " ha recibido la maldición!"));
        }
    }

    private void handleBoss(CommandSender sender, String[] args) {
        if (!sender.hasPermission("permadeathcore.admin")) return;
        if (args.length < 3 || !args[1].equalsIgnoreCase("spawn")) {
            sender.sendMessage(TextUtils.format(instance.prefix + "&6Comandos de Bosses: &7/pdc boss spawn warden"));
            return;
        }
        if (!(sender instanceof Player p)) return;
        if (args[2].equalsIgnoreCase("warden") && VersionManager.getRev().equals("1_21_R3")) {
            instance.getNmsHandler().spawnNMSCustomEntity("boss.CustomWarden", null, p.getLocation(), null);
            Bukkit.broadcastMessage(TextUtils.format(instance.prefix + "&3&l¡El Twisted Warden ha sido invocado!"));
        }
    }

    private void handleSpeedRun(CommandSender sender, String[] args) {
        if (!sender.hasPermission("permadeathcore.admin") || args.length < 2) return;
        switch (args[1].toLowerCase()) {
            case "toggle" -> { Main.SPEED_RUN_MODE = !Main.SPEED_RUN_MODE; sender.sendMessage(TextUtils.format(Main.prefix + (Main.SPEED_RUN_MODE ? "&aActivado" : "&cDesactivado"))); }
            case "tiempo" -> sender.sendMessage(TextUtils.format(Main.prefix + "&eTiempo SpeedRun: &b" + TextUtils.formatInterval(instance.getPlayTime())));
            case "reset" -> { instance.setPlayTime(0); sender.sendMessage(TextUtils.format(Main.prefix + "&aTiempo reiniciado.")); }
        }
    }

    private void handleEvent(CommandSender sender, String[] args) {
        if (!sender.hasPermission("permadeathcore.event") || args.length < 2) return;
        String ev = args[1].toLowerCase();
        if (ev.equals("shulkershell")) {
            instance.getShulkerEvent().setRunning(true);
            Bukkit.getOnlinePlayers().forEach(instance.getShulkerEvent()::addPlayer);
            sender.sendMessage(TextUtils.format("&aEvento Shulker iniciado."));
        } else if (ev.equals("lifeorb") && instance.getDay() >= 60) {
            instance.getOrbEvent().setRunning(true);
            Bukkit.getOnlinePlayers().forEach(instance.getOrbEvent()::addPlayer);
            sender.sendMessage(TextUtils.format("&aEvento Life Orb iniciado."));
        }
    }

    private void handleLocate(CommandSender sender, String[] args) {
        if (!sender.hasPermission("permadeathcore.locate") || args.length < 2 || !args[1].equalsIgnoreCase("beginning")) return;
        if (instance.getDay() < 40 || instance.getBeData() == null || !instance.getBeData().generatedOverWorldBeginningPortal()) {
            sender.sendMessage(TextUtils.format("&cPortal no disponible aún."));
            return;
        }
        Location l = instance.getBeData().getOverWorldPortal();
        sender.sendMessage(TextUtils.format("&ePortal en Overworld: &b" + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ()));
    }

    private void handleAFK(CommandSender sender, String[] args) {
        if (!sender.hasPermission("permadeathcore.admin") || args.length < 2) return;
        if (args[1].equalsIgnoreCase("unban") && args.length >= 3) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pardon " + args[2]);
            new PlayerDataManager(args[2], instance).setLastDay(instance.getDay());
            sender.sendMessage(TextUtils.format("&aJugador perdonado."));
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(instance.prefix + ChatColor.RED + "Comandos PDC:");
        sender.sendMessage(ChatColor.RED + "/pdc idioma <es, en> " + ChatColor.GRAY + "(Cambia tu idioma)");
        sender.sendMessage(ChatColor.RED + "/pdc dias " + ChatColor.GRAY + "(Día actual)");
        sender.sendMessage(ChatColor.RED + "/pdc duracion " + ChatColor.GRAY + "(Tiempo de tormenta)");
        sender.sendMessage(ChatColor.RED + "/pdc cambios " + ChatColor.GRAY + "(Link a la wiki)");
        if (sender instanceof Player) {
            sender.sendMessage(ChatColor.RED + "/pdc mensaje <msj> " + ChatColor.GRAY + "(Tu mensaje de muerte)");
            sender.sendMessage(ChatColor.RED + "/pdc awake " + ChatColor.GRAY + "(Tiempo despierto)");
        }
        if (sender.hasPermission("permadeathcore.admin")) {
            sender.sendMessage(ChatColor.YELLOW + "/pdc give <item> " + ChatColor.GRAY + "(Dar items especiales)");
            sender.sendMessage(ChatColor.YELLOW + "/pdc spawn <mob> " + ChatColor.GRAY + "(Spawnear mobs custom)");
            sender.sendMessage(ChatColor.YELLOW + "/pdc storm <add/remove> <h> " + ChatColor.GRAY + "(Gestionar tormentas)");
            sender.sendMessage(ChatColor.YELLOW + "/pdc cambiarDia <d> " + ChatColor.GRAY + "(Cambiar el día)");
            sender.sendMessage(ChatColor.YELLOW + "/pdc reload/debug/afk/boss/event/speedrun/beginning/setupBeginning");
        }
    }
}


















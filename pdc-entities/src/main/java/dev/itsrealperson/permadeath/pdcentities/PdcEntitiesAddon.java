package dev.itsrealperson.permadeath.pdcentities;

import dev.itsrealperson.permadeath.api.PermadeathAPIProvider;
import dev.itsrealperson.permadeath.api.addons.AddonRegistry;
import dev.itsrealperson.permadeath.api.addons.PermadeathAddon;
import dev.itsrealperson.permadeath.pdcentities.command.EntitiesSubCommand;
import dev.itsrealperson.permadeath.pdcentities.script.MechanicRegistry;
import dev.itsrealperson.permadeath.pdcentities.script.TargeterRegistry;
import dev.itsrealperson.permadeath.pdcentities.script.mechanic.DamageMechanic;
import dev.itsrealperson.permadeath.pdcentities.script.targeter.SelfTargeter;
import java.io.File;

public class PdcEntitiesAddon implements PermadeathAddon {

    private static PermadeathAPIProvider api;
    private File dataFolder;
    public static boolean debugMode = false;

    // Constructor vacío obligatorio para el cargador
    public PdcEntitiesAddon() {}

    public static PermadeathAPIProvider getApi() {
        return api;
    }

    public static void log(String msg) {
        org.bukkit.Bukkit.getLogger().info("[PDC-Entities] " + msg);
    }

    public static void debug(String msg) {
        if (debugMode) {
            org.bukkit.Bukkit.getLogger().info("[PDC-Entities] [DEBUG] " + msg);
        }
    }

    @Override
    public String getName() { return "PDC-Entities"; }

    @Override
    public String getVersion() { return "0.0.1-Alpha"; }

    @Override
    public void onInitialize(PermadeathAPIProvider apiInstance) {
        api = apiInstance;
        this.dataFolder = api.getAddonDataFolder("PDC-Entities");
    }

    @Override
    public void onEnable() {
        if (!dataFolder.exists()) dataFolder.mkdirs();
        
        dev.itsrealperson.permadeath.pdcentities.integration.BetterModelHook.init();
        dev.itsrealperson.permadeath.pdcentities.script.MobManager.init((org.bukkit.plugin.Plugin) api);

        // Registrar sub-comando vía API
        AddonRegistry.registerSubCommand(new EntitiesSubCommand());
        
        // Registrar mecánicas base
        MechanicRegistry.register("damage", DamageMechanic::new);
        MechanicRegistry.register("message", dev.itsrealperson.permadeath.pdcentities.script.mechanic.MessageMechanic::new);
        MechanicRegistry.register("speak", dev.itsrealperson.permadeath.pdcentities.script.mechanic.SpeakMechanic::new);
        MechanicRegistry.register("lightning", dev.itsrealperson.permadeath.pdcentities.script.mechanic.LightningMechanic::new);
        MechanicRegistry.register("shieldbreak", dev.itsrealperson.permadeath.pdcentities.script.mechanic.ShieldBreakMechanic::new);
        MechanicRegistry.register("leap", dev.itsrealperson.permadeath.pdcentities.script.mechanic.LeapMechanic::new);
        MechanicRegistry.register("title", dev.itsrealperson.permadeath.pdcentities.script.mechanic.TitleMechanic::new);
        MechanicRegistry.register("actionbar", dev.itsrealperson.permadeath.pdcentities.script.mechanic.ActionBarMechanic::new);
        MechanicRegistry.register("spin", dev.itsrealperson.permadeath.pdcentities.script.mechanic.SpinMechanic::new);
        MechanicRegistry.register("blockwave", dev.itsrealperson.permadeath.pdcentities.script.mechanic.BlockWaveMechanic::new);
        MechanicRegistry.register("potion", dev.itsrealperson.permadeath.pdcentities.script.mechanic.PotionMechanic::new);
        MechanicRegistry.register("teleport", dev.itsrealperson.permadeath.pdcentities.script.mechanic.TeleportMechanic::new);
        MechanicRegistry.register("summon", dev.itsrealperson.permadeath.pdcentities.script.mechanic.SummonMechanic::new);
        MechanicRegistry.register("settarget", dev.itsrealperson.permadeath.pdcentities.script.mechanic.SetTargetMechanic::new);
        MechanicRegistry.register("delay", dev.itsrealperson.permadeath.pdcentities.script.mechanic.DelayMechanic::new);
        
        // Nuevas mecánicas avanzadas
        MechanicRegistry.register("particle", dev.itsrealperson.permadeath.pdcentities.script.mechanic.ParticleMechanic::new);
        MechanicRegistry.register("sound", dev.itsrealperson.permadeath.pdcentities.script.mechanic.SoundMechanic::new);
        MechanicRegistry.register("glow", dev.itsrealperson.permadeath.pdcentities.script.mechanic.GlowMechanic::new);
        MechanicRegistry.register("heal", dev.itsrealperson.permadeath.pdcentities.script.mechanic.HealMechanic::new);
        MechanicRegistry.register("velocity", dev.itsrealperson.permadeath.pdcentities.script.mechanic.VelocityMechanic::new);
        MechanicRegistry.register("explode", dev.itsrealperson.permadeath.pdcentities.script.mechanic.ExplodeMechanic::new);
        MechanicRegistry.register("drain_oxygen", dev.itsrealperson.permadeath.pdcentities.script.mechanic.DrainOxygenMechanic::new);
        MechanicRegistry.register("attribute", dev.itsrealperson.permadeath.pdcentities.script.mechanic.AttributeMechanic::new);
        MechanicRegistry.register("projectile", dev.itsrealperson.permadeath.pdcentities.script.mechanic.ProjectileMechanic::new);
        MechanicRegistry.register("animation", dev.itsrealperson.permadeath.pdcentities.script.mechanic.AnimationMechanic::new);
        MechanicRegistry.register("model", dev.itsrealperson.permadeath.pdcentities.script.mechanic.ModelMechanic::new);
        MechanicRegistry.register("randomskill", dev.itsrealperson.permadeath.pdcentities.script.mechanic.RandomSkillMechanic::new);
        MechanicRegistry.register("model_tint", dev.itsrealperson.permadeath.pdcentities.script.mechanic.ModelTintMechanic::new);
        MechanicRegistry.register("model_glow", dev.itsrealperson.permadeath.pdcentities.script.mechanic.ModelGlowMechanic::new);
        MechanicRegistry.register("model_enchant", dev.itsrealperson.permadeath.pdcentities.script.mechanic.ModelEnchantMechanic::new);
        MechanicRegistry.register("model_visibility", dev.itsrealperson.permadeath.pdcentities.script.mechanic.ModelVisibilityMechanic::new);
        MechanicRegistry.register("skill", dev.itsrealperson.permadeath.pdcentities.script.mechanic.MetaSkillMechanic::new);
        MechanicRegistry.register("ignite", dev.itsrealperson.permadeath.pdcentities.script.mechanic.IgniteMechanic::new);
        MechanicRegistry.register("jump", dev.itsrealperson.permadeath.pdcentities.script.mechanic.JumpMechanic::new);
        MechanicRegistry.register("absorption", dev.itsrealperson.permadeath.pdcentities.script.mechanic.AbsorptionMechanic::new);
        MechanicRegistry.register("swap", dev.itsrealperson.permadeath.pdcentities.script.mechanic.SwapMechanic::new);
        MechanicRegistry.register("consume", dev.itsrealperson.permadeath.pdcentities.script.mechanic.ConsumeMechanic::new);
        MechanicRegistry.register("stun", dev.itsrealperson.permadeath.pdcentities.script.mechanic.StunMechanic::new);
        MechanicRegistry.register("pull", args -> new dev.itsrealperson.permadeath.pdcentities.script.mechanic.ForceMechanic(args, true));
        MechanicRegistry.register("push", args -> new dev.itsrealperson.permadeath.pdcentities.script.mechanic.ForceMechanic(args, false));
        MechanicRegistry.register("particle_line", dev.itsrealperson.permadeath.pdcentities.script.mechanic.ParticleLineMechanic::new);
        MechanicRegistry.register("particle_ring", dev.itsrealperson.permadeath.pdcentities.script.mechanic.ParticleRingMechanic::new);
        MechanicRegistry.register("particle_helix", dev.itsrealperson.permadeath.pdcentities.script.mechanic.ParticleHelixMechanic::new);
        MechanicRegistry.register("particle_math", dev.itsrealperson.permadeath.pdcentities.script.mechanic.ParticleMathMechanic::new);
        MechanicRegistry.register("variable", dev.itsrealperson.permadeath.pdcentities.script.mechanic.VariableMechanic::new);
        MechanicRegistry.register("signal", dev.itsrealperson.permadeath.pdcentities.script.mechanic.SignalMechanic::new);
        
        // Registrar selectores base
        TargeterRegistry.register("self", args -> new dev.itsrealperson.permadeath.pdcentities.script.targeter.SelfTargeter());
        TargeterRegistry.register("target", args -> new dev.itsrealperson.permadeath.pdcentities.script.targeter.CurrentTargetTargeter());
        TargeterRegistry.register("pir", dev.itsrealperson.permadeath.pdcentities.script.targeter.PlayersInRadiusTargeter::new);
        TargeterRegistry.register("eir", dev.itsrealperson.permadeath.pdcentities.script.targeter.EntitiesInRadiusTargeter::new);
        TargeterRegistry.register("cone", dev.itsrealperson.permadeath.pdcentities.script.targeter.ConeTargeter::new);
        TargeterRegistry.register("nearestplayer", args -> new dev.itsrealperson.permadeath.pdcentities.script.targeter.NearestPlayerTargeter());
        TargeterRegistry.register("line", dev.itsrealperson.permadeath.pdcentities.script.targeter.LineTargeter::new);
        TargeterRegistry.register("forward", dev.itsrealperson.permadeath.pdcentities.script.targeter.ForwardTargeter::new);
        TargeterRegistry.register("playersinworld", args -> new dev.itsrealperson.permadeath.pdcentities.script.targeter.Targeter() {
            @Override
            public java.util.Collection<org.bukkit.entity.Entity> getTargets(org.bukkit.entity.Entity caster) {
                return new java.util.ArrayList<>(caster.getWorld().getPlayers());
            }
        });
        TargeterRegistry.register("ring", dev.itsrealperson.permadeath.pdcentities.script.targeter.RingTargeter::new);
        TargeterRegistry.register("block", dev.itsrealperson.permadeath.pdcentities.script.targeter.BlockTargeter::new);

        // Registrar condiciones
        dev.itsrealperson.permadeath.pdcentities.script.ConditionRegistry.register("day", dev.itsrealperson.permadeath.pdcentities.script.condition.DayCondition::new);
        dev.itsrealperson.permadeath.pdcentities.script.ConditionRegistry.register("health", dev.itsrealperson.permadeath.pdcentities.script.condition.HealthCondition::new);
        dev.itsrealperson.permadeath.pdcentities.script.ConditionRegistry.register("chance", dev.itsrealperson.permadeath.pdcentities.script.condition.ChanceCondition::new);
        dev.itsrealperson.permadeath.pdcentities.script.ConditionRegistry.register("targetwithin", dev.itsrealperson.permadeath.pdcentities.script.condition.TargetWithinCondition::new);
        dev.itsrealperson.permadeath.pdcentities.script.ConditionRegistry.register("distance", dev.itsrealperson.permadeath.pdcentities.script.condition.DistanceCondition::new);
        dev.itsrealperson.permadeath.pdcentities.script.ConditionRegistry.register("blocking", dev.itsrealperson.permadeath.pdcentities.script.condition.BlockingCondition::new);
        dev.itsrealperson.permadeath.pdcentities.script.ConditionRegistry.register("onground", dev.itsrealperson.permadeath.pdcentities.script.condition.OnGroundCondition::new);
        dev.itsrealperson.permadeath.pdcentities.script.ConditionRegistry.register("los", dev.itsrealperson.permadeath.pdcentities.script.condition.LineOfSightCondition::new);
        dev.itsrealperson.permadeath.pdcentities.script.ConditionRegistry.register("burning", dev.itsrealperson.permadeath.pdcentities.script.condition.BurningCondition::new);
        dev.itsrealperson.permadeath.pdcentities.script.ConditionRegistry.register("isplayer", dev.itsrealperson.permadeath.pdcentities.script.condition.IsPlayerCondition::new);
        dev.itsrealperson.permadeath.pdcentities.script.ConditionRegistry.register("weather", dev.itsrealperson.permadeath.pdcentities.script.condition.WeatherCondition::new);
        dev.itsrealperson.permadeath.pdcentities.script.ConditionRegistry.register("haspotion", dev.itsrealperson.permadeath.pdcentities.script.condition.PotionCondition::new);
        dev.itsrealperson.permadeath.pdcentities.script.ConditionRegistry.register("biome", dev.itsrealperson.permadeath.pdcentities.script.condition.BiomeCondition::new);
        dev.itsrealperson.permadeath.pdcentities.script.ConditionRegistry.register("inblock", dev.itsrealperson.permadeath.pdcentities.script.condition.InBlockCondition::new);
        dev.itsrealperson.permadeath.pdcentities.script.ConditionRegistry.register("world", dev.itsrealperson.permadeath.pdcentities.script.condition.WorldCondition::new);
        dev.itsrealperson.permadeath.pdcentities.script.ConditionRegistry.register("daytime", dev.itsrealperson.permadeath.pdcentities.script.condition.DayTimeCondition::new);
        dev.itsrealperson.permadeath.pdcentities.script.ConditionRegistry.register("height", dev.itsrealperson.permadeath.pdcentities.script.condition.HeightCondition::new);
        dev.itsrealperson.permadeath.pdcentities.script.ConditionRegistry.register("sneaking", dev.itsrealperson.permadeath.pdcentities.script.condition.SneakingCondition::new);
        dev.itsrealperson.permadeath.pdcentities.script.ConditionRegistry.register("lightlevel", dev.itsrealperson.permadeath.pdcentities.script.condition.LightLevelCondition::new);
        dev.itsrealperson.permadeath.pdcentities.script.ConditionRegistry.register("hasitem", args -> new dev.itsrealperson.permadeath.pdcentities.script.condition.ItemCondition(args, false));
        dev.itsrealperson.permadeath.pdcentities.script.ConditionRegistry.register("holding", args -> new dev.itsrealperson.permadeath.pdcentities.script.condition.ItemCondition(args, true));
        dev.itsrealperson.permadeath.pdcentities.script.ConditionRegistry.register("moving", dev.itsrealperson.permadeath.pdcentities.script.condition.MovementCondition::new);
        dev.itsrealperson.permadeath.pdcentities.script.ConditionRegistry.register("variable", dev.itsrealperson.permadeath.pdcentities.script.condition.VariableCondition::new);

        // --- INICIALIZAR MOTOR ---
        reload();
        
        // Registrar Provider de Spawn
        AddonRegistry.setSpawnProvider(new PdcSpawnProvider());
        
        // Registrar Listeners de eventos internos del addon
        org.bukkit.Bukkit.getPluginManager().registerEvents(new EntityListener(), (org.bukkit.plugin.Plugin) api);
        org.bukkit.Bukkit.getPluginManager().registerEvents(new InteractListener(), (org.bukkit.plugin.Plugin) api);

        // Iniciar Spawner Natural
        new NaturalSpawnerTask().start((org.bukkit.plugin.Plugin) api);
        
        if (api != null) {
            org.bukkit.Bukkit.getLogger().info(" [PDC-Entities] Motor de Comportamiento Activado vía API.");
        }
    }

    public void reload() {
        dev.itsrealperson.permadeath.pdcentities.util.FileUtil.copyResources(getClass(), "mobs", new File(dataFolder, "mobs"));
        dev.itsrealperson.permadeath.pdcentities.util.FileUtil.copyResources(getClass(), "skills", new File(dataFolder, "skills"));

        dev.itsrealperson.permadeath.pdcentities.script.MobManager.getTemplates().clear();
        dev.itsrealperson.permadeath.pdcentities.script.SkillRegistry.clear();

        new dev.itsrealperson.permadeath.pdcentities.script.SkillLoader(dataFolder, org.bukkit.Bukkit.getLogger()).loadAll();
        new dev.itsrealperson.permadeath.pdcentities.script.MobLoader(dataFolder, org.bukkit.Bukkit.getLogger()).loadAll();
        
        dev.itsrealperson.permadeath.pdcentities.script.MobManager.updateAll();
        log("Motor de entidades recargado.");
    }

    @Override
    public void onDisable() {
        org.bukkit.Bukkit.getLogger().info(" [PDC-Entities] Motor de Comportamiento Desactivado.");
    }

    @Override
    public File getDataFolder() { return dataFolder; }
}

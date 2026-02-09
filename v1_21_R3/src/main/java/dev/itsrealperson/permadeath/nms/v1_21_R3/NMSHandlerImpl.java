package dev.itsrealperson.permadeath.nms.v1_21_R3;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.Plugin;
import dev.itsrealperson.permadeath.nms.v1_21_R3.entity.*;
import dev.itsrealperson.permadeath.nms.v1_21_R3.entity.minions.*;
import dev.itsrealperson.permadeath.nms.v1_21_R3.entity.boss.*;
import dev.itsrealperson.permadeath.api.interfaces.NMSHandler;

public class NMSHandlerImpl implements NMSHandler {

    private final Plugin plugin;

    public NMSHandlerImpl(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Class<?> getNMSClass(String name) {
        return null; // En 1.21 no usamos carga directa por nombre de clase NMS
    }

    @Override
    public Object convertBukkitToNMS(EntityType ogType) {
        return null;
    }

    @Override
    public Entity spawnNMSEntity(String className, EntityType type, Location location, CreatureSpawnEvent.SpawnReason reason) {
        if (className.equals("DeathModuleImpl")) {
            new DeathModuleImpl().spawn(location);
            return null;
        }
        return location.getWorld().spawnEntity(location, type, reason);
    }

    @Override
    public Entity spawnNMSCustomEntity(String classPath, EntityType type, Location location, CreatureSpawnEvent.SpawnReason reason) {
        dev.itsrealperson.permadeath.api.entity.PermadeathEntity custom = spawnCustom(classPath, location);
        return (custom != null) ? custom.getBukkitEntity() : null;
    }

    @Override
    public dev.itsrealperson.permadeath.api.entity.PermadeathEntity spawnCustom(String name, Location location) {
        // Normalizar nombre si viene con paquete
        String id = name.contains(".") ? name.substring(name.lastIndexOf(".") + 1) : name;

        return switch (id) {
            case "UltraRavager" -> new dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper.UltraRavagerWrapper(UltraRavager.spawn(location, plugin));
            case "CustomWarden", "Warden", "TwistedWarden" -> new dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper.CustomWardenWrapper(CustomWarden.spawn(location, plugin));
            case "CustomGiant" -> new dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper.CustomGiantWrapper(CustomGiant.spawn(location, plugin));
            case "AbyssalCreeper" -> new dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper.AbyssalCreeperWrapper(AbyssalCreeper.spawn(location, plugin));
            case "SilentSeeker" -> new dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper.SilentSeekerWrapper(SilentSeeker.spawn(location, plugin));
            case "HollowGuard" -> new dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper.HollowGuardWrapper(HollowGuard.spawn(location, plugin));
            case "EchoArcher" -> new dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper.EchoArcherWrapper(EchoArcher.spawn(location, plugin));
            case "SculkParasite" -> new dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper.SculkParasiteWrapper(SculkParasite.spawn(location, plugin));
            case "PaleParagon" -> new dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper.PaleParagonWrapper(PaleParagon.spawn(location, plugin));
            case "VoidSkeleton" -> new dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper.VoidSkeletonWrapper(VoidSkeleton.spawn(location, plugin));
            case "ArcaneEvoker" -> new dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper.ArcaneEvokerWrapper(ArcaneEvoker.spawn(location, plugin));
            case "ArcaneBreeze" -> new dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper.ArcaneBreezeWrapper(ArcaneBreeze.spawn(location, plugin));
            case "GloomBat" -> new dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper.GloomBatWrapper(GloomBat.spawn(location, plugin));
            case "QuantumReactor" -> new dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper.QuantumReactorWrapper(QuantumReactor.spawn(location, plugin));
            case "SpecialPig" -> new dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper.SpecialPigWrapper(SpecialPig.spawn(location, plugin));
            case "CustomCreeper" -> new dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper.PermadeathCreeperWrapper(CustomCreeper.spawn(location, plugin, CustomCreeper.CreeperType.ENDER));
            case "QuantumCreeper" -> new dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper.PermadeathCreeperWrapper(CustomCreeper.spawn(location, plugin, CustomCreeper.CreeperType.QUANTUM));
            case "EnderQuantumCreeper" -> new dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper.PermadeathCreeperWrapper(CustomCreeper.spawn(location, plugin, CustomCreeper.CreeperType.ENDER_QUANTUM));
            case "CustomCod" -> new dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper.CustomCodWrapper(CustomCod.spawn(location, plugin));
            case "SpecialBee" -> new dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper.SpecialBeeWrapper(SpecialBee.spawn(location, plugin));
            case "AggressiveSnowGolem" -> new dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper.AggressiveSnowGolemWrapper(AggressiveSnowGolem.spawn(location, plugin));
            case "ExplosivePufferfish" -> new dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper.ExplosivePufferfishWrapper(ExplosivePufferfish.spawn(location, plugin));
            default -> {
                // Consultar el registro dinámico de la API para Addons
                var dynamic = dev.itsrealperson.permadeath.api.PermadeathAPI.getEntityRegistry();
                if (dynamic != null) {
                    yield dynamic.spawnEntity(id, location).orElse(null);
                }
                yield null;
            }
        };
    }

    private Entity spawnLegacy(String name, Location location) {
        return null;
    }

    @Override
    public Entity spawnCustomGhast(Location location, CreatureSpawnEvent.SpawnReason reason, boolean isEnder) {
        return CustomGhast.spawn(location, plugin);
    }

    @Override
    public void addMushrooms() {
        // Not needed for this version
    }

    // This method is not part of the interface, but it's used by the old implementation.
    // I will leave it here for now, but it should be removed if it's not used.
    public void setMaxHealth(LivingEntity entity, double health) {
        AttributeInstance attr = entity.getAttribute(Attribute.MAX_HEALTH);
        if (attr != null) {
            attr.setBaseValue(health);
            entity.setHealth(health);
        }
    }
}

package tech.sebazcrc.permadeath.nms.v1_21_R3;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.Plugin;
import tech.sebazcrc.permadeath.nms.v1_21_R3.entity.*;
import tech.sebazcrc.permadeath.nms.v1_21_R3.entity.minions.*;
import tech.sebazcrc.permadeath.nms.v1_21_R3.entity.boss.*;
import tech.sebazcrc.permadeath.api.interfaces.NMSHandler;

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
        // Normalizar nombre si viene con paquete
        String name = classPath.contains(".") ? classPath.substring(classPath.lastIndexOf(".") + 1) : classPath;

        switch (name) {
            case "UltraRavager":
                return UltraRavager.spawn(location, plugin);
            case "SpecialPig":
                return SpecialPig.spawn(location, plugin);
            case "CustomGiant":
                return CustomGiant.spawn(location, plugin);
            case "CustomCreeper":
                return CustomCreeper.spawn(location, plugin, CustomCreeper.CreeperType.ENDER);
            case "QuantumCreeper":
                return CustomCreeper.spawn(location, plugin, CustomCreeper.CreeperType.QUANTUM);
            case "EnderQuantumCreeper":
                return CustomCreeper.spawn(location, plugin, CustomCreeper.CreeperType.ENDER_QUANTUM);
            case "CustomCod":
                return CustomCod.spawn(location, plugin);
            case "SpecialBee":
                return SpecialBee.spawn(location, plugin);
            case "QuantumReactor":
                return QuantumReactor.spawn(location, plugin);
            case "PaleParagon":
                return PaleParagon.spawn(location, plugin);
            case "ArcaneEvoker":
                return ArcaneEvoker.spawn(location, plugin);
            case "ArcaneBreeze":
                return ArcaneBreeze.spawn(location, plugin);
            case "SilentSeeker":
                return SilentSeeker.spawn(location, plugin);
            case "SculkParasite":
                return SculkParasite.spawn(location, plugin);
            case "EchoArcher":
                return EchoArcher.spawn(location, plugin);
            case "GloomBat":
                return GloomBat.spawn(location, plugin);
            case "HollowGuard":
                return HollowGuard.spawn(location, plugin);
            case "AggressiveSnowGolem":
                return AggressiveSnowGolem.spawn(location, plugin);
            case "ExplosivePufferfish":
                return ExplosivePufferfish.spawn(location, plugin);
            case "CustomWarden":
            case "Warden":
                return CustomWarden.spawn(location, plugin);
            default:
                return null;
        }
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



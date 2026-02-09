package dev.itsrealperson.permadeath.util;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChangeGameState;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerExplosion;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoRemove;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Gestor de efectos visuales mediante paquetes (PacketEvents).
 */
public class PacketManager {

    /**
     * Inyecta un jugador global en el TAB de los jugadores locales.
     */
    public static void injectPlayerToList(Player viewer, UUID uuid, String name) {
        // Creamos la entrada del jugador
        WrapperPlayServerPlayerInfoUpdate.PlayerInfo entry = new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
                new UserProfile(uuid, name),
                true, // Listado
                10, // Latencia fake
                GameMode.SURVIVAL,
                null, // Nombre en lista (null = nombre real)
                null  // Chat Session
        );

        // PacketEvents 2.x usa EnumSet para indicar qué campos actualizar
        EnumSet<WrapperPlayServerPlayerInfoUpdate.Action> actions = EnumSet.of(
                WrapperPlayServerPlayerInfoUpdate.Action.ADD_PLAYER,
                WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_LISTED,
                WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_LATENCY,
                WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_GAME_MODE
        );

        WrapperPlayServerPlayerInfoUpdate packet = new WrapperPlayServerPlayerInfoUpdate(actions, Collections.singletonList(entry));
        PacketEvents.getAPI().getPlayerManager().sendPacket(viewer, packet);
    }

    /**
     * Elimina un jugador del TAB local.
     */
    public static void removePlayerFromList(Player viewer, UUID uuid) {
        WrapperPlayServerPlayerInfoRemove packet = new WrapperPlayServerPlayerInfoRemove(uuid);
        PacketEvents.getAPI().getPlayerManager().sendPacket(viewer, packet);
    }

    /**
     * Envía un rayo puramente visual a un jugador o a todos cerca.
     */
    public static void sendFakeLightning(Location loc) {
        WrapperPlayServerSpawnEntity spawnLightning = new WrapperPlayServerSpawnEntity(
                ThreadLocalRandom.current().nextInt(100000, 200000), // ID Temporal
                Optional.of(UUID.randomUUID()),
                EntityTypes.LIGHTNING_BOLT,
                new Vector3d(loc.getX(), loc.getY(), loc.getZ()),
                0, 0, 0, 0, null
        );

        loc.getWorld().getPlayers().forEach(p -> {
            if (p.getLocation().distanceSquared(loc) < 128 * 128) {
                PacketEvents.getAPI().getPlayerManager().sendPacket(p, spawnLightning);
            }
        });
    }

    /**
     * Envía una explosión puramente visual (con sonido y partículas) pero sin daño real.
     */
    public static void sendVisualExplosion(Location loc, float power) {
        WrapperPlayServerExplosion explosion = new WrapperPlayServerExplosion(
                new Vector3d(loc.getX(), loc.getY(), loc.getZ()),
                power,
                new ArrayList<Vector3i>(),
                new Vector3f(0, 0, 0)
        );

        loc.getWorld().getPlayers().forEach(p -> {
            if (p.getLocation().distanceSquared(loc) < 64 * 64) {
                PacketEvents.getAPI().getPlayerManager().sendPacket(p, explosion);
            }
        });
    }

    /**
     * Ajusta la oscuridad del cielo (nivel de lluvia/trueno) visualmente.
     * @param level 0.0f a 1.0f.
     */
    public static void setSkyDarkness(Player player, float level) {
        WrapperPlayServerChangeGameState rainLevel = new WrapperPlayServerChangeGameState(
                WrapperPlayServerChangeGameState.Reason.RAIN_LEVEL_CHANGE,
                level
        );
        WrapperPlayServerChangeGameState thunderLevel = new WrapperPlayServerChangeGameState(
                WrapperPlayServerChangeGameState.Reason.THUNDER_LEVEL_CHANGE,
                level
        );
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, rainLevel);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, thunderLevel);
    }

    /**
     * Cambia el clima visualmente para un jugador específico.
     * @param rain True para empezar a llover visualmente.
     */
    public static void setFakeWeather(Player player, boolean rain) {
        WrapperPlayServerChangeGameState packet = new WrapperPlayServerChangeGameState(
                rain ? WrapperPlayServerChangeGameState.Reason.BEGIN_RAINING : WrapperPlayServerChangeGameState.Reason.END_RAINING,
                0f
        );
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
    }

    /**
     * Simula un temblor de cámara (Camera Shake) enviando un paquete de explosión visual.
     * @param loc Ubicación del epicentro.
     * @param radius Radio visual del efecto.
     */
    public static void sendCameraShake(Location loc, float radius) {
        // En Java Edition, el paquete de explosión causa un "sacudón" de cámara
        // si el jugador está cerca. Al enviar una lista de bloques vacía, no hay daño real.
        WrapperPlayServerExplosion explosion = new WrapperPlayServerExplosion(
                new Vector3d(loc.getX(), loc.getY(), loc.getZ()),
                radius,
                new ArrayList<Vector3i>(),
                new Vector3f(0, 0, 0) // Sin empuje real
        );

        loc.getWorld().getPlayers().forEach(p -> {
            if (p.getLocation().distanceSquared(loc) < (radius * 4) * (radius * 4)) {
                PacketEvents.getAPI().getPlayerManager().sendPacket(p, explosion);
            }
        });
    }

    /**
     * Envía metadatos de Guardian para mostrar un láser hacia un objetivo.
     */
    public static void sendGuardianBeam(int entityId, int targetId, Location loc) {
        List<EntityData<?>> data = new ArrayList<>();
        data.add(new EntityData(16, EntityDataTypes.INT, targetId));

        WrapperPlayServerEntityMetadata metadataPacket = new WrapperPlayServerEntityMetadata(entityId, data);

        loc.getWorld().getPlayers().forEach(p -> {
            if (p.getLocation().distanceSquared(loc) < 64 * 64) {
                PacketEvents.getAPI().getPlayerManager().sendPacket(p, metadataPacket);
            }
        });
    }

    /**
     * Activa o desactiva el borde rojo de peligro en la pantalla del jugador.
     */
    public static void setWarningBorder(Player player, boolean enabled) {
        // Fallback a Bukkit para máxima compatibilidad
        if (enabled) {
            player.getWorldBorder().setWarningDistance(Integer.MAX_VALUE);
        } else {
            player.getWorldBorder().setWarningDistance(0);
        }
    }
}
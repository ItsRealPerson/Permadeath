package tech.sebazcrc.permadeath.nms.v1_21_R3.utils;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MobUtils {

    /**
     * Busca al jugador válido más cercano en un rango determinado.
     * Prioriza jugadores en Survival/Aventura que no estén en modo espectador.
     * Optimizado: Itera únicamente los jugadores del mundo sin escanear chunks ni crear Streams.
     *
     * @param source La entidad que busca (el mob).
     * @param range El radio de búsqueda en bloques.
     * @return El jugador más cercano o null si no se encuentra ninguno válido.
     */
    public static Player getNearestPlayer(LivingEntity source, double range) {
        if (source == null || !source.isValid()) return null;

        World world = source.getWorld();
        Location srcLoc = source.getLocation();
        double maxDistSq = range * range;
        Player nearest = null;
        double bestDistSq = Double.MAX_VALUE;

        for (Player p : world.getPlayers()) {
            if (!isValidTarget(p)) continue;
            double distSq = p.getLocation().distanceSquared(srcLoc);
            double allowedRangeSq = p.isSneaking() ? (8.0 * 8.0) : maxDistSq;
            if (distSq <= allowedRangeSq && distSq < bestDistSq) {
                nearest = p;
                bestDistSq = distSq;
            }
        }

        return nearest;
    }

    /**
     * Obtiene una lista de todos los jugadores válidos en un rango.
     * Útil para ataques de área (AoE).
     */
    public static List<Player> getNearbyPlayers(LivingEntity source, double range) {
        if (source == null || !source.isValid()) return Collections.emptyList();

        World world = source.getWorld();
        Location srcLoc = source.getLocation();
        double maxDistSq = range * range;
        List<Player> players = new ArrayList<>();

        for (Player p : world.getPlayers()) {
            if (!isValidTarget(p)) continue;
            double distSq = p.getLocation().distanceSquared(srcLoc);
            if (distSq <= maxDistSq) {
                players.add(p);
            }
        }
        return players;
    }

    /**
     * Valida si un jugador es un objetivo viable para la IA del mob.
     * Filtra modos de juego invulnerables, muertos o desconectados.
     */
    public static boolean isValidTarget(Player p) {
        return p != null &&
                p.isOnline() &&
                !p.isDead() &&
                (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE) &&
                !p.isInvulnerable(); // Importante para admins en god mode
    }

    /**
     * Comprueba si hay línea de visión directa entre dos entidades.
     * Evita que los mobs te "huelan" a través de paredes sólidas si no es deseado.
     * (El Warden y sus minions ignoran esto porque huelen/escuchan).
     */
    public static boolean hasLineOfSight(LivingEntity source, LivingEntity target) {
        return source.hasLineOfSight(target);
    }

    /**
     * Empuja una entidad lejos de un punto de origen (Efecto Knockback).
     */
    public static void pushAway(Entity entity, Location from, double speed) {
        Vector direction = entity.getLocation().toVector().subtract(from.toVector()).normalize();
        entity.setVelocity(direction.multiply(speed));
    }
}

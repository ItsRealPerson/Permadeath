package dev.itsrealperson.permadeath.nms.v1_21_R3.utils;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MobUtils {

    /**
     * Busca al jugador válido más cercano en un rango determinado.
     * Prioriza jugadores en Survival/Aventura que no estén en modo espectador.
     *
     * @param source La entidad que busca (el mob).
     * @param range El radio de búsqueda en bloques.
     * @return El jugador más cercano o null si no se encuentra ninguno válido.
     */
    public static Player getNearestPlayer(LivingEntity source, double range) {
        if (source == null || !source.isValid()) return null;

        // Optimización: Usar getNearbyEntities con un Consumer/Predicate es más rápido en Paper
        // pero por compatibilidad con Spigot puro usamos el método estándar y filtramos.
        List<Entity> nearby = source.getNearbyEntities(range, range / 2, range);

        return nearby.stream()
                .filter(e -> e instanceof Player)
                .map(e -> (Player) e)
                .filter(MobUtils::isValidTarget)
                .filter(p -> {
                    // MECÁNICA DE SIGILO: Si el jugador se agacha, el rango de detección baja a 8 bloques
                    if (p.isSneaking()) {
                        return p.getLocation().distanceSquared(source.getLocation()) <= 8 * 8;
                    }
                    // Si no está agachado, se detecta por vibración/sonido en el rango total
                    return true;
                })
                .min(Comparator.comparingDouble(p -> p.getLocation().distanceSquared(source.getLocation())))
                .orElse(null);
    }

    /**
     * Obtiene una lista de todos los jugadores válidos en un rango.
     * Útil para ataques de área (AoE).
     */
    public static List<Player> getNearbyPlayers(LivingEntity source, double range) {
        if (source == null || !source.isValid()) return new ArrayList<>();

        List<Entity> nearby = source.getNearbyEntities(range, range / 2, range);
        return nearby.stream()
                .filter(e -> e instanceof Player)
                .map(e -> (Player) e)
                .filter(MobUtils::isValidTarget)
                .collect(Collectors.toList());
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

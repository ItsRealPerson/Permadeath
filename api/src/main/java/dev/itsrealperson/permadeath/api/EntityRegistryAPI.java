package dev.itsrealperson.permadeath.api;

import dev.itsrealperson.permadeath.api.entity.PermadeathEntity;
import org.bukkit.Location;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Interfaz para el registro de entidades personalizadas de Permadeath.
 * Permite a los Addons registrar sus propios spawners de entidades abstraídas.
 */
public interface EntityRegistryAPI {

    /**
     * Registra un nuevo tipo de entidad.
     * @param id Identificador único (ej: "SuperZombie").
     * @param spawner Función que recibe una ubicación y devuelve la entidad abstraída.
     */
    void registerEntity(String id, Function<Location, PermadeathEntity> spawner);

    /**
     * Spawnea una entidad registrada.
     * @param id Identificador de la entidad.
     * @param location Ubicación del spawn.
     * @return Un Optional con la entidad si el ID existe.
     */
    Optional<PermadeathEntity> spawnEntity(String id, Location location);

    /**
     * @return Todos los IDs de entidades registradas.
     */
    Set<String> getRegisteredIds();
}

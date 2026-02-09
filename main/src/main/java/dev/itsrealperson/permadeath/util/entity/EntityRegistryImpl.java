package dev.itsrealperson.permadeath.util.entity;

import dev.itsrealperson.permadeath.api.EntityRegistryAPI;
import dev.itsrealperson.permadeath.api.entity.PermadeathEntity;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class EntityRegistryImpl implements EntityRegistryAPI {

    private final Map<String, Function<Location, PermadeathEntity>> entities = new HashMap<>();

    @Override
    public void registerEntity(String id, Function<Location, PermadeathEntity> spawner) {
        entities.put(id, spawner);
    }

    @Override
    public Optional<PermadeathEntity> spawnEntity(String id, Location location) {
        if (!entities.containsKey(id)) return Optional.empty();
        return Optional.ofNullable(entities.get(id).apply(location));
    }

    @Override
    public Set<String> getRegisteredIds() {
        return entities.keySet();
    }
}

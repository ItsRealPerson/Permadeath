package dev.itsrealperson.permadeath.api.addons;

import dev.itsrealperson.permadeath.api.addons.providers.HealthProvider;
import dev.itsrealperson.permadeath.api.addons.providers.SpawnProvider;
import dev.itsrealperson.permadeath.api.addons.providers.VisualProvider;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Punto central de registro para Addons y sus componentes.
 */
public class AddonRegistry {

    private static final Map<String, PermadeathAddon> addons = new HashMap<>();
    private static final Map<String, AddonSubCommand> subCommands = new HashMap<>();
    
    private static HealthProvider healthProvider;
    private static SpawnProvider spawnProvider;
    private static VisualProvider visualProvider;

    /**
     * Registra un nuevo addon en el ecosistema.
     */
    public static void registerAddon(PermadeathAddon addon) {
        addons.put(addon.getName().toLowerCase(), addon);
        addon.onEnable();
    }

    public static Optional<PermadeathAddon> getAddon(String name) {
        return Optional.ofNullable(addons.get(name.toLowerCase()));
    }

    public static Collection<PermadeathAddon> getRegisteredAddons() {
        return Collections.unmodifiableCollection(addons.values());
    }

    // --- SISTEMA DE COMANDOS ---

    public static void registerSubCommand(AddonSubCommand command) {
        subCommands.put(command.getName().toLowerCase(), command);
    }

    public static Optional<AddonSubCommand> getSubCommand(String name) {
        return Optional.ofNullable(subCommands.get(name.toLowerCase()));
    }

    public static Collection<AddonSubCommand> getSubCommands() {
        return Collections.unmodifiableCollection(subCommands.values());
    }

    // --- SISTEMA DE PROVIDERS ---

    public static void setHealthProvider(HealthProvider provider) {
        healthProvider = provider;
    }

    public static Optional<HealthProvider> getHealthProvider() {
        return Optional.ofNullable(healthProvider);
    }

    public static void setSpawnProvider(SpawnProvider provider) {
        spawnProvider = provider;
    }

    public static Optional<SpawnProvider> getSpawnProvider() {
        return Optional.ofNullable(spawnProvider);
    }

    public static void setVisualProvider(VisualProvider provider) {
        visualProvider = provider;
    }

    public static Optional<VisualProvider> getVisualProvider() {
        return Optional.ofNullable(visualProvider);
    }
}

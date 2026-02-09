package dev.itsrealperson.permadeath.api;

import java.util.Optional;

/**
 * Interfaz para el gestor de módulos de Permadeath.
 */
public interface ModuleManagerAPI {

    /**
     * Registra e inicializa un nuevo módulo.
     */
    void registerModule(PermadeathModule module);

    /**
     * Busca un módulo por su nombre.
     */
    Optional<PermadeathModule> getModule(String name);
}

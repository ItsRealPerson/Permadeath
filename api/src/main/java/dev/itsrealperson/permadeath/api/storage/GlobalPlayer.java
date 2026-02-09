package dev.itsrealperson.permadeath.api.storage;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

/**
 * Representa un jugador en cualquier servidor de la red.
 */
@Data
@AllArgsConstructor
public class GlobalPlayer {
    private final UUID uuid;
    private final String name;
    private final String serverId;
    private final String worldName;
}

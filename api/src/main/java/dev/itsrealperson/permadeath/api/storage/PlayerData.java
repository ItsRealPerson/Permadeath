package dev.itsrealperson.permadeath.api.storage;

import dev.itsrealperson.permadeath.api.Language;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * Representa los datos persistentes de un jugador en Permadeath.
 */
@Data
@Builder
public class PlayerData {
    private final String name;
    private UUID uuid;
    private String banDay;
    private String banTime;
    private String banCause;
    private String coords;
    private int extraHP;
    private Language language;
    private long lastDay;
    private boolean locked; // Nuevo flag de seguridad

    public static PlayerData createDefault(String name) {
        return PlayerData.builder()
                .name(name)
                .banDay("")
                .banTime("")
                .banCause("")
                .coords("")
                .extraHP(0)
                .language(Language.SPANISH)
                .lastDay(0)
                .locked(false)
                .build();
    }
}

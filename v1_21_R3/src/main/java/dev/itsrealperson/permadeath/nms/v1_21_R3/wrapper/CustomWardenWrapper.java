package dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper;

import dev.itsrealperson.permadeath.api.entity.CustomWarden;
import org.bukkit.entity.Warden;

public record CustomWardenWrapper(Warden warden) implements CustomWarden {

    @Override
    public Warden getBukkitEntity() {
        return warden;
    }

    @Override
    public void performSonicBoom() {
        // La lógica ya está en la tarea del warden, pero podríamos exponerla aquí
    }

    @Override
    public void summonMinions() {
        // Ídem
    }
}

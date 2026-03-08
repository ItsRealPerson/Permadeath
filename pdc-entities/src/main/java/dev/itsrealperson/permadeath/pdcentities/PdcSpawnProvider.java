package dev.itsrealperson.permadeath.pdcentities;

import dev.itsrealperson.permadeath.api.addons.providers.SpawnProvider;
import dev.itsrealperson.permadeath.pdcentities.script.MobManager;
import dev.itsrealperson.permadeath.pdcentities.script.PdcMobTemplate;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import java.util.Random;

public class PdcSpawnProvider implements SpawnProvider {

    private final Random random = new Random();

    @Override
    public SpawnResult handleSpawn(EntityType type, Location location, CreatureSpawnEvent.SpawnReason reason) {
        if (reason == CreatureSpawnEvent.SpawnReason.CUSTOM) return new SpawnResult(SpawnProvider.SpawnResult.Decision.IGNORE, null, null);

        PdcMobTemplate replacement = MobManager.findReplacement(type, random);
        
        if (replacement != null) {
            PdcEntitiesAddon.debug("§b[Replacement] Reemplazando " + type + " por " + replacement.getId() + " (" + reason + ")");
            return new SpawnResult(SpawnProvider.SpawnResult.Decision.CUSTOM, replacement.getId(), null);
        }

        return new SpawnResult(SpawnProvider.SpawnResult.Decision.IGNORE, null, null);
    }
}

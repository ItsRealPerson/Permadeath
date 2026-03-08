package dev.itsrealperson.permadeath.pdcentities;

import dev.itsrealperson.permadeath.pdcentities.script.MobManager;
import dev.itsrealperson.permadeath.pdcentities.script.PdcMobInstance;
import dev.itsrealperson.permadeath.pdcentities.script.PdcMobTemplate;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractListener implements Listener {

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent e) {
        PdcMobInstance instance = MobManager.getInstance(e.getRightClicked());
        if (instance != null) {
            PdcEntitiesAddon.debug("§e[Trigger] onInteract detectado para " + instance.getTemplate().getId());
            instance.executeTrigger("onInteract");
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;

        for (PdcMobTemplate template : MobManager.getTemplates().values()) {
            if (template.getInteractBlock() == null) continue;

            if (e.getClickedBlock().getType() == template.getInteractBlock()) {
                String action = e.getAction().name();
                if (action.contains(template.getInteractAction().toUpperCase())) {
                    PdcEntitiesAddon.debug("§b[Interact] Generando " + template.getId() + " por interacción con " + e.getClickedBlock().getType());
                    MobManager.spawnMob(template.getId(), e.getClickedBlock().getLocation().add(0.5, 1, 0.5), 
                        PdcEntitiesAddon.getApi().getDay());
                    break;
                }
            }
        }
    }
}

package dev.itsrealperson.permadeath.pdcentities;

import com.destroystokyo.paper.event.entity.EntityJumpEvent;
import dev.itsrealperson.permadeath.pdcentities.script.MobManager;
import dev.itsrealperson.permadeath.pdcentities.script.PdcMobInstance;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;

public class EntityListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJump(EntityJumpEvent e) {
        PdcMobInstance instance = MobManager.getInstance(e.getEntity());
        if (instance != null) {
            instance.executeTrigger("onJump");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAttack(EntityDamageByEntityEvent e) {
        PdcMobInstance attacker = MobManager.getInstance(e.getDamager());
        if (attacker != null) {
            PdcEntitiesAddon.debug("§e[Trigger] onAttack detectado para " + attacker.getTemplate().getId());
            attacker.executeTrigger("onAttack");
            attacker.playAnimation("attack");
        }

        PdcMobInstance victim = MobManager.getInstance(e.getEntity());
        if (victim != null) {
            PdcEntitiesAddon.debug("§e[Trigger] onDamaged detectado para " + victim.getTemplate().getId());
            victim.executeTrigger("onDamaged");
            
            // Si el daño fue bloqueado o reducido drásticamente, disparar animación de guardia
            if (e.getDamage() < 1.0) {
                victim.playAnimation("guard");
            }
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        PdcMobInstance instance = MobManager.getInstance(e.getEntity());
        if (instance != null) {
            PdcEntitiesAddon.debug("§e[Trigger] onDeath detectado para " + instance.getTemplate().getId());
            instance.executeTrigger("onDeath");
            instance.playAnimation("death");
            MobManager.removeInstance(e.getEntity().getUniqueId());
        }

        // Trigger: onKill
        if (e.getEntity().getKiller() != null) {
            PdcMobInstance killer = MobManager.getInstance(e.getEntity().getKiller());
            if (killer != null) {
                PdcEntitiesAddon.debug("§e[Trigger] onKill detectado para " + killer.getTemplate().getId());
                killer.executeTrigger("onKill");
            }
        }
    }

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent e) {
        if (e.getTarget() == null) return;

        // Cancelar si el objetivo es un jugador en creativo o espectador
        if (e.getTarget() instanceof Player p) {
            if (p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR) {
                e.setCancelled(true);
                return;
            }
        }

        PdcMobInstance instance = MobManager.getInstance(e.getEntity());
        if (instance != null) {
            PdcEntitiesAddon.debug("§e[Trigger] onEnterCombat detectado para " + instance.getTemplate().getId());
            instance.executeTrigger("onEnterCombat");
        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent e) {
        PdcMobInstance instance = MobManager.getInstance(e.getEntity());
        if (instance != null) {
            PdcEntitiesAddon.debug("§e[Trigger] onExplode detectado para " + instance.getTemplate().getId());
            instance.executeTrigger("onExplode");
        }
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent e) {
        PdcMobInstance instance = MobManager.getInstance(e.getEntity());
        if (instance != null) {
            PdcEntitiesAddon.debug("§e[Trigger] onShoot detectado para " + instance.getTemplate().getId());
            instance.executeTrigger("onShoot");
        }
    }
    
    @EventHandler
    public void onProjectile(ProjectileLaunchEvent e) {
        if (e.getEntity().getShooter() instanceof Entity shooter) {
            PdcMobInstance instance = MobManager.getInstance(shooter);
            if (instance != null) {
                PdcEntitiesAddon.debug("§e[Trigger] onShoot (Proyectil) detectado para " + instance.getTemplate().getId());
                instance.executeTrigger("onShoot");
            }
        }
    }

    @EventHandler
    public void onEntityLoad(org.bukkit.event.world.EntitiesLoadEvent e) {
        for (Entity entity : e.getEntities()) {
            MobManager.restoreInstance(entity);
        }
    }
}

package tech.sebazcrc.permadeath.event.raid;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.raid.RaidFinishEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tech.sebazcrc.permadeath.Main;

public class RaidEvents implements Listener {

    @EventHandler
    public void onRaidFinish(RaidFinishEvent e) {
        if (Main.getInstance().getDay() < 50) return;
        if (e.getWinners().isEmpty()) return;

        if (Main.isRunningFolia()) {
            for (Player player : e.getWinners()) {
                player.getScheduler().runDelayed(Main.instance, t -> giveHeroBuff(player), null, 40L);
            }
        } else {
            Bukkit.getScheduler().runTaskLater(Main.instance, () -> {
                for (Player player : e.getWinners()) {
                    giveHeroBuff(player);
                }
            }, 40L);
        }
    }

    private void giveHeroBuff(Player player) {
        if (player == null || !player.isOnline()) return;
        if (player.hasPotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE)) {
            PotionEffect effect = player.getPotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE);
            player.removePotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE);

            int min = 5 * 60;
            player.addPotionEffect(new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, min, effect != null ? effect.getAmplifier() : 0));
        }
    }
}


















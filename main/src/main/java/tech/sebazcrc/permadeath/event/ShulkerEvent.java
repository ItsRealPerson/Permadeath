package tech.sebazcrc.permadeath.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import java.util.HashSet;
import java.util.Set;

public class ShulkerEvent {
    @Getter @Setter private boolean running = false;
    private final Set<Player> players = new HashSet<>();

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }
}












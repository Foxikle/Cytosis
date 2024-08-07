package net.cytonic.cytosis.events.ranks;

import lombok.Getter;
import net.cytonic.enums.PlayerRank;
import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.event.trait.PlayerEvent;

/**
 * An event that is fired when a player's rank is setup
 */
@Getter
public class RankSetupEvent implements PlayerEvent, CancellableEvent {

    private final Player player;
    private final PlayerRank rank;
    private boolean canceled;

    /**
     * Creates a new event object
     *
     * @param player the player
     * @param rank   the rank
     */
    public RankSetupEvent(Player player, PlayerRank rank) {
        this.player = player;
        this.rank = rank;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public void setCancelled(boolean canceled) {
        this.canceled = canceled;
    }
}
package net.cytonic.cytosis.messaging.pubsub;

import net.cytonic.containers.PlayerChangeServerContainer;
import net.cytonic.cytosis.Cytosis;
import net.cytonic.cytosis.data.RedisDatabase;
import redis.clients.jedis.JedisPubSub;

/**
 * A pub sub listener that handles player server changes
 */
public class PlayerServerChange extends JedisPubSub {

    /**
     * Default constructor
     */
    public PlayerServerChange() {
    }

    /**
     * Consumes messages on the redis pub/sub interface to determine the online players
     *
     * @param channel The channel that was messaged
     * @param message The content of the message
     */
    @Override
    public void onMessage(String channel, String message) {
        if (!channel.equals(RedisDatabase.PLAYER_SERVER_CHANGE_CHANNEL)) return;
        PlayerChangeServerContainer container = PlayerChangeServerContainer.deserialize(message);
        Cytosis.getCytonicNetwork().getNetworkPlayersOnServers().put(container.uuid(), container.serverName());
    }
}

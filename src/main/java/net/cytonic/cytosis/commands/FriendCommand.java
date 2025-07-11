package net.cytonic.cytosis.commands;

import net.cytonic.cytosis.Cytosis;
import net.cytonic.cytosis.commands.utils.CytosisCommand;
import net.cytonic.cytosis.player.CytosisPlayer;
import net.cytonic.cytosis.utils.CytosisPreferences;
import net.cytonic.cytosis.utils.Msg;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.condition.Conditions;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * A command to handle friends
 */
public class FriendCommand extends CytosisCommand {

    /**
     * Creates the command
     */
    public FriendCommand() {
        super("friend", "f");
        setCondition(Conditions::playerOnly);

        var action = ArgumentType.Word("action").from("add", "remove", "list", "accept", "decline");

        var playerArg = ArgumentType.Word("player").setDefaultValue("");
        playerArg.setSuggestionCallback((sender, context, suggestion) -> {
            if (sender instanceof CytosisPlayer player) {
                if (context.get(action).equalsIgnoreCase("add") ||
                        context.get(action).equalsIgnoreCase("decline") ||
                        context.get(action).equalsIgnoreCase("accept")) {

                    List<UUID> friends = Cytosis.getFriendManager().getFriends(player.getUuid());

                    for (String networkPlayer : Cytosis.getCytonicNetwork().getOnlinePlayers().getValues()) {
                        if (networkPlayer.equalsIgnoreCase(player.getUsername())) continue;
                        if (friends.contains(Cytosis.getCytonicNetwork().getOnlinePlayers().getByValue(networkPlayer)))
                            continue;
                        suggestion.addEntry(new SuggestionEntry(networkPlayer));
                    }
                }

                if (context.get(action).equalsIgnoreCase("remove")) {
                    for (UUID friend : Cytosis.getFriendManager().getFriends(player.getUuid())) {
                        suggestion.addEntry(new SuggestionEntry(Cytosis.getCytonicNetwork().getLifetimePlayers().getByKey(friend)));
                    }
                }
            }
        });


        addSyntax((sender, context) -> {
            if (!(sender instanceof CytosisPlayer player)) {
                sender.sendMessage(Msg.whoops("You must be a player to use this command!"));
                return;
            }
            // actions that don't require a UUID:
            String actionStr = context.get(action).toLowerCase(Locale.ROOT);
            if (actionStr.equals("list")) {
                Cytosis.getFriendManager().listFriends(player);
                return;
            }

            if (context.get(playerArg).isEmpty()) {
                player.sendMessage(Msg.whoops("Please specify a player to use the subcommand '" + context.get(action) + "'!"));
                return;
            }

            UUID target;
            String name;
            try {
                target = UUID.fromString(context.get(playerArg));
                name = Cytosis.getCytonicNetwork().getLifetimePlayers().getByKey(target);
            } catch (IllegalArgumentException e) {
                target = Cytosis.getCytonicNetwork().getLifetimeFlattened().getByValue(context.get(playerArg).toLowerCase());
                name = context.get(playerArg);
            }

            if (target == null) {
                player.sendMessage(Msg.whoops("The player '" + context.get(playerArg) + "' does not exist!"));
                return;
            }

            if (target.equals(player.getUuid())) {
                player.sendMessage(Msg.whoops("You cannot " + context.get(action) + " yourself!"));
                return;
            }

            Component targetComp = Cytosis.getCytonicNetwork().getCachedPlayerRanks().get(target).getPrefix().append(Component.text(name));

            switch (context.get(action).toLowerCase(Locale.ROOT)) {
                case "add" -> {
                    // check to see if they are online
                    if (!Cytosis.getCytonicNetwork().getOnlinePlayers().containsKey(target)) {
                        player.sendMessage(Msg.whoops("The player ").append(targetComp).append(Msg.mm("<gray> is not online!")));
                        return;
                    }
                    if (!Cytosis.getPreferenceManager().getPlayerPreference(target, CytosisPreferences.ACCEPT_FRIEND_REQUESTS)) {
                        player.sendMessage(Msg.whoops("").append(targetComp).append(Msg.mm("<gray> is not accepting friend requests!")));
                        return;
                    }
                    if (Cytosis.getFriendManager().getFriends(player.getUuid()).contains(target)) {
                        player.sendMessage(Msg.whoops("You are already friends with ").append(targetComp).append(Msg.mm("<gray>!")));
                        return;
                    }

                    player.sendFriendRequest(target);
                }
                case "remove" -> {
                    if (Cytosis.getFriendManager().getFriends(player.getUuid()).contains(target)) {
                        Cytosis.getFriendManager().removeFriend(player.getUuid(), target);
                    } else {
                        player.sendMessage(Msg.whoops("You are not friends with ").append(targetComp).append(Msg.mm("<gray>!")));
                    }
                }
                case "accept" -> player.acceptFriendRequest(target);
                case "decline" -> player.declineFriendRequest(target);
            }

        }, action, playerArg);
    }
}

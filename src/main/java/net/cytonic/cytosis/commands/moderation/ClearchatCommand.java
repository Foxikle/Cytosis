package net.cytonic.cytosis.commands.moderation;

import net.cytonic.cytosis.Cytosis;
import net.cytonic.cytosis.commands.CommandUtils;
import net.cytonic.cytosis.config.CytosisSnoops;
import net.cytonic.cytosis.player.CytosisPlayer;
import net.cytonic.cytosis.utils.Msg;
import net.cytonic.cytosis.utils.SnoopUtils;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;

/**
 * The class representing the clearchat command
 */
public class ClearchatCommand extends Command {

    /**
     * Creates a new command and sets up the consumers and execution logic
     */
    public ClearchatCommand() {
        super("clearchat", "cc");
        setCondition(CommandUtils.IS_MODERATOR);
        setDefaultExecutor((sender, ignored) -> {
            if (sender instanceof CytosisPlayer player) {
                for (CytosisPlayer online : Cytosis.getOnlinePlayers()) {
                    if (online.isStaff()) {
                        // don't actually clear the chat
                        online.sendMessage(Msg.mm("<green>Chat has been cleared by ").append(player.formattedName()).append(Msg.mm("<green>!")));
                    } else {
                        // todo: use the ClearChatPacket, but minestom doesn't support it
                        for (int i = 0; i < 250; i++) {
                            online.sendMessage("");
                        }
                    }
                }
                Component snoop = player.formattedName().append(Msg.mm("<gray> cleared the chat in server " + Cytosis.SERVER_ID + "."));
                Cytosis.getSnooperManager().sendSnoop(CytosisSnoops.CHAT_CLEAR, SnoopUtils.toSnoop(snoop));
            } else {
                sender.sendMessage(Msg.mm("<red>Only players may execute this command :("));
            }
        });
    }
}
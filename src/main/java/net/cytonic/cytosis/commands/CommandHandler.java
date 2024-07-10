package net.cytonic.cytosis.commands;

import net.cytonic.cytosis.Cytosis;
import net.cytonic.cytosis.commands.defaultMinecraft.GamemodeCommand;
import net.cytonic.cytosis.commands.defaultMinecraft.TeleportCommand;
import net.cytonic.cytosis.commands.friends.*;
import net.cytonic.cytosis.commands.moderation.BanCommand;
import net.cytonic.cytosis.commands.server.*;
import net.cytonic.cytosis.logging.Logger;
import net.minestom.server.command.CommandManager;
import net.minestom.server.entity.Player;

import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A class that handles the commands, their execution, and allegedly a console.
 */
public class CommandHandler {

    private final ScheduledExecutorService worker;
    private final Scanner CONSOLE_IN = new Scanner(System.in);
    private final Object consoleLock = new Object();

    /**
     * Creates a command handler and sets up the worker
     */
    public CommandHandler() {
        this.worker = Executors.newSingleThreadScheduledExecutor(Thread.ofVirtual().name("CytosisConsoleWorker").factory());
    }

    /**
     * Registers the default Cytosis commands
     */
    public void registerCytosisCommands() {
        CommandManager cm = Cytosis.getCommandManager();
        cm.register(new GamemodeCommand());
        cm.register(new RankCommand());
        cm.register(new BanCommand());
        cm.register(new ChatChannelCommand());
        cm.register(new StopCommand());
        cm.register(new ServerCommand());
        cm.register(new CreateInstanceCommand());
        cm.register(new ShutdownInstancesCommand());
        cm.register(new PodDetailsCommand());
        cm.register(new TeleportCommand());
        cm.register(new FindCommand());
        cm.register(new PreferenceCommand());
        cm.register(new ServerAlertsCommand());
        cm.register(new HelloCommand());
        cm.register(new FlyCommand());
        cm.register(new KaboomCommand());
        cm.register(new BroadcastCommand());
        cm.register(new HelpCommand());
        cm.register(new AllChatCommand());
        cm.register(new TimeCommand());
        cm.register(new PluginsCommand());
        cm.register(new VersionCommand());
        cm.register(new TeleportAllCommand());
        cm.register(new PingCommand());
        cm.register(new TPSCommand());
        cm.register(new ReportCommand());
        cm.register(new OPMeCommand());
        try {
            cm.register(new AcceptFriendCommand());
            cm.register(new AddFriendCommand());
            cm.register(new DeclineFriendCommand());
            cm.register(new ListFriendsCommand());
            cm.register(new RemoveFriendCommand());
            cm.register(new FriendCommand());
        } catch (Exception e) {
            Logger.error("An error occurred whilst registering Cytosis commands. Please report the following stacktrace to CytonicMC: ", e);
        }

        // friends

    }

    /**
     * Sends a packet to the player to recalculate command permissions
     *
     * @param player The player to send the packet to
     */
    @SuppressWarnings("UnstableApiUsage")
    public void recalculateCommands(Player player) {
        player.sendPacket(Cytosis.getCommandManager().createDeclareCommandsPacket(player));
    }

    /**
     * Sets up the console so commands can be executed from there
     */
    public void setupConsole() {
        worker.scheduleAtFixedRate(() -> {
            if (CONSOLE_IN.hasNext()) {
                String command = CONSOLE_IN.nextLine();

                synchronized (consoleLock) {
                    Cytosis.getCommandManager().getDispatcher().execute(Cytosis.getConsoleSender(), command);
                }
            }
        }, 50, 50, TimeUnit.MILLISECONDS);
    }
}
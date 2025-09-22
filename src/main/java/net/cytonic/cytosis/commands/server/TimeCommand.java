package net.cytonic.cytosis.commands.server;

import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;

import net.cytonic.cytosis.Cytosis;
import net.cytonic.cytosis.commands.utils.CommandUtils;
import net.cytonic.cytosis.commands.utils.CytosisCommand;
import net.cytonic.cytosis.utils.Msg;

/**
 * The class representing the time command
 */
public class TimeCommand extends CytosisCommand {

    /**
     * Creates a new command and sets up the consumers and execution logic
     */
    public TimeCommand() {
        super("time");
        setCondition(CommandUtils.IS_STAFF);
        ArgumentWord timeArgument = ArgumentType.Word("time")
            .from("day", "night", "noon", "midnight", "sunrise", "sunset", "freeze");
        ArgumentInteger timeInteger = ArgumentType.Integer("timeInteger");
        timeArgument.setSuggestionCallback((sender, cmdc, suggestion) -> {
            suggestion.addEntry(new SuggestionEntry("day"));
            suggestion.addEntry(new SuggestionEntry("night"));
            suggestion.addEntry(new SuggestionEntry("noon"));
            suggestion.addEntry(new SuggestionEntry("midnight"));
            suggestion.addEntry(new SuggestionEntry("sunrise"));
            suggestion.addEntry(new SuggestionEntry("sunset"));
            suggestion.addEntry(new SuggestionEntry("freeze"));
        });
        setDefaultExecutor((sender, cmdc) -> sender.sendMessage(Msg.red("Usage: /time (time)")));
        addSyntax((sender, context) -> {
            long timeToSet = Cytosis.getDefaultInstance().getTime();
            switch (context.get(timeArgument).toLowerCase()) {
                case "day" -> timeToSet = 1000L;
                case "night" -> timeToSet = 13000L;
                case "midnight" -> timeToSet = 18000L;
                case "noon" -> timeToSet = 6000L;
                case "sunrise" -> timeToSet = 23000L;
                case "sunset" -> timeToSet = 12000L;
                case "freeze" -> {
                    if (Cytosis.getDefaultInstance().getTimeRate() == 1) {
                        Cytosis.getDefaultInstance().setTimeRate(0);
                        sender.sendMessage(Msg.aqua("Time frozen."));
                    } else if (Cytosis.getDefaultInstance().getTimeRate() == 0) {
                        Cytosis.getDefaultInstance().setTimeRate(1);
                        sender.sendMessage(Msg.gold("Time unfrozen."));
                    }
                }
                default -> {
                    // won't ever happen
                }
            }
            Cytosis.getDefaultInstance().setTime(timeToSet);
        }, timeArgument);
        addSyntax((sender, context) -> {
            Cytosis.getDefaultInstance().setTime(context.get(timeInteger)); // Set time to input
            sender.sendMessage(Msg.green("Time set to " + context.get(timeInteger) + "."));
        }, timeInteger);
    }
}

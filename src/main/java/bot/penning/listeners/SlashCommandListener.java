package bot.penning.listeners;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import bot.penning.commmands.AddCommand;
import bot.penning.commmands.BattleCommand;
import bot.penning.commmands.GoalCommand;
import bot.penning.commmands.GreetCommand;
import bot.penning.commmands.HeyCommand;
import bot.penning.commmands.OnslaughtCommand;
import bot.penning.commmands.ProgressCommand;
import bot.penning.commmands.QuestCommand;
import bot.penning.commmands.SkirmishCommand;
import bot.penning.commmands.SlashCommand;
import bot.penning.commmands.TotalCommand;
import bot.penning.commmands.WarCommand;
import bot.penning.commmands.WritersBlockCommand;
import bot.penning.commmands.WritingPromptCommand;

public class SlashCommandListener {
    //An array list of classes that implement the SlashCommand interface
    private final static List<SlashCommand> commands = new ArrayList<>();

    static {
        //We register our commands here when the class is initialized
//        commands.add(new PingCommand());
//    	commands.add(new GoalCommand());
    	commands.add(new AddCommand());
    	commands.add(new BattleCommand());
        commands.add(new GreetCommand());
        commands.add(new GoalCommand());
        commands.add(new HeyCommand());
        commands.add(new OnslaughtCommand());
        commands.add(new ProgressCommand());
        commands.add(new QuestCommand());
        commands.add(new SkirmishCommand());
        commands.add(new TotalCommand());
        commands.add(new WarCommand());
        commands.add(new WritingPromptCommand());
        commands.add(new WritersBlockCommand());
    }

    public static Mono<Void> handle(ChatInputInteractionEvent event) {
        // Convert our array list to a flux that we can iterate through
        return Flux.fromIterable(commands)
            //Filter out all commands that don't match the name of the command this event is for
            .filter(command -> command.getName().equals(event.getCommandName()))
            // Get the first (and only) item in the flux that matches our filter
            .next()
            //have our command class handle all the logic related to its specific command.
            .flatMap(command -> command.handle(event));
    }
}
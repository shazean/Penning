package bot.penning.listeners;

import java.util.ArrayList;
import java.util.List;

import bot.penning.buttons.JoinButton;
import bot.penning.buttons.PenningButton;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ButtonListener {
    //An array list of classes that implement the SlashCommand interface
    private final static List<PenningButton> buttons = new ArrayList<>();

    static {
        //We register our commands here when the class is initialized
//        commands.add(new PingCommand());
//    	commands.add(new GoalCommand());
    	buttons.add(new JoinButton());
        
    }

    public static Mono<Void> handle(ButtonInteractionEvent event) {
        // Convert our array list to a flux that we can iterate through
        return Flux.fromIterable(buttons)
            //Filter out all commands that don't match the name of the command this event is for
            .filter(button -> button.getName().equals(event.getCustomId()))
            // Get the first (and only) item in the flux that matches our filter
            .next()
            //have our command class handle all the logic related to its specific command.
            .flatMap(button -> button.handle(event));
    }
}
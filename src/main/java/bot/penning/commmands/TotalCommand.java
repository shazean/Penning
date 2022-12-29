package bot.penning.commmands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

public class TotalCommand implements SlashCommand {
	  @Override
	    public String getName() {
	        return "total";
	    }

	    @Override
	    public Mono<Void> handle(ChatInputInteractionEvent event) {

	        //Reply to the slash command, with the name the user supplied
	    	return event.reply("Total added!");
	    }
	}
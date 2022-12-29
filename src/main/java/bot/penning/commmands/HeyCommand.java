package bot.penning.commmands;

import java.util.Optional;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

public class HeyCommand implements SlashCommand {
	  @Override
	    public String getName() {
	        return "hey";
	    }

	    @Override
	    public Mono<Void> handle(ChatInputInteractionEvent event) {

	    	
	    	
	        //Reply to the slash command, with the name the user supplied
	    	return event.reply("Hello!");
	        //return event.replyEphemeral("Pong!");
//	        return  event.reply()
//	            .withEphemeral(true)
//	            .withContent("Hello, " + name);
	    }
	}
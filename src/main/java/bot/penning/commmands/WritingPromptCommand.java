package bot.penning.commmands;

import java.util.Random;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

public class WritingPromptCommand implements SlashCommand {
	  @Override
	    public String getName() {
	        return "writing_prompt";
	    }

	    @Override
	    public Mono<Void> handle(ChatInputInteractionEvent event) {

	    	Random rand = new Random(); 
	    	
	    	String writingPrompt = getWritingPrompt(rand.nextInt(50));
	    	
	        //Reply to the slash command, with the name the user supplied
	    	return event.reply(writingPrompt);
	        //return event.replyEphemeral("Pong!");
//	        return  event.reply()
//	            .withEphemeral(true)
//	            .withContent("Hello, " + name);
	    }
	    
	    
	    private String getWritingPrompt(int rand) {
	    	switch (rand) {
	    	case 0:
	    		return "Sample writing prompt #1";
	    	
	    	case 1:
	    		return "Sample writing prompt #2";
    		
	    	default:
	    		return "Sample writing prompt #3";
	    	
	    	
	    	}
	    }
	}
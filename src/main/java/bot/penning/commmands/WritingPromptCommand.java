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
	    	
	    	String writingPrompt = getWritingPrompt(rand.nextInt(5));
	    	
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
	    		return "Pick up the book nearest to you and flip to page 37.  Find the 5th word on the page, and incorporate that into your next scene.";
	    	
	    	case 1:
	    		return "Your protagonist comes up against the thing they fear the most.";

	    	case 2:
	    		return "Something catches fire.";
	    		
	    	case 3:
	    		return "Something blows up.";

	    	case 4:
	    		return "Introduce a new character";

	    		
	    	default:
	    		return "Sample writing prompt #3";
	    	
	    	
	    	}
	    }
	}
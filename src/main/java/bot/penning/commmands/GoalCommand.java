package bot.penning.commmands;

import java.util.Optional;

import bot.penning.Goal;
import bot.penning.WarInfo;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

public class GoalCommand implements SlashCommand {
	
	  @Override
	    public String getName() {
	        return "goal";
	    }
	  
	  	

	    @Override
	    public Mono<Void> handle(ChatInputInteractionEvent event) {
	        /*
	        Since slash command options are optional according to discord, we will wrap it into the following function
	        that gets the value of our option as a String without chaining several .get() on all the optional values
	        In this case, there is no fear it will return empty/null as this is marked "required: true" in our json.
	         */
	        Long target = event.getOption("target")
	            .flatMap(ApplicationCommandInteractionOption::getValue)
	            .map(ApplicationCommandInteractionOptionValue::asLong)
	            .get(); //This is warning us that we didn't check if its present, we can ignore this on required options

	        String type = event.getOption("type")
		            .flatMap(ApplicationCommandInteractionOption::getValue)
		            .map(ApplicationCommandInteractionOptionValue::asString)
		            .orElse("words");
//		            .get(); //This is warning us that we didn't check if its present, we can ignore this on required options
	        
//	        Mono<User> user = event.getOption("user")
//	        		.flatMap(ApplicationCommandInteractionOption::getValue)
//	        		.map(ApplicationCommandInteractionOptionValue::asUser)
//	        		.get();
	        
	        Optional<Member> user = event.getInteraction().getMember();
	        
			//If the writerIndex map contains an object with the key of the user who called the command,
			//then the object is completely cleared.
			//this makes it so creating a new goal replaces the last one, and a person can not have 2 goals at once.
	        if (WarInfo.writerIndex.containsKey(user)) {
				WarInfo.writerIndex.remove(user);
			}

	        if (type != null) { //type was specified FIXME?
 	        
				Goal writerGoal = new Goal(target, type);
//				event.getMessage().getChannel().block().createMessage("Goal of " + writerGoal.getGoal() + " " + writerGoal.getGoalType() + " set!").block();
	
				WarInfo.writerIndex.put(user, writerGoal);
	
//				System.out.println("Created goal: " + writerGoal.getGoal() + " " + writerGoal.getGoalType());
				return event.reply("Goal of " + writerGoal.getGoal() + " " + writerGoal.getGoalType() + " set!");
	
			}
			else { //type was not specified, only target was
				Goal writerGoal = new Goal(target);
//				event.getMessage().getChannel().block().createMessage("Goal of " + writerGoal.getGoal() + " " + writerGoal.getGoalType() + " set!").block();
	
				WarInfo.writerIndex.put(user, writerGoal);
				return event.reply("Goal of " + writerGoal.getGoal() + " " + writerGoal.getGoalType() + " set!");
	
			}
	
	        
	        
//	        return event.reply("Goal of " + target + " set!");
	        
	        //Reply to the slash command, with the name the user supplied
//	        return event.replyEphemeral("Hello");
//	        return  event.reply()
//	            .withEphemeral(true)
//	            .withContent("Hello, " + name);
	    }
	

	
	
}

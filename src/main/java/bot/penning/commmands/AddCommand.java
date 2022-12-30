package bot.penning.commmands;

import java.util.Optional;

import bot.penning.Goal;
import bot.penning.EncounterInfo;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.Member;
import reactor.core.publisher.Mono;

public class AddCommand implements SlashCommand {

	@Override
	public String getName() {
		return "add";
	}

	@Override
	public Mono<Void> handle(ChatInputInteractionEvent event) {

		Long words = event.getOption("words")
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asLong)
				.get(); //This is warning us that we didn't check if its present, we can ignore this on required options

		Optional<Member> user = event.getInteraction().getMember();

		if (!EncounterInfo.writerIndex.containsKey(user)) { //if user hasn't created goal, alert them
			return event.reply("Create a goal first!").withEphemeral(true);
		}	        

		EncounterInfo.writerIndex.get(user).addWords(words);

		return event.reply("Progress updated! You have written " + EncounterInfo.writerIndex.get(user).getProgress() + " " + EncounterInfo.writerIndex.get(user).getGoalType() + " of " + EncounterInfo.writerIndex.get(user).getGoal() + " " + EncounterInfo.writerIndex.get(user).getGoalType() + ".");
	}



}

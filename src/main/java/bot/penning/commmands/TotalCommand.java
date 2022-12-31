package bot.penning.commmands;

import java.util.Optional;

import bot.penning.EncounterInfo;
import bot.penning.encounters.Encounter;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.Member;
import reactor.core.publisher.Mono;

public class TotalCommand implements SlashCommand {
	@Override
	public String getName() {
		return "total";
	}

	@Override
	public Mono<Void> handle(ChatInputInteractionEvent event) {

		Long ID = event.getOption("id")
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asLong)
				.get(); //This is warning us that we didn't check if its present, we can ignore this on required options

		Long totalWritten = event.getOption("total")
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asLong)
				.get(); //This is warning us that we didn't check if its present, we can ignore this on required options

		String type = event.getOption("type")
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asString)
				.orElse("words");
		String goalType;
		String goalTypeAbbr;

		Optional<Member> user = event.getInteraction().getMember();
		
		if (EncounterInfo.warRegistry.get(ID % 50) == null) return event.reply("This encounter is invalid! Try again with a valid encounter ID.").withEphemeral(true);

		Encounter currentEncounter = EncounterInfo.warRegistry.get(ID % 50);
		Long length = currentEncounter.getLength();
		Double wordsPerMin = (double) (totalWritten / length);
		
		if (EncounterInfo.writerIndex.containsKey(user)) {
			goalType = EncounterInfo.writerIndex.get(user).getGoalType();
			goalTypeAbbr = EncounterInfo.writerIndex.get(user).getGoalTypeAbbr();
		} else {
			goalType = "words";
			goalTypeAbbr = "wpm";
		}
		
		currentEncounter.createParticipant(user.get().getDisplayName(), totalWritten, wordsPerMin, goalType, goalTypeAbbr);
		
		if (!currentEncounter.isComplete()) return event.reply("This encounter is incomplete! Try again after it has finished.").withEphemeral(true);
		if (currentEncounter.isExpired()) return event.reply("This encounter is invalid! Try again with a valid encounter ID.").withEphemeral(true);

		//check if user has an established goal and update it if so
		if (EncounterInfo.writerIndex.containsKey(user)) {
			EncounterInfo.writerIndex.get(user).addWords(totalWritten);

			return event.reply("You have written " + totalWritten + " words for an average of " + wordsPerMin + " wpm.")
					.then(event.createFollowup("Progress updated! You have written " + EncounterInfo.writerIndex.get(user).getProgress() + " " + EncounterInfo.writerIndex.get(user).getGoalType() + " of " + EncounterInfo.writerIndex.get(user).getGoal() + " " + EncounterInfo.writerIndex.get(user).getGoalType() + ".").then());
		} else {
			return event.reply("You have written " + totalWritten + " words for an average of " + wordsPerMin + " wpm.");
		}

	}

}
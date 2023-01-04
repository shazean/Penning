package bot.penning.commmands;

import java.util.Optional;

import bot.penning.EncounterInfo;
import bot.penning.Writer;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.Member;
import reactor.core.publisher.Mono;

public class ProgressCommand implements SlashCommand {

	@Override
	public String getName() {
		return "progress";
	}

	@Override
	public Mono<Void> handle(ChatInputInteractionEvent event) {

		Long words = event.getOption("words")
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asLong)
				.get(); //This is warning us that we didn't check if its present, we can ignore this on required options

		Optional<Member> user = event.getInteraction().getMember();
		Writer writer = EncounterInfo.writerIndex.get(user);

		if (!EncounterInfo.writerIndex.containsKey(user)) { //if user hasn't created goal, alert them
			return event.reply("Create a goal first!").withEphemeral(true);
		}	        

		writer.getGoal().setProgress(words);
		writer.updateQuests(writer.getGoalNum() - words);

		if (writer.getQuest().getQuestGoal().isComplete()) {
			return event.reply("Progress updated! You have written " + EncounterInfo.writerIndex.get(user).getGoal().getProgress() + " " + EncounterInfo.writerIndex.get(user).getGoal().getGoalType() + " of " + EncounterInfo.writerIndex.get(user).getGoalNum() + " " + EncounterInfo.writerIndex.get(user).getGoal().getGoalType() + ".").then(event.createFollowup("Quest completed!").then());

		} else {
			return event.reply("Progress updated! You have written " + EncounterInfo.writerIndex.get(user).getGoal().getProgress() + " " + EncounterInfo.writerIndex.get(user).getGoal().getGoalType() + " of " + EncounterInfo.writerIndex.get(user).getGoalNum() + " " + EncounterInfo.writerIndex.get(user).getGoal().getGoalType() + ".");
		}
	}
}

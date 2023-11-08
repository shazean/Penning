package bot.penning.commmands;

import java.util.Optional;

import bot.penning.Goal;
import bot.penning.Writer;
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

		Member user = event.getInteraction().getMember().get();
		Writer writer = EncounterInfo.writerIndex.get(user);

		if (writer == null) {
			return event.reply("Create a goal first!").withEphemeral(true);
		}
		
		if (!writer.hasGoalSet()) {
			return event.reply("Create a goal first!").withEphemeral(true);
		}

		writer.getGoal().addWords(words);
		
		if (writer.hasQuest()) {
			writer.updateQuests(words);
		}
		
		if (writer.hasChallengeQuest() && !writer.getChallengeQuest().isTimed()) {
			writer.updateChallengeQuests(false, words);
		}

		if (writer.hasQuest() && writer.getQuest().getQuestGoal().isComplete()) {
			//quest is complete
			return event.reply("Progress updated! You have written " + writer.getGoal().getProgress() + " " + writer.getGoal().getGoalType() + " out of " + writer.getGoalNum() + " " + writer.getGoal().getGoalType() + ".")
					.then(event.createFollowup("Quest completed!").then());

		} else if (writer.hasChallengeQuest() && writer.getChallengeQuest().getQuestGoal().isComplete()) {
			//challenge quest is complete
			return event.reply("Progress updated! You have written " + writer.getGoal().getProgress() + " " + writer.getGoal().getGoalType() + " out of " + writer.getGoalNum() + " " + writer.getGoal().getGoalType() + ".")
					.then(event.createFollowup("Challenge quest completed!").then());
			
		} else if (writer.hasQuest() && writer.getQuest().getQuestGoal().isComplete() && writer.hasChallengeQuest() && writer.getChallengeQuest().getQuestGoal().isComplete()) {
			//quest and challenge quest complete
			return event.reply("Progress updated! You have written " + writer.getGoal().getProgress() + " " + writer.getGoal().getGoalType() + " out of " + writer.getGoalNum() + " " + writer.getGoal().getGoalType() + ".")
					.then(event.createFollowup("Quest completed!")
					.then(event.createFollowup("Challenge quest completed!").then()));
		}
		else {
			//no quests or they aren't complete
			return event.reply("Progress updated! You have written " + writer.getGoal().getProgress() + " " + writer.getGoal().getGoalType() + " out of " + writer.getGoalNum() + " " + writer.getGoal().getGoalType() + ".");
		}
	}
}

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

		Optional<Member> user = event.getInteraction().getMember();
		Writer writer = EncounterInfo.writerIndex.get(user);

		if (writer == null || !writer.hasGoalSet()) { //if user hasn't created a goal, alert them
			return event.reply("Create a goal first!").withEphemeral(true);
		}	        

		writer.getGoal().addWords(words);
		

		
		
		if (writer.hasQuest()) {
			writer.updateQuests(words);
		}

		if (writer.hasQuest() && writer.getQuest().getQuestGoal().isComplete()) {
			System.out.println("WRITER QUEST: " + writer.hasQuest() + " " + writer.getQuest().getQuestGoal().isComplete());
			System.out.println("WRITER QUEST: " + writer.hasQuest() + " " + writer.getQuest().getQuestGoal().isComplete());
			System.out.println("WRITER QUEST: " + writer.hasQuest() + " " + writer.getQuest().getQuestGoal().isComplete());
			System.out.println("WRITER QUEST: " + writer.hasQuest() + " " + writer.getQuest().getQuestGoal().isComplete());
			System.out.println("WRITER QUEST: " + writer.hasQuest() + " " + writer.getQuest().getQuestGoal().isComplete());
			return event.reply("Progress updated! You have written " + writer.getGoal().getProgress() + " " + writer.getGoal().getGoalType() + " out of " + writer.getGoalNum() + " " + writer.getGoal().getGoalType() + ".").then(event.createFollowup("Quest completed!").then());

		} else {
			return event.reply("Progress updated! You have written " + writer.getGoal().getProgress() + " " + writer.getGoal().getGoalType() + " out of " + writer.getGoalNum() + " " + writer.getGoal().getGoalType() + ".");
		}
	}
}

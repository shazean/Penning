package bot.penning.commmands;

import java.util.Optional;

import bot.penning.EncounterInfo;
import bot.penning.Writer;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Member;
import reactor.core.publisher.Mono;

public class InfoCommand implements SlashCommand {
	@Override
	public String getName() {
		return "info";
	}

	@Override
	public Mono<Void> handle(ChatInputInteractionEvent event) {

		Member user = event.getInteraction().getMember().get();
		Writer writer = EncounterInfo.writerIndex.get(user);
		String goalInfo = "Current goal: NONE";
		String questInfo = "Current quest: NONE";

		if (writer == null) {
			return event.reply(goalInfo + "\n" + questInfo);
		}

		if (writer.hasGoalSet()) {
			goalInfo = "Current goal: " + writer.getGoal().getProgress() + " " + writer.getGoal().getGoalType() + " of " + writer.getGoalNum() + " " + writer.getGoal().getGoalType();
		}
		if (writer.hasQuest()) {
			questInfo = "Current quest: " + writer.getQuest().getQuestGoal().getProgress() + " words of " + writer.getQuest().getQuestGoal().getGoal();
		}

		return event.reply(goalInfo + "\n" + questInfo);
	}
}
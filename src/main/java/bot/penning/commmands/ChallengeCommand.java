package bot.penning.commmands;

import java.util.Optional;

import bot.penning.EncounterInfo;
import bot.penning.Writer;
import bot.penning.quests.ChallengeQuest;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Member;
import reactor.core.publisher.Mono;

public class ChallengeCommand implements SlashCommand {
	@Override
	public String getName() {
		return "challenge_quest";
	}

	@Override
	public Mono<Void> handle(ChatInputInteractionEvent event) {

		Member user = event.getInteraction().getMember().get();
		Writer writer = EncounterInfo.writerIndex.get(user);
		Writer newWriter;
		ChallengeQuest quest;

		if (writer == null) { //if user has never interacted with the bot before
			newWriter = new Writer(user);
			EncounterInfo.writerIndex.put(user, newWriter);
			writer = EncounterInfo.writerIndex.get(user);
		}

		if (!writer.hasGoalSet()) { //user has no specified goal
			quest = new ChallengeQuest();
			quest.generateQuest();
		} else { //user has a goal, so we can generate a quest based off of that
			quest = new ChallengeQuest();
			quest.generateQuest(writer.getGoal());
		}

		writer.addChallengeQuest(quest);

		return event.reply(quest.toString());
	}

}
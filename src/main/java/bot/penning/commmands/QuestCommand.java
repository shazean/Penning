package bot.penning.commmands;

import java.util.Optional;

import java.util.Random;

import bot.penning.EncounterInfo;
import bot.penning.Goal;
import bot.penning.Writer;
import bot.penning.quests.GenericQuest;
import bot.penning.quests.Quest;
import bot.penning.quests.GoalQuest;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.Member;
import reactor.core.publisher.Mono;

public class QuestCommand implements SlashCommand {
	@Override
	public String getName() {
		return "quest";
	}

	@Override
	public Mono<Void> handle(ChatInputInteractionEvent event) {

		Member user = event.getInteraction().getMember().get();
		Writer writer = EncounterInfo.writerIndex.get(user);
		Writer newWriter;
		Quest quest;

		if (writer == null) { //if user has never interacted with the bot before
			newWriter = new Writer(user);
			EncounterInfo.writerIndex.put(user, newWriter);
			writer = EncounterInfo.writerIndex.get(user);
		}

		if (!writer.hasGoalSet()) { //user has no specified goal
			quest = new GenericQuest();
			quest.generateQuest();
		} else { //user has a goal, so we can generate a quest based off of that
			quest = new GoalQuest();
			quest.generateQuest(writer.getGoal());
		}

		writer.addQuest(quest);
		System.out.println("QUEST GOAL: " + quest.getQuestGoal().getGoal());

		return event.reply(quest.toString());
	}

}
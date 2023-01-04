package bot.penning.commmands;

import java.util.Optional;
import java.util.Random;

import bot.penning.EncounterInfo;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Member;
import reactor.core.publisher.Mono;

public class QuestCommand implements SlashCommand {
	@Override
	public String getName() {
		return "quest";
	}

	@Override
	public Mono<Void> handle(ChatInputInteractionEvent event) {
		//TODO
		//get user's statistics/info
		//suggest a quest based off how far along in their goal they are, what their average WPM is, etc.
		//if user info is sparse/null, suggest some sample quests
		
		
		Optional<Member> user = event.getInteraction().getMember();


		if (EncounterInfo.writerIndex.containsKey(user)) {
			//TODO
		}
		
		Random rand = new Random(); 
		String quest = getSampleQuest(rand.nextInt(50));

		return event.reply(quest);
	}


	private String getSampleQuest(int rand) {
		switch (rand) {
		case 0:
			return "Sample quest #1";

		case 1:
			return "Sample quest #2";

		default:
			return "Sample quest #3";


		}
	}
}
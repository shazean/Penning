package bot.penning.commmands;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import bot.penning.EncounterInfo;
import bot.penning.Goal;
import bot.penning.Writer;
import bot.penning.encounters.Encounter;
import bot.penning.encounters.Encounter.Participant;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.MessageChannel;
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
				.get();

		Long totalWritten = event.getOption("total")
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asLong)
				.get();

		String type = event.getOption("type")
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asString)
				.orElse("words"); //assume if writer didn't specify type, then they're writing words

		String goalType;
		String goalTypeAbbr;

		Encounter currentEncounter = EncounterInfo.encounterRegistry.get(ID % 50);

		
		//can only use valid War ID
		if (currentEncounter == null) return event.reply("This encounter is invalid! Try again with a valid encounter ID.").withEphemeral(true);
		
		Member user = event.getInteraction().getMember().get();
		Writer writer = EncounterInfo.writerIndex.get(user);

		
		if (writer == null) {
			writer = new Writer(user);
			EncounterInfo.writerIndex.put(user, writer);
		}
		

		
		
		Long length = currentEncounter.getLength();
		Double wordsPerMin = Math.round((totalWritten / (double) length) * 100.0) / 100.0;
		
		goalType = type;
//		if (goalType.toUpperCase().equals("LINES")) {
//			goalTypeAbbr = "lpm";
//		} else if (goalType.toUpperCase().equals("MINUTES")) {
//			goalTypeAbbr = "minutes";
//		} else if (goalType.toUpperCase().equals("PAGES")) {
//			goalTypeAbbr = "ppm";
//		} else if (goalType.toUpperCase().equals("PERIWINKLES")) {
//			goalTypeAbbr = "periwinkles per minute";
//		} else if (goalType.toUpperCase().equals("MEASURES")) {
//			goalTypeAbbr = "measures per minute";
//		} else {
//			goalTypeAbbr = "wpm";
//		}
		
		goalTypeAbbr = new Goal(1L, goalType).getGoalTypeAbbr();
				
		//can only use a completed, & non-expired War ID
		if (!currentEncounter.isComplete()) return event.reply("This encounter is incomplete! Try again after it has finished.").withEphemeral(true);
		if (currentEncounter.isExpired()) return event.reply("This encounter is invalid! Try again with a valid encounter ID.").withEphemeral(true);

		writer.updateAverageWPM(wordsPerMin);
		
		if (writer.hasGoalSet() && writer.getGoal().getGoalType() == type) {
			writer.getGoal().addWords(totalWritten);
		}

		if (currentEncounter.hasParticipantAlready(user)) { return event.reply("You already entered a total for this encounter!").withEphemeral(true); }
		
		currentEncounter.createParticipant(user, totalWritten, wordsPerMin, goalType, goalTypeAbbr);

		if (currentEncounter.getIsWar()) {
			EncounterInfo.addToWarSummary(user, totalWritten, wordsPerMin, goalType);
		}
		
		Random rand = new Random();
		
//		if (rand.nextInt(100) < 45) {
			String animal = writer.getAnimalData().generateRandomAnimal();
			ScheduledExecutorService schedule = Executors.newScheduledThreadPool(3);
			GatewayDiscordClient client = event.getClient();
			Snowflake channelID = event.getInteraction().getChannelId();
			String nickname = writer.getUser().getMention();


			schedule.schedule(() -> {

				client.getChannelById(channelID).ofType(MessageChannel.class).flatMap(channel -> channel.createMessage(nickname + " You have found a " + animal + "!"))
				.subscribe();
			
			}, 3, TimeUnit.SECONDS);	
//		}
		
		
		/* NOTE: to whoever looks at this code, including future me:
		 * I originally had this all in a bunch of nested if/else-if/else statements,
		 * looking one at a time at if the writer had a goal, had a quest, if the quest was complete,
		 * had a challenge quest, if the challenge quest was complete, etc.,
		 * but even before checking if the quests are complete, that's 8 possible outcomes,
		 * and the if/else nest was gonna get ugly really fast.
		 * So I decided to try this instead.
		 * We now individually check for goal, quest, and challenge quests (and if the latter two are completed)
		 * and anything that comes back true assigns a specific value into the array whichToDo.
		 * All of said values are specific numbers that have a unique sum no matter how they're added up.
		 * Then we run the sum through a switch statement for each unique option.
		 * It's still lengthy, but I think it's easier to follow.*/
		
		int[] whichToDo = new int[] {0,0,0,0,0}; //false values: 0,0,0,0,0 | true values: 1,2,5,11,21
		//[0] = goal, [1] = quest, [2] = quest completed, [3] = challenge quest, [4] = challenge quest completed

		if (writer.hasGoalSet() && writer.getGoal().getGoalType() == type) whichToDo[0] = 1; //has goal and it's a relevant goal to the war
		
		if (writer.hasQuest()) {
			whichToDo[1] = 2;
			writer.updateQuests(totalWritten);
			if (writer.getQuest().getQuestGoal().isComplete()) {
				whichToDo[3] = 11;
			}
		}
		
		if (writer.hasChallengeQuest()) {
			whichToDo[2] = 5;
			if (writer.getChallengeQuest().isTimed() && writer.getChallengeQuest().getTimeLimit() <= currentEncounter.getLength()) { //current encounter is long enough to qualify for timed quest
				writer.updateChallengeQuests(true, totalWritten);
				if (writer.getChallengeQuest().getQuestGoal().isComplete()) whichToDo[4] = 21;
			} else if (!writer.getChallengeQuest().isTimed()) { //challenge isn't timed
				writer.updateChallengeQuests(false, totalWritten);
				if (writer.getChallengeQuest().getQuestGoal().isComplete()) whichToDo[4] = 21;
			}
		}
		int totalToDo = whichToDo[0] + whichToDo[1] + whichToDo[2] + whichToDo[3] + whichToDo[4];
		
		switch (totalToDo) {
		case(0): //no goal, no quest, no challenge quest
			return event.reply("You have written " + totalWritten + " " + goalType + " for an average of " + wordsPerMin + " " + goalTypeAbbr + ".");
		case(1): //has goal, no quest, no challenge quest
			return event.reply("You have written " + totalWritten + " " + goalType + " for an average of " + wordsPerMin + " " + goalTypeAbbr + ".")
					.then(event.createFollowup("Progress updated! You have written " + writer.getGoal().getProgress() + " " + goalType + " of " + writer.getGoalNum() + " " + goalType + ".").then());
		case(2): //no goal, has incomplete quest, no challenge quest
			return event.reply("You have written " + totalWritten + "  " + goalType + " for an average of " + wordsPerMin + " " + goalTypeAbbr + ".");
		case(3): //has goal, has incomplete quest, no challenge quest
			return event.reply("You have written " + totalWritten + "  " + goalType + " for an average of " + wordsPerMin + " " + goalTypeAbbr + ".")
					.then(event.createFollowup("Progress updated! You have written " + writer.getGoal().getProgress() + " " + goalType + " of " + writer.getGoalNum() + " " + goalType + ".").then());
		case(5): //no goal, no quest, has incomplete challenge quest
			return event.reply("You have written " + totalWritten + "  " + goalType + " for an average of " + wordsPerMin + " " + goalTypeAbbr + ".");
		case(6): //has goal, no quest, has incomplete challenge quest
			return event.reply("You have written " + totalWritten + "  " + goalType + " for an average of " + wordsPerMin + " " + goalTypeAbbr + ".")
					.then(event.createFollowup("Progress updated! You have written " + writer.getGoal().getProgress() + " " + goalType + " of " + writer.getGoalNum() + " " + goalType + ".").then());
		case(7): //no goal, has incomplete quest, has incomplete challenge quest
			return event.reply("You have written " + totalWritten + "  " + goalType + " for an average of " + wordsPerMin + " " + goalTypeAbbr + ".");
		case(8): //has goal, has incomplete quest, has incomplete challenge quest	
			return event.reply("You have written " + totalWritten + "  " + goalType + " for an average of " + wordsPerMin + " " + goalTypeAbbr + ".")
					.then(event.createFollowup("Progress updated! You have written " + writer.getGoal().getProgress() + " " + goalType + " of " + writer.getGoalNum() + " " + goalType + ".").then());
		case(13): //no goal, has complete quest, no challenge quest
			return event.reply("You have written " + totalWritten + "  " + goalType + " for an average of " + wordsPerMin + " " + goalTypeAbbr + ".")
					.then(event.createFollowup("Quest completed!").then());
		case(14): //has goal, has complete quest, no challenge quest
			return event.reply("You have written " + totalWritten + "  " + goalType + " for an average of " + wordsPerMin + " " + goalTypeAbbr + ".")
					.then(event.createFollowup("Progress updated! You have written " + writer.getGoal().getProgress() + " " + goalType + " of " + writer.getGoalNum() + " " + goalType + ".").then())
					.then(event.createFollowup("Quest completed!").then());
		case(16): //has goal, has complete quest, has incomplete challenge
			return event.reply("You have written " + totalWritten + "  " + goalType + " for an average of " + wordsPerMin + " " + goalTypeAbbr + ".")
					.then(event.createFollowup("Progress updated! You have written " + writer.getGoal().getProgress() + " " + goalType + " of " + writer.getGoalNum() + " " + goalType + ".").then())
					.then(event.createFollowup("Quest completed!").then());
		case(18): //no goal, has complete quest, has incomplete challenge
			return event.reply("You have written " + totalWritten + "  " + goalType + " for an average of " + wordsPerMin + " " + goalTypeAbbr + ".")
					.then(event.createFollowup("Quest completed!").then());
		case(27): //has goal, no quest, has complete challenge
			return event.reply("You have written " + totalWritten + "  " + goalType + " for an average of " + wordsPerMin + " " + goalTypeAbbr + ".")
					.then(event.createFollowup("Progress updated! You have written " + writer.getGoal().getProgress() + " " + goalType + " of " + writer.getGoalNum() + " " + goalType + ".").then())
					.then(event.createFollowup("Challenge quest completed!").then());
		case(28): //no goal, has incomplete quest, has complete challenge
			return event.reply("You have written " + totalWritten + "  " + goalType + " for an average of " + wordsPerMin + " " + goalTypeAbbr + ".")
					.then(event.createFollowup("Challenge quest completed!").then());
		case(29): //has goal, has incomplete quest, has complete challenge
			return event.reply("You have written " + totalWritten + "  " + goalType + " for an average of " + wordsPerMin + " " + goalTypeAbbr + ".")
					.then(event.createFollowup("Progress updated! You have written " + writer.getGoal().getProgress() + " " + goalType + " of " + writer.getGoalNum() + " " + goalType + ".").then())
					.then(event.createFollowup("Challenge quest completed!").then());
		case(39): //no goal, has complete quest, has complete challenge
			return event.reply("You have written " + totalWritten + "  " + goalType + " for an average of " + wordsPerMin + " " + goalTypeAbbr + ".")
					.then(event.createFollowup("Quest completed!").then())
					.then(event.createFollowup("Challenge quest completed!").then());
		case(40): //has goal, has complete quest, has complete challenge
			return event.reply("You have written " + totalWritten + "  " + goalType + " for an average of " + wordsPerMin + " " + goalTypeAbbr + ".")
					.then(event.createFollowup("Progress updated! You have written " + writer.getGoal().getProgress() + " " + writer.getGoal().getGoalType() + " of " + writer.getGoalNum() + " " + goalType + ".").then())
					.then(event.createFollowup("Quest completed!").then())
					.then(event.createFollowup("Challenge quest completed!").then());
		default: //assume no goal, no quest, no challenge quest
			return event.reply("You have written " + totalWritten + "  " + goalType + " for an average of " + wordsPerMin + " " + goalTypeAbbr + ".");
		}
	}
}
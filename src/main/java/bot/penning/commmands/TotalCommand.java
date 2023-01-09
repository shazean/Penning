package bot.penning.commmands;

import java.util.Optional;

import bot.penning.EncounterInfo;
import bot.penning.Writer;
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
		Writer writer = EncounterInfo.writerIndex.get(user);

		//can only use valid War ID
		if (EncounterInfo.warRegistry.get(ID % 50) == null) return event.reply("This encounter is invalid! Try again with a valid encounter ID.").withEphemeral(true);

		Encounter currentEncounter = EncounterInfo.warRegistry.get(ID % 50);
		Long length = currentEncounter.getLength();
		Double wordsPerMin = (double) (totalWritten / length);

		//get goal if writer has one, else assume they're writing in words
		if (EncounterInfo.writerIndex.containsKey(user)) {
			goalType = EncounterInfo.writerIndex.get(user).getGoal().getGoalType();
			goalTypeAbbr = EncounterInfo.writerIndex.get(user).getGoal().getGoalTypeAbbr();
		} else {
			goalType = "words";
			goalTypeAbbr = "wpm";
		}

		currentEncounter.createParticipant(user.get().getDisplayName(), totalWritten, wordsPerMin, goalType, goalTypeAbbr);

		//can only use a completed, & unexpired War ID
		if (!currentEncounter.isComplete()) return event.reply("This encounter is incomplete! Try again after it has finished.").withEphemeral(true);
		if (currentEncounter.isExpired()) return event.reply("This encounter is invalid! Try again with a valid encounter ID.").withEphemeral(true);

		//NOTE: to whoever looks at this code, including future me
		//I originally had this all in a bunch of nested if/else-if/else statements
		//looking individually at if the writer had a goal, had a quest, if the quest was complete,
		//has a challenge quest, and if the challenge quest was complete
		//but even before checking if the quests are complete, that's 8 possible outcomes
		//so I decided to try this instead
		//I individually check for goal, quest, and challenge quests (and if the latter two are completed)
		//and based off if it's true or false, assign a value into the array whichToDo
		//they're all specific numbers that no matter how they're added up, the sum is a unique number
		//then I can run the sum through a switch statement for each unique option
		int[] whichToDo = new int[] {0,0,0,0,0}; //false values: 0,0,0,0,0 | true values: 1,2,5,11,21
		Boolean hasGoal = writer.hasGoalSet();
		Boolean hasQuest = writer.hasQuest();
		Boolean hasCQuest = writer.hasChallengeQuest();

		if (hasGoal) whichToDo[0] = 1;
		if (hasQuest) {
			whichToDo[1] = 2;
			writer.updateQuests(totalWritten);
			if (writer.getQuest().getQuestGoal().isComplete()) {
				whichToDo[3] = 11;
			}
		}
		if (hasCQuest) {
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
		System.out.println("GOAL: " + hasGoal + " " + writer.getGoalNum() + " QUEST: " + hasQuest + " " + writer.getQuest().getQuestGoal().getGoal() + " CHALLENGE: " + hasCQuest + writer.getChallengeQuest().getQuestGoal().getGoal() + " timed: " + writer.getChallengeQuest().isTimed() + ", " + totalToDo);
		System.out.println("GOAL: " + hasGoal + " " + writer.getGoalNum() + " QUEST: " + hasQuest + " " + writer.getQuest().getQuestGoal().getGoal() + " CHALLENGE: " + hasCQuest + writer.getChallengeQuest().getQuestGoal().getGoal() + " timed: " + writer.getChallengeQuest().isTimed() + ", " + totalToDo);
		System.out.println("GOAL: " + hasGoal + " " + writer.getGoalNum() + " QUEST: " + hasQuest + " " + writer.getQuest().getQuestGoal().getGoal() + " CHALLENGE: " + hasCQuest + writer.getChallengeQuest().getQuestGoal().getGoal() + " timed: " + writer.getChallengeQuest().isTimed() + ", " + totalToDo);
		System.out.println("GOAL: " + hasGoal + " " + writer.getGoalNum() + " QUEST: " + hasQuest + " " + writer.getQuest().getQuestGoal().getGoal() + " CHALLENGE: " + hasCQuest + writer.getChallengeQuest().getQuestGoal().getGoal() + " timed: " + writer.getChallengeQuest().isTimed() + ", " + totalToDo);
		System.out.println("GOAL: " + hasGoal + " " + writer.getGoalNum() + " QUEST: " + hasQuest + " " + writer.getQuest().getQuestGoal().getGoal() + " CHALLENGE: " + hasCQuest + writer.getChallengeQuest().getQuestGoal().getGoal() + " timed: " + writer.getChallengeQuest().isTimed() + ", " + totalToDo);

		
		switch (totalToDo) {
		case(0): //no goal, no quest, no challenge quest
			return event.reply("You have written " + totalWritten + " words for an average of " + wordsPerMin + " wpm.");
		case(1): //has goal, no quest, no challenge quest
			return event.reply("You have written " + totalWritten + " words for an average of " + wordsPerMin + " wpm.")
					.then(event.createFollowup("Progress updated! You have written " + EncounterInfo.writerIndex.get(user).getGoal().getProgress() + " " + EncounterInfo.writerIndex.get(user).getGoal().getGoalType() + " of " + EncounterInfo.writerIndex.get(user).getGoalNum() + " " + EncounterInfo.writerIndex.get(user).getGoal().getGoalType() + ".").then());
		case(2): //no goal, has incomplete quest, no challenge quest
			return event.reply("You have written " + totalWritten + " words for an average of " + wordsPerMin + " wpm.");
		case(3): //has goal, has incomplete quest, no challenge quest
			return event.reply("You have written " + totalWritten + " words for an average of " + wordsPerMin + " wpm.")
					.then(event.createFollowup("Progress updated! You have written " + EncounterInfo.writerIndex.get(user).getGoal().getProgress() + " " + EncounterInfo.writerIndex.get(user).getGoal().getGoalType() + " of " + EncounterInfo.writerIndex.get(user).getGoalNum() + " " + EncounterInfo.writerIndex.get(user).getGoal().getGoalType() + ".").then());
		case(5): //no goal, no quest, has incomplete challenge quest
			return event.reply("You have written " + totalWritten + " words for an average of " + wordsPerMin + " wpm.");
		case(6): //has goal, no quest, has incomplete challenge quest
			return event.reply("You have written " + totalWritten + " words for an average of " + wordsPerMin + " wpm.")
					.then(event.createFollowup("Progress updated! You have written " + EncounterInfo.writerIndex.get(user).getGoal().getProgress() + " " + EncounterInfo.writerIndex.get(user).getGoal().getGoalType() + " of " + EncounterInfo.writerIndex.get(user).getGoalNum() + " " + EncounterInfo.writerIndex.get(user).getGoal().getGoalType() + ".").then());
		case(7): //no goal, has incomplete quest, has incomplete challenge quest
			return event.reply("You have written " + totalWritten + " words for an average of " + wordsPerMin + " wpm.");
		case(8): //has goal, has incomplete quest, has incomplete challenge quest	
			return event.reply("You have written " + totalWritten + " words for an average of " + wordsPerMin + " wpm.")
					.then(event.createFollowup("Progress updated! You have written " + EncounterInfo.writerIndex.get(user).getGoal().getProgress() + " " + EncounterInfo.writerIndex.get(user).getGoal().getGoalType() + " of " + EncounterInfo.writerIndex.get(user).getGoalNum() + " " + EncounterInfo.writerIndex.get(user).getGoal().getGoalType() + ".").then());
		case(13): //no goal, has complete quest, no challenge quest
			return event.reply("You have written " + totalWritten + " words for an average of " + wordsPerMin + " wpm.")
					.then(event.createFollowup("Quest completed!").then());
		case(14): //has goal, has complete quest, no challenge quest
			return event.reply("You have written " + totalWritten + " words for an average of " + wordsPerMin + " wpm.")
					.then(event.createFollowup("Progress updated! You have written " + EncounterInfo.writerIndex.get(user).getGoal().getProgress() + " " + EncounterInfo.writerIndex.get(user).getGoal().getGoalType() + " of " + EncounterInfo.writerIndex.get(user).getGoalNum() + " " + EncounterInfo.writerIndex.get(user).getGoal().getGoalType() + ".").then())
					.then(event.createFollowup("Quest completed!").then());
		case(16): //has goal, has complete quest, has incomplete challenge
			return event.reply("You have written " + totalWritten + " words for an average of " + wordsPerMin + " wpm.")
					.then(event.createFollowup("Progress updated! You have written " + EncounterInfo.writerIndex.get(user).getGoal().getProgress() + " " + EncounterInfo.writerIndex.get(user).getGoal().getGoalType() + " of " + EncounterInfo.writerIndex.get(user).getGoalNum() + " " + EncounterInfo.writerIndex.get(user).getGoal().getGoalType() + ".").then())
					.then(event.createFollowup("Quest completed!").then());
		case(18): //no goal, has complete quest, has incomplete challenge
			return event.reply("You have written " + totalWritten + " words for an average of " + wordsPerMin + " wpm.")
					.then(event.createFollowup("Quest completed!").then());
		case(27): //has goal, no quest, has complete challenge
			return event.reply("You have written " + totalWritten + " words for an average of " + wordsPerMin + " wpm.")
					.then(event.createFollowup("Progress updated! You have written " + EncounterInfo.writerIndex.get(user).getGoal().getProgress() + " " + EncounterInfo.writerIndex.get(user).getGoal().getGoalType() + " of " + EncounterInfo.writerIndex.get(user).getGoalNum() + " " + EncounterInfo.writerIndex.get(user).getGoal().getGoalType() + ".").then())
					.then(event.createFollowup("Challenge quest completed!").then());
		case(28): //no goal, has incomplete quest, has complete challenge
			return event.reply("You have written " + totalWritten + " words for an average of " + wordsPerMin + " wpm.")
					.then(event.createFollowup("Challenge quest completed!").then());
		case(29): //has goal, has incomplete quest, has complete challenge
			return event.reply("You have written " + totalWritten + " words for an average of " + wordsPerMin + " wpm.")
					.then(event.createFollowup("Progress updated! You have written " + EncounterInfo.writerIndex.get(user).getGoal().getProgress() + " " + EncounterInfo.writerIndex.get(user).getGoal().getGoalType() + " of " + EncounterInfo.writerIndex.get(user).getGoalNum() + " " + EncounterInfo.writerIndex.get(user).getGoal().getGoalType() + ".").then())
					.then(event.createFollowup("Challenge quest completed!").then());
		case(39): //no goal, has complete quest, has complete challenge
			return event.reply("You have written " + totalWritten + " words for an average of " + wordsPerMin + " wpm.")
					.then(event.createFollowup("Quest completed!").then())
					.then(event.createFollowup("Challenge quest completed!").then());
		case(40): //has goal, has complete quest, has complete challenge
			return event.reply("You have written " + totalWritten + " words for an average of " + wordsPerMin + " wpm.")
					.then(event.createFollowup("Progress updated! You have written " + EncounterInfo.writerIndex.get(user).getGoal().getProgress() + " " + EncounterInfo.writerIndex.get(user).getGoal().getGoalType() + " of " + EncounterInfo.writerIndex.get(user).getGoalNum() + " " + EncounterInfo.writerIndex.get(user).getGoal().getGoalType() + ".").then())
					.then(event.createFollowup("Quest completed!").then())
					.then(event.createFollowup("Challenge quest completed!").then());
		default: //assume no goal, no quest, no challenge quest
			return event.reply("You have written " + totalWritten + " words for an average of " + wordsPerMin + " wpm.");
		}
	

//				if (writer.hasGoalSet()) { //writer has a goal
//					writer.getGoal().addWords(totalWritten);
//					if (writer.hasQuest()) { //writer also has a quest
//						writer.updateQuests(totalWritten);
//						if (writer.getQuest().getQuestGoal().isComplete()) {
//							//if writer has a goal, has a quest, and quest is also completed
//							return event.reply("You have written " + totalWritten + " words for an average of " + wordsPerMin + " wpm.")
//									.then(event.createFollowup("Progress updated! You have written " + EncounterInfo.writerIndex.get(user).getGoal().getProgress() + " " + EncounterInfo.writerIndex.get(user).getGoal().getGoalType() + " of " + EncounterInfo.writerIndex.get(user).getGoalNum() + " " + EncounterInfo.writerIndex.get(user).getGoal().getGoalType() + ".").then());
//						} else {
//							return event.reply("You have written " + totalWritten + " words for an average of " + wordsPerMin + " wpm.")
//									.then(event.createFollowup("Progress updated! You have written " + EncounterInfo.writerIndex.get(user).getGoal().getProgress() + " " + EncounterInfo.writerIndex.get(user).getGoal().getGoalType() + " of " + EncounterInfo.writerIndex.get(user).getGoalNum() + " " + EncounterInfo.writerIndex.get(user).getGoal().getGoalType() + ".").then());
//						}
//					} else if (writer.hasChallengeQuest()) { //writer has challenge quest
//						if (writer.getChallengeQuest().isTimed()) { //timed challenge quest
//							if (writer.getChallengeQuest().getTimeLimit() < currentEncounter.getLength()) { //current encounter is long enough to qualify for timed quest
//								writer.updateChallengeQuests(true, totalWritten);
//								if (writer.getChallengeQuest().getQuestGoal().isComplete()) { //successfully finished challenge quest
//									return event.reply("You have written " + totalWritten + " words for an average of " + wordsPerMin + " wpm.")
//											.then(event.createFollowup("Progress updated! You have written " + EncounterInfo.writerIndex.get(user).getGoal().getProgress() + " " + EncounterInfo.writerIndex.get(user).getGoal().getGoalType() + " of " + EncounterInfo.writerIndex.get(user).getGoalNum() + " " + EncounterInfo.writerIndex.get(user).getGoal().getGoalType() + ".").then());
//								}
//							}
//						}
//					} else if (writer.hasQuest() && writer.hasChallengeQuest()) { //writer has quest and challenge quest 
//						return event.reply(""); //FIXME
//					}
//					else { //writer has no quests
//						return event.reply("You have written " + totalWritten + " words for an average of " + wordsPerMin + " wpm.")
//								.then(event.createFollowup("Progress updated! You have written " + EncounterInfo.writerIndex.get(user).getGoal().getProgress() + " " + EncounterInfo.writerIndex.get(user).getGoal().getGoalType() + " of " + EncounterInfo.writerIndex.get(user).getGoalNum() + " " + EncounterInfo.writerIndex.get(user).getGoal().getGoalType() + ".").then());
//					}
//				} else { //writer has no set goal
//					return event.reply("You have written " + totalWritten + " words for an average of " + wordsPerMin + " wpm.");
//				}

	}

}
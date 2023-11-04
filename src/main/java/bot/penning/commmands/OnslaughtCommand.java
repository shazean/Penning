package bot.penning.commmands;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;

import bot.penning.EncounterInfo;
import bot.penning.Goal;
import bot.penning.encounters.Onslaught;
import bot.penning.encounters.Skirmish;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Member;
import reactor.core.publisher.Mono;

public class OnslaughtCommand implements SlashCommand {

	@Override
	public String getName() {
		return "onslaught";
	}
	
	int buttonCounter = 0;
	int buttonID = 0;
	
	@Override
	public Mono<Void> handle(ChatInputInteractionEvent event) {
		/*
	        Since slash command options are optional according to discord, we will wrap it into the following function
	        that gets the value of our option as a String without chaining several .get() on all the optional values
	        In this case, there is no fear it will return empty/null as this is marked "required: true" in our json.
		 */
		Long target = event.getOption("target")
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asLong)
				.get(); //This is warning us that we didn't check if its present, we can ignore this on required options

		Long startTime = event.getOption("start") //how long from now the skirmish should begin
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asLong)
				.get(); //This is warning us that we didn't check if its present, we can ignore this on required options


		Member user = event.getInteraction().getMember().get();

		Long warIndex = EncounterInfo.getWarIndex();
		Onslaught onslaught = new Onslaught(warIndex, target, startTime);
		EncounterInfo.warRegistry.put(onslaught.getIndex() % 50, onslaught);
		GatewayDiscordClient client = event.getClient();
		Long finalTime;
		Button doneButton = Button.success("done-button", "Word goal reached!");
		ScheduledExecutorService schedule = onslaught.getSchedule();



		//stop user from creating an onslaught with length 0 (because calculating the average for total creates a divide by zero scenario)
		if (target == 0) {
			return event.reply("Onslaught cannot have a goal of 0 words! Try again!").withEphemeral(true);
		}

		if (startTime == 15) { //convert startTime to seconds, and remove 1 second of 15 minutes, to stop a timed out token from causing issues
			finalTime = 899L;
		} else {
			finalTime = startTime * 60L;
		}

		StopWatch stopwatch = StopWatch.create();
		
		schedule.schedule(() -> {

			stopwatch.start();

		}, startTime, TimeUnit.MINUTES);	
		
		client.on(ButtonInteractionEvent.class, buttonEvent -> {
			if (buttonEvent.getCustomId().equals("done-button")) {
				Member writer = buttonEvent.getInteraction().getMember().get();
				stopwatch.split();;
				long timeToGoal = stopwatch.getSplitTime() / 60000; //convert from milliseconds to minutes
				onslaught.createParticipant(writer, target, (double) target / timeToGoal, timeToGoal);
				buttonCounter++;
				if (buttonCounter == 1) {
					return buttonEvent.reply("You have finished the onslaught!").then(Mono.delay(Duration.ofMinutes(1)).then(event.createFollowup(onslaught.createParticipantSummary())).then());
				}
				else {
					return buttonEvent.reply("You have finished the onslaught in " + timeToGoal + " minutes!");
				}
			}
			else {
				return Mono.empty();
			}
		}).timeout(Duration.ofMinutes((target / 20) + 5)).subscribe();
				
		schedule.schedule(() -> {

			stopwatch.stop();

		}, target / 20 + 5, TimeUnit.MINUTES);	

		EncounterInfo.incrementWarIndex();

		return event.reply("Onslaught #" + onslaught.getIndex() + " created with a goal of " + onslaught.getGoal() + " words and will start in " + onslaught.getStartTime() + " minutes.")
				.then(Mono.delay(Duration.ofSeconds(finalTime)))
				.then(event.createFollowup("Onslaught #" + onslaught.getIndex() + " starts now! Race to " + onslaught.getGoal() + " words!")
						.withComponents(ActionRow.of(doneButton))
						.then());

	}
}

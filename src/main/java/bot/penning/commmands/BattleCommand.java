package bot.penning.commmands;

import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import bot.penning.EncounterInfo;
import bot.penning.encounters.Battle;
import bot.penning.encounters.Skirmish;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.component.Button;
import reactor.core.publisher.Mono;

public class BattleCommand implements SlashCommand {

	static ArrayList<Object> writersEntered = new ArrayList<Object>();

	@Override
	public String getName() {
		return "battle";
	}

	@Override
	public Mono<Void> handle(ChatInputInteractionEvent event) {

		Double duration = event.getOption("time") //duration of skirmish
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asDouble)
				.get(); //This is warning us that we didn't check if its present, we can ignore this on required options

		Long startTime = event.getOption("start") //how long from now the skirmish should begin
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asLong)
				.get(); //This is warning us that we didn't check if its present, we can ignore this on required options

		Long warIndex = EncounterInfo.getWarIndex();
		Battle battle = new Battle(warIndex, duration, startTime);
		EncounterInfo.warRegistry.put(battle.getIndex() % 50, battle);
		GatewayDiscordClient client = event.getClient();
		Long finalTime;

		//stop user from creating a battle with length 0 (because calculating the average for total creates a divide by zero scenario)
		if (duration == 0) {
			return event.reply("Length of battle cannot be zero! Try again!").withEphemeral(true);
		}

		//Let's user know the length is too long
		if (duration < 1) {
			return event.reply("Length is too short! Try starting a word skirmish instead.").withEphemeral(true);
		}
		if (duration > 12) {
			return event.reply("Length is too long! A word battle cannot exceed 12 hours.").withEphemeral(true);
		}

		if (startTime > 15) {
			return event.reply("Battle must be started within 15 minutes!").withEphemeral(true);
		}

		if (startTime == 15) { //convert startTime to seconds, and remove 1 second if 15 minutes, to stop a timed out token from causing issues
			finalTime = 899L;
		} else {
			finalTime = startTime * 60L;
		}

		EncounterInfo.incrementWarIndex();

		//FIXME if adding in a join button ping option
		//		client.on(ButtonInteractionEvent.class, embedEvent -> {
		//			if (embedEvent.getCustomId().equals("join_button")) {
		//				Optional<Member> writer = embedEvent.getInteraction().getMember();
		//				writersEntered.add(writer);
		//				return embedEvent.reply("You have joined the skirmish!");
		//			}
		//			else if (embedEvent.getCustomId().equals("total button")) {
		//				return embedEvent.reply("Total! //FIXME"); //FIXME
		//			}
		//			else {
		//				return embedEvent.reply("Else! //FIXME"); //FIXME
		//			}
		//		}).timeout(Duration.ofMinutes(startTime)).subscribe();


		//		String pingList = ""; //FIXME
		//		for (int i = 0; i < writersEntered.size(); i++) {
		//			pingList += "@" + writersEntered.get(i).toString() + " ";
		//			System.out.print(writersEntered.get(i).toString() + " ");
		//		}

		runBattle(event, startTime, battle);

		return event.reply("Battle #" + battle.getIndex() + " created for " + battle.getLengthHours() + " hours and " + battle.getLengthMinutes() + " minutes, and will start in " + battle.getStartTime() + " minutes.")
				.then(Mono.delay(Duration.ofSeconds(finalTime)))
				.then(event.createFollowup("Battle #" + battle.getIndex() + " starts now!")
						.then());
	}


	public void runBattle(ChatInputInteractionEvent event, Long startTime, Battle battle) {
		GatewayDiscordClient client = event.getClient();
		Snowflake guildID = event.getInteraction().getGuildId().get();

		client.on(MessageCreateEvent.class, embedEvent -> {
			if (embedEvent.getMember().get().equals(client.getSelfMember(guildID).block())) { //if message was sent by ourselves
				String botMessage = embedEvent.getMessage().getContent();
				if (botMessage.equals("Battle #" + battle.getIndex() + " starts now!")) {

					Long penningsWords = Math.abs(19 * battle.getLength() + ((int)(Math.random() * (50- -50+1)+ -50)));
					//					Button totalButton = Button.primary("total-button", "Add your total!");
					ScheduledExecutorService schedule = battle.getSchedule();

					schedule.schedule(() -> {

						battle.setComplete();
						battle.createMessage(embedEvent, "Battle #" + battle.getIndex() + " ends now!");
						battle.createMessage(embedEvent, "How much did you write? I wrote " + penningsWords + " words.");

						battle.createMessage(embedEvent, "Use `/total " + battle.getIndex() + "` to add your total.");

						printSummary(embedEvent, battle);

					}, battle.getLength(), TimeUnit.MINUTES);		

				}	
			}
			return Mono.empty();
		}).timeout(Duration.ofMinutes(12 * 60 + 1)).subscribe(); //battles' length capped at 12 hours, but an additional minute added for wiggle room

	}

	public void printSummary(MessageCreateEvent event, Battle battle) {
		ScheduledExecutorService schedule = battle.getSchedule();

		schedule.schedule(() -> {

			battle.setExpired();
			battle.createMessage(event, battle.createParticipantSummary());


		}, 5, TimeUnit.MINUTES);		
	}

}
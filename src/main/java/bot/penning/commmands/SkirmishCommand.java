package bot.penning.commmands;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import bot.penning.EncounterInfo;
import bot.penning.encounters.Skirmish;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Member;
import reactor.core.publisher.Mono;

public class SkirmishCommand implements SlashCommand {

	static ArrayList<String> writersEntered = new ArrayList<String>();

	@Override
	public String getName() {
		return "skirmish";
	}

	@Override
	public Mono<Void> handle(ChatInputInteractionEvent event) {

		Long duration = event.getOption("time") //duration of skirmish
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asLong)
				.get(); //This is warning us that we didn't check if its present, we can ignore this on required options

		Long startTime = event.getOption("start") //how long from now the skirmish should begin
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asLong)
				.get(); //This is warning us that we didn't check if its present, we can ignore this on required options

		Long warIndex = EncounterInfo.getEncounterIndex();
		Skirmish skirmish = new Skirmish(warIndex, duration, startTime);
		EncounterInfo.encounterRegistry.put(skirmish.getIndex() % 50, skirmish);
		GatewayDiscordClient client = event.getClient();
//		Long finalTime;

		//stop user from creating a skirmish with length 0 (because calculating the average for total creates a divide by zero scenario)
		if (duration == 0) {
			return event.reply("Length of skirmish cannot be zero! Try again!").withEphemeral(true);
		}

		//Let's user know the length is too long
		if (duration > 60) {
			return event.reply("Length is too long! Try starting a word battle instead.").withEphemeral(true);
		}

		if (startTime > 30) {
			return event.reply("Skirmish must be started within 30 minutes!").withEphemeral(true);
		}

//		if (startTime == 15) { //convert startTime to seconds, and remove 1 second if 15 minutes, to stop a timed out token from potentially causing issues
//			finalTime = 899L;
//		} else {
//			finalTime = startTime * 60L;
//		}

		Button joinButton = Button.primary("join_button_" + skirmish.getIndex(), "Join!");

		EncounterInfo.incrementEncounterIndex();

		client.on(ButtonInteractionEvent.class, embedEvent -> {
			if (embedEvent.getCustomId().equals("join_button_" + skirmish.getIndex())) {
				Member writerMention = embedEvent.getInteraction().getMember().get();
				skirmish.addPingableMember(writerMention);
				return embedEvent.reply(writerMention.getNicknameMention() + ", you have joined the skirmish!");
			}
			else {
				return Mono.empty();
			}
		}).timeout(Duration.ofMinutes(startTime)).subscribe();

		skirmish.setIsWar(false);
		runSkirmish(event, startTime, skirmish);

		return event.reply("Skirmish #" + skirmish.getIndex() + " created for " + skirmish.getLength() + " minutes, and will start in " + skirmish.getStartTime() + " minutes.")
				.withComponents(ActionRow.of(joinButton));
	}


	public void runSkirmish(ChatInputInteractionEvent event, Long startTime, Skirmish skirmish) {
		GatewayDiscordClient client = event.getClient();
		Snowflake guildID = event.getInteraction().getGuildId().get();

		client.on(MessageCreateEvent.class, embedEvent -> {
			if (embedEvent.getMember().get().equals(client.getSelfMember(guildID).block())) { //if message was sent by ourselves
				String botMessage = embedEvent.getMessage().getContent().substring(0, 22 + getNumDigits(skirmish.getIndex())); //length based off how many digits the skirmish index is

				if (botMessage.equals("Skirmish #" + skirmish.getIndex() + " created for")) {

					ScheduledExecutorService schedule = skirmish.getSchedule();

					if (skirmish.getStartTime() > 1) {
						schedule.schedule(() -> {

							skirmish.createMessage(embedEvent, "Skirmish #" + skirmish.getIndex() + " starts in one minute!");

						}, skirmish.getStartTime() - 1, TimeUnit.MINUTES);	
					}
					
					schedule.schedule(() -> {

						skirmish.createMessage(embedEvent, "Skirmish #" + skirmish.getIndex() + " starts now! " + skirmish.getPingableMembers());

					}, skirmish.getStartTime(), TimeUnit.MINUTES);	
					
					Long penningsWords = Math.abs(24 * skirmish.getLength() + ((int)(Math.random() * (50- -50+1)+ -50)));
//					Button totalButton = Button.primary("total-button", "Add your total!");

					schedule.schedule(() -> {

						skirmish.setComplete();
						skirmish.createMessage(embedEvent, "Skirmish #" + skirmish.getIndex() + " ends now! " + skirmish.getPingableMembers());
						skirmish.createMessage(embedEvent, "How much did you write? I wrote " + penningsWords + " words. Use `/total " + skirmish.getIndex() + "` to add your total.");

						printSummary(embedEvent, skirmish);

					}, skirmish.getLength() + skirmish.getStartTime(), TimeUnit.MINUTES);		

				}	
			}
			return Mono.empty();
		}).timeout(Duration.ofMinutes(skirmish.getLength() + skirmish.getStartTime() + 1)).subscribe();

	}
	
	private int getNumDigits(long num) {
		int length = (int) (Math.log10(num) + 1);
		return length;
	}

	public void printSummary(MessageCreateEvent event, Skirmish skirmish) {
		ScheduledExecutorService schedule = skirmish.getSchedule();

		schedule.schedule(() -> {

			//compile skirmish info TODO
			skirmish.setExpired();
			skirmish.createMessage(event, skirmish.createParticipantSummary());

		}, 5, TimeUnit.MINUTES);		
	}

}
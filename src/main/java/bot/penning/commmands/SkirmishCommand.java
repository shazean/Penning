package bot.penning.commmands;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import bot.penning.Skirmish;
import bot.penning.WarInfo;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.TextInput;
import discord4j.core.object.entity.Member;
import reactor.core.publisher.Mono;

public class SkirmishCommand implements SlashCommand {

	static ArrayList<Object> writersEntered = new ArrayList<Object>();

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

		ScheduledExecutorService schedule = Executors.newScheduledThreadPool(3);
		int warIndex = WarInfo.getWarIndex();
		Skirmish skirmish = new Skirmish(warIndex, duration, startTime);
		WarInfo.skirmishes.add(skirmish.getIndex(), skirmish);
		GatewayDiscordClient client = event.getClient();
		Long finalTime;

		//stop user from creating a skirmish with length 0 (because calculating the average for total creates a divide by zero scenario)
		if (duration == 0) {
			return event.reply("Length of skirmish cannot be zero! Try again!").withEphemeral(true);
		}

		//Let's user know the length is too long
		if (duration > 60) {
			return event.reply("Length is too long! Try starting a word battle instead.").withEphemeral(true);
		}

		if (startTime > 15) {
			return event.reply("Skirmish must be started within 15 minutes!").withEphemeral(true);
		}

		if (startTime == 15) { //convert startTime to seconds, and remove 1 second if 15 minutes, to stop a timed out token from causing issues
			finalTime = 899L;
		} else {
			finalTime = startTime * 60L;
		}




		Button joinButton = Button.primary("join_button", "//FIXME!");

		event.reply("Skirmish #" + skirmish.getIndex() + " created for " + skirmish.getLength() + " minutes, and will start in " + skirmish.getStartTime() + " minutes.")
		.withComponents(ActionRow.of(joinButton));		
		WarInfo.incrementWarIndex();


		client.on(ButtonInteractionEvent.class, embedEvent -> {
			if (embedEvent.getCustomId().equals("join_button")) {
				Optional<Member> writer = embedEvent.getInteraction().getMember();
				writersEntered.add(writer);
				return embedEvent.reply("You have joined the skirmish!");
			}
			else if (embedEvent.getCustomId().equals("total button")) {
				return embedEvent.reply("Total! //FIXME"); //FIXME
			}
			else {
				return embedEvent.reply("Else! //FIXME"); //FIXME
			}
		}).timeout(Duration.ofMinutes(startTime)).subscribe();


		//		String pingList = ""; //FIXME
		//		for (int i = 0; i < writersEntered.size(); i++) {
		//			pingList += "@" + writersEntered.get(i).toString() + " ";
		//			System.out.print(writersEntered.get(i).toString() + " ");
		//		}

		runSkirmish(event, startTime, skirmish);

		return event.reply("Skirmish #" + skirmish.getIndex() + " created for " + skirmish.getLength() + " minutes, and will start in " + skirmish.getStartTime() + " minutes.")
				.then(Mono.delay(Duration.ofSeconds(finalTime)))
				.then(event.createFollowup("Skirmish #" + skirmish.getIndex() + " starts now!")
						.then());
	}


	public Mono<Void> runSkirmish(ChatInputInteractionEvent event, Long startTime, Skirmish skirmish) {
		GatewayDiscordClient client = event.getClient();
		Snowflake guildID = event.getInteraction().getGuildId().get();

		client.on(MessageCreateEvent.class, embedEvent -> {
			if (embedEvent.getMember().get().equals(client.getSelfMember(guildID).block())) { //if message was sent by ourselves
				String botMessage = embedEvent.getMessage().getContent();
				if (botMessage.equals("Skirmish #" + skirmish.getIndex() + " starts now!")) {

					Long penningsWords = Math.abs(29 * skirmish.getLength() + ((int)(Math.random() * (50- -50+1)+ -50)));
					Button totalButton = Button.primary("total-button", "Add your total!");
					ScheduledExecutorService schedule = skirmish.getSchedule();

					schedule.schedule(() -> {

						skirmish.createMessage(embedEvent, "Skirmish #" + skirmish.getIndex() + " ends now!");
						skirmish.createMessage(embedEvent, "How much did you write? I wrote " + penningsWords + " words.");
						//						embedEvent.getMessage().getChannel().block().createMessage("Add your total!")
						//						.withComponents(ActionRow.of(TextInput.small("total-id", "Total?")))
						//						.block();

						//						skirmish.createMessage(event, "Use `'!total " + skirmish.getIndex() + " [amount written]'` to add your total.");
						//						.withComponents(ActionRow.of(totalButton));


					}, skirmish.getLength(), TimeUnit.MINUTES);		

				}	
			}
			return null;
		}).timeout(Duration.ofMinutes(75)).subscribe();

		return null;
	}

}
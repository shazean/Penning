package bot.penning.commmands;

import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import bot.penning.EncounterInfo;
import bot.penning.encounters.Skirmish;
import bot.penning.encounters.War;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.component.Button;
import reactor.core.publisher.Mono;

public class WarCommand implements SlashCommand {

	static ArrayList<Object> writersEntered = new ArrayList<Object>();

	@Override
	public String getName() {
		return "war";
	}

	@Override
	public Mono<Void> handle(ChatInputInteractionEvent event) {

		Long duration = event.getOption("time") //duration of individual skirmish
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asLong)
				.get(); //This is warning us that we didn't check if its present, we can ignore this on required options

		Long startTime = event.getOption("start") //how long from now the war should begin
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asLong)
				.get(); //This is warning us that we didn't check if its present, we can ignore this on required options

		Long quantity = event.getOption("quantity") //how many skirmishes should be created
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asLong)
				.get(); //This is warning us that we didn't check if its present, we can ignore this on required options

		Long warIndex = EncounterInfo.getWarIndex();
		War war = new War(warIndex, duration, startTime, quantity);
		EncounterInfo.warRegistry.put(war.getIndex() % 50, war);
		GatewayDiscordClient client = event.getClient();
		Long finalTime;

		if (EncounterInfo.isWarRunning()) {
			return event.reply("You cannot run two wars at once! Try creating an individual skirmish or battle instead!").withEphemeral(true);
		}

		//stop user from creating a skirmish with length 0 (because calculating the average for total creates a divide by zero scenario)
		if (duration == 0 || quantity == 0) {
			return event.reply("Inputs cannot be zero! Try again!").withEphemeral(true);
		}
		
		if (quantity == 1) {
			return event.reply("Just one? Try creating a skirmish or battle instead!").withEphemeral(true);
		}

		//Let's user know the length is too long
		if (duration > 60) {
			return event.reply("Length is too long! Try starting a word battle instead.").withEphemeral(true);
		}

		if (startTime > 15) {
			return event.reply("War must be started within 15 minutes!").withEphemeral(true);
		}

		if (startTime == 15) { //convert startTime to seconds, and remove 1 second if 15 minutes, to stop a timed out token from causing issues
			finalTime = 899L;
		} else {
			finalTime = startTime * 60L;
		}
		
		if ((duration + startTime) * quantity > 720) {
			return event.reply("Length is too long! Total time of war cannot exceed 12 hours.").withEphemeral(true);

		}

		EncounterInfo.incrementWarIndex();
		EncounterInfo.setWarRunning(true);

		runWar(event, startTime, war);

		return event.reply("War created! " + war.getQuantity() + " skirmishes will run for " + war.getLength() + " minutes, in " + war.getStartTime() + " minute intervals.");
				//				.then(Mono.delay(Duration.ofSeconds(finalTime)))
				//				.then(event.createFollowup("Skirmish #" + war.getIndex() + " starts now!")
				//						.then());
	}


	public void runWar(ChatInputInteractionEvent event, Long startTime, War war) {
		GatewayDiscordClient client = event.getClient();
		Snowflake guildID = event.getInteraction().getGuildId().get();
		ScheduledExecutorService schedule = war.getSchedule();
		
		client.on(MessageCreateEvent.class, embedEvent -> {
			if (embedEvent.getMember().get().equals(client.getSelfMember(guildID).block())) { //if message was sent by ourselves
//				String botMessage = embedEvent.getMessage().getContent();
				String[] botMessage = embedEvent.getMessage().getContent().split(" ");
				if (botMessage[0].equals("War") && botMessage[1].equals("created!")) {

					
					runNextSkirmish(embedEvent, startTime, war, schedule, war.getQuantity());
//					while (war.getRemainingQty() > 0) {
//						
//						Long penningsWords = Math.abs(29 * war.getLength() + ((int)(Math.random() * (50- -50+1)+ -50)));
//						Long currentStartTime = (war.getStartTime() * (war.getQuantity() - war.getRemainingQty() + 1)) + (war.getLength() * (war.getQuantity() - war.getRemainingQty()));
//						Long currentLengthTime = war.getLength() * (war.getQuantity() - war.getRemainingQty() + 1);
//
//						war.reduceRemainingQty();
//
//						schedule.schedule(() -> {
//
////							war.setComplete();
//							war.createMessage(embedEvent, "Skirmish #" + war.getIndex() + ", part " + (war.getQuantity() - war.getRemainingQty()) + " of " + war.getQuantity() + " starts now!");
//
//						}, currentStartTime , TimeUnit.MINUTES);		
//						
//						schedule.schedule(() -> {
//
////							war.setComplete();
//							war.createMessage(embedEvent, "Skirmish #" + war.getIndex() + ", part " + (war.getQuantity() - war.getRemainingQty()) + " of " + war.getQuantity() + " ends now!");
//							war.createMessage(embedEvent, "How much did you write? I wrote " + penningsWords + " words.");
//							//						embedEvent.getMessage().getChannel().block().createMessage("Add your total!")
//							//						.withComponents(ActionRow.of(TextInput.small("total-id", "Total?")))
//							//						.block();
//
//							war.createMessage(embedEvent, "Use `/total " + war.getIndex() + "` to add your total.");
//							//						.withComponents(ActionRow.of(totalButton));
//
//							printSummary(embedEvent, war);
//							
//						}, currentLengthTime, TimeUnit.MINUTES);	
//						
//						schedule.schedule(() -> {
//
////							war.setComplete();
//							war.reduceRemainingQty();
//
//						}, currentStartTime + currentLengthTime, TimeUnit.MINUTES);	
//						
//					}
				}	
			}
			return Mono.empty();
		}).timeout(Duration.ofMinutes(721)).subscribe();
		
		schedule.schedule(() -> {
			
			EncounterInfo.setWarRunning(false);
			war.setComplete();

		}, ((war.getLength() + war.getStartTime()) * war.getQuantity() + 1), TimeUnit.MINUTES);	

	}
	
	public void runNextSkirmish(MessageCreateEvent event, Long startTime, War war, ScheduledExecutorService schedule, Long remainingSkirmishes) {
	
			Long penningsWords = Math.abs(29 * war.getLength() + ((int)(Math.random() * (50- -50+1)+ -50)));
//			Long currentStartTime = (war.getStartTime() * (war.getQuantity() - war.getRemainingQty() + 1)) + (war.getLength() * (war.getQuantity() - war.getRemainingQty()));
//			Long currentLengthTime = war.getLength() * (war.getQuantity() - war.getRemainingQty() + 1);

			schedule.schedule(() -> {

//				war.setComplete();
				war.createMessage(event, "Skirmish #" + war.getIndex() + ", part " + (war.getQuantity() - war.getRemainingQty() + 1) + " of " + war.getQuantity() + " starts now!");

			}, startTime , TimeUnit.MINUTES);		
			
			schedule.schedule(() -> {

//				war.setComplete();
				war.createMessage(event, "Skirmish #" + war.getIndex() + ", part " + (war.getQuantity() - war.getRemainingQty() + 1) + " of " + war.getQuantity() + " ends now!");
				war.createMessage(event, "How much did you write? I wrote " + penningsWords + " words.");
				war.createMessage(event, "Use `/total " + war.getIndex() + "` to add your total.");

				printSummary(event, war);

				war.reduceRemainingQty();
				EncounterInfo.incrementWarIndex();
				if (war.getRemainingQty() > 0) {
					runNextSkirmish(event, startTime, war, schedule, war.getRemainingQty());
				}

				
			}, war.getLength() + startTime, TimeUnit.MINUTES);	
			
//			
//		}
	}

	public void printSummary(MessageCreateEvent event, War war) {
		ScheduledExecutorService schedule = war.getSchedule();

		schedule.schedule(() -> {

			war.setExpired();
			war.createMessage(event, war.createParticipantSummary());

		}, 5, TimeUnit.MINUTES);		
	}

}
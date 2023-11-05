package bot.penning.commmands;

import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import bot.penning.EncounterInfo;
import bot.penning.encounters.Skirmish;
import bot.penning.encounters.War;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Member;
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
				.get();

		Long startTime = event.getOption("start") //how long from now the war should begin
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asLong)
				.get();

		Long interval = event.getOption("interval") //time between each skirmish
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asLong)
				.get();

		Long quantity = event.getOption("quantity") //how many skirmishes should be created
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asLong)
				.get();

//		Long encounterIndex = EncounterInfo.getEncounterIndex();
//		War war = new War(encounterIndex, duration, startTime, quantity, interval);
//		EncounterInfo.encounterRegistry.put(war.getIndex() % 50, war);
//		GatewayDiscordClient client = event.getClient();
//		Long finalTime;
//		numSkirmishes = quantity; 

		if (EncounterInfo.isWarRunning()) {
			return event.reply("You cannot run two wars at once! Try creating an individual skirmish or battle instead!").withEphemeral(true);
		}

		if (duration == 0 || quantity == 0) {
			return event.reply("Inputs cannot be zero! Try again!").withEphemeral(true);
		}

		if (quantity == 1) {
			return event.reply("Just one? Try creating a skirmish or battle instead!").withEphemeral(true);
		}

		if (duration > 60) {
			return event.reply("Length is too long! Try starting a word battle instead.").withEphemeral(true);
		}

		if (startTime > 30) {
			return event.reply("War must be started within 30 minutes!").withEphemeral(true);
		}

		//		if (startTime == 15) { //convert startTime to seconds, and remove 1 second if 15 minutes, to stop a timed out token from causing issues
		//			finalTime = 899L;
		//		} else {
		//			finalTime = startTime * 60L;
		//		}

		if ((duration + interval) * quantity - interval + startTime > 720) {
			return event.reply("Length is too long! Total time of war cannot exceed 12 hours.").withEphemeral(true);
		}

//		EncounterInfo.incrementEncounterIndex();
		EncounterInfo.setWarRunning(true);

		runWar(event, duration, startTime, interval, quantity);

		return event.reply("War created! " + quantity + " skirmishes will run for " + duration + " minutes each, in " + interval + " minute intervals, beginning in " + startTime + " minutes.");
		//				.then(Mono.delay(Duration.ofSeconds(finalTime)))
		//				.then(event.createFollowup("Skirmish #" + war.getIndex() + " starts now!")
		//						.then());
	}


	public void runWar(ChatInputInteractionEvent event, Long duration, Long startTime, Long interval, Long quantity) {
		GatewayDiscordClient client = event.getClient();
		Snowflake guildID = event.getInteraction().getGuildId().get();
		ScheduledExecutorService schedule = Executors.newScheduledThreadPool(3);

		client.on(MessageCreateEvent.class, embedEvent -> {
			if (embedEvent.getMember().get().equals(client.getSelfMember(guildID).block())) { //if message was sent by ourselves
				String botMessage = embedEvent.getMessage().getContent().substring(0, 12);
				if (botMessage.equals("War created!")) {
					Long encounterIndex = EncounterInfo.getEncounterIndex();
					Skirmish skirmish = new Skirmish(encounterIndex, duration, startTime);
					EncounterInfo.encounterRegistry.put(skirmish.getIndex() % 50, skirmish);
					skirmish.setIsWar(true);

//					war.skirmishes.add(new Skirmish(war.getIndex(), war.getLength(), war.getInterval()));
					runNextSkirmish(embedEvent, skirmish, interval, quantity, quantity);
					EncounterInfo.incrementEncounterIndex();

//					numSkirmishes--;
				}	
			}
			return Mono.empty();
		}).timeout(Duration.ofMinutes(721)).subscribe();

		schedule.schedule(() -> {

//			war.setComplete();
			EncounterInfo.setWarRunning(false);

		}, ((duration + interval) * quantity - interval + startTime + 1), TimeUnit.MINUTES);

	}

	public void runNextSkirmish(MessageCreateEvent event, Skirmish skirmish, Long interval, Long totalSkirmishes, Long remainingSkirmishes) {

		Long penningsWords = Math.abs(24 * skirmish.getLength() + ((int)(Math.random() * (50- -50 + 1) + -50)));
		ScheduledExecutorService schedule = skirmish.getSchedule();
		Button joinButton = Button.primary("join_button_" + skirmish.getIndex(), "Join!");
		GatewayDiscordClient client = event.getClient();
		
		client.on(ButtonInteractionEvent.class, embedEvent -> {
			if (embedEvent.getCustomId().equals("join_button_" + skirmish.getIndex())) {
				Member writerMention = embedEvent.getInteraction().getMember().get();
				skirmish.addPingableMember(writerMention);
				return embedEvent.reply(writerMention.getNicknameMention() + ", you have joined the skirmish!");
			}
			else {
				return Mono.empty();
			}
		}).timeout(Duration.ofMinutes(skirmish.getStartTime() + 1)).subscribe();
		
		skirmish.createMessageWithButton(event, "Skirmish #" + skirmish.getIndex() + ", part " + (totalSkirmishes - remainingSkirmishes + 1) + " of " + totalSkirmishes + " starts in " + skirmish.getStartTime() + " minutes!", joinButton);

		schedule.schedule(() -> {

			//				war.setComplete();
			skirmish.createMessage(event, "Skirmish #" + skirmish.getIndex() + ", part " + (totalSkirmishes - remainingSkirmishes + 1) + " of " + totalSkirmishes + " starts now! "  + skirmish.getPingableMembers());

		}, skirmish.getStartTime() , TimeUnit.MINUTES);		

		schedule.schedule(() -> {

			skirmish.setComplete();
			skirmish.createMessage(event, "Skirmish #" + skirmish.getIndex() + ", part " + (totalSkirmishes - remainingSkirmishes + 1) + " of " + totalSkirmishes + " ends now! " + skirmish.getPingableMembers());
			skirmish.createMessage(event, "How much did you write? I wrote " + penningsWords + " words. Use `/total " + skirmish.getIndex() + "` to add your total.");

			printSkirmishSummary(event, skirmish);


			if (remainingSkirmishes > 1) {
				Skirmish newSkirmish = new Skirmish(EncounterInfo.getEncounterIndex(), skirmish.getLength(), interval);
				EncounterInfo.encounterRegistry.put(newSkirmish.getIndex() % 50, newSkirmish);
				newSkirmish.setIsWar(true);
				runNextSkirmish(event, newSkirmish, interval, totalSkirmishes, remainingSkirmishes - 1);
//				EncounterInfo.incrementEncounterIndex();
			}
			
			if (remainingSkirmishes == 1) { //last skirmish
				printWarSummary(event, skirmish);
			}
			

		}, skirmish.getLength() + skirmish.getStartTime(), TimeUnit.MINUTES);	
	}

	public void printSkirmishSummary(MessageCreateEvent event, Skirmish skirmish) {
		ScheduledExecutorService schedule = skirmish.getSchedule();

		schedule.schedule(() -> {

			skirmish.setExpired();
			skirmish.createMessage(event, skirmish.createParticipantSummary());

		}, 5, TimeUnit.MINUTES);		
	}
	
	public void printWarSummary(MessageCreateEvent event, Skirmish skirmish) {
		ScheduledExecutorService schedule = skirmish.getSchedule();

		schedule.schedule(() -> {

			skirmish.setExpired();
			skirmish.createMessage(event, EncounterInfo.createWarSummary());
			EncounterInfo.resetWarSummary();

		}, 5, TimeUnit.MINUTES);		
	}
	
}
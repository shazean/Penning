package bot.penning.commmands;

import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import bot.penning.BotUtil;
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
import discord4j.core.object.component.ActionRow;
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


		GatewayDiscordClient client = event.getClient();
		Long index = EncounterInfo.getEncounterIndex();

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

		if ((duration + interval) * quantity - interval + startTime > 720) {
			return event.reply("Length is too long! Total time of war cannot exceed 12 hours.").withEphemeral(true);
		}

		
		//		if (startTime == 15) { //convert startTime to seconds, and remove 1 second if 15 minutes, to stop a timed out token from causing issues
		//			finalTime = 899L;
		//		} else {
		//			finalTime = startTime * 60L;
		//		}

		
		War war = new War(index, duration, startTime, quantity, interval);
//		EncounterInfo.encounterRegistry.put(war.getIndex() % 50, war);

		EncounterInfo.currentWar = war;
		EncounterInfo.setWarRunning(true);

		EncounterInfo.incrementEncounterIndex();

		runWar(war, event, duration, startTime, interval, quantity);

		
		Button alertButton = Button.primary("alert_button_" + war.getIndex(), "Ping me!");
		

		client.on(ButtonInteractionEvent.class, embedEvent -> {
			if (embedEvent.getCustomId().equals("alert_button_" + war.getIndex())) {
				Member writerMention = embedEvent.getInteraction().getMember().get();
				war.addPingableMember(writerMention);
				return embedEvent.reply(writerMention.getNicknameMention() + ", you have joined alerts for the war!");
			}
			else {
				return Mono.empty();
			}
		}).timeout(Duration.ofMinutes(startTime)).subscribe();
		
		
		//TODO add join war alerts button
		return event.reply("War created!" + quantity + " skirmishes will run for " + duration + " minutes each, in " + interval + " minute intervals, beginning in " + startTime + " minutes.")
				.withComponents(ActionRow.of(alertButton));
		//				.then(Mono.delay(Duration.ofSeconds(finalTime)))
		//				.then(event.createFollowup("Skirmish #" + war.getIndex() + " starts now!")
		//						.then());
	}


	public void runWar(War war, ChatInputInteractionEvent event, Long duration, Long startTime, Long interval, Long quantity) {
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
					runNextSkirmish(war, embedEvent, skirmish);
					EncounterInfo.incrementEncounterIndex();

//					numSkirmishes--;
				}	
			}
			return Mono.empty();
		}).timeout(Duration.ofMinutes((duration + interval) * quantity - interval + startTime + 1)).subscribe();

		schedule.schedule(() -> {

//			war.setComplete();
			EncounterInfo.setWarRunning(false);

		}, ((duration + interval) * quantity - interval + startTime + 1), TimeUnit.MINUTES);

	}

	public void runNextSkirmish(War war, MessageCreateEvent event, Skirmish skirmish) {

		long penningsWords = Math.abs(BotUtil.PENNING_WRITING_SPEED * skirmish.getLength() + ((int)(Math.random() * (50- -50 + 1) + -50)));
		ScheduledExecutorService schedule = skirmish.getSchedule();
//		GatewayDiscordClient client = event.getClient();  
		Long currentIndex = war.getQuantity() - war.getRemainingQty() + 1;		
		
		
		schedule.schedule(() -> {

			skirmish.createMessage(event, "Skirmish #" + skirmish.getIndex() + ", part " + currentIndex + " of " + war.getQuantity() + " starts in 1 minute!"  + war.getPingableMembers());

		}, skirmish.getStartTime() - 1, TimeUnit.MINUTES);	
		
//		skirmish.createMessage(event, "Skirmish #" + skirmish.getIndex() + ", part " + (war.getQuantity() - remainingSkirmishes + 1) + " of " + war.getQuantity() + " starts in " + skirmish.getStartTime() + " minutes!");

		schedule.schedule(() -> {

			skirmish.createMessage(event, "Skirmish #" + skirmish.getIndex() + ", part " + currentIndex + " of " + war.getQuantity() + " starts now! "  + war.getPingableMembers());

		}, skirmish.getStartTime() , TimeUnit.MINUTES);

		schedule.schedule(() -> {

			skirmish.setComplete();
			skirmish.createMessage(event, "Skirmish #" + skirmish.getIndex() + ", part " + currentIndex + " of " + war.getQuantity() + " ends now! " + war.getPingableMembers());
			skirmish.createMessage(event, "How much did you write? I wrote " + penningsWords + " words. Use `/total " + skirmish.getIndex() + "` to add your total.");

			printSkirmishSummary(event, skirmish);
			war.reduceRemainingQty();

			if (war.getRemainingQty() > 0) {
				Skirmish newSkirmish = new Skirmish(EncounterInfo.getEncounterIndex(), skirmish.getLength(), war.getInterval());
				EncounterInfo.encounterRegistry.put(newSkirmish.getIndex() % 50, newSkirmish);
				newSkirmish.setIsWar(true);
				runNextSkirmish(war, event, newSkirmish);
				EncounterInfo.incrementEncounterIndex();
			} else { //last skirmish
				printWarSummary(event, war);
			}
		}, skirmish.getLength() + skirmish.getStartTime(), TimeUnit.MINUTES);	
	}

	public void printSkirmishSummary(MessageCreateEvent event, Skirmish skirmish) {
		ScheduledExecutorService schedule = skirmish.getSchedule();

		schedule.schedule(() -> {

			skirmish.setExpired();
			skirmish.createMessage(event, skirmish.createParticipantSummary());

		}, BotUtil.MINUTES_TO_SUMMARY, TimeUnit.MINUTES);		
	}
	
	public void printWarSummary(MessageCreateEvent event, War war) {
		ScheduledExecutorService schedule = war.getSchedule();

		schedule.schedule(() -> {

			war.setExpired();
			EncounterInfo.currentWar = null;
			war.createMessage(event, war.createParticipantSummary());
			EncounterInfo.resetWarSummary();

		}, BotUtil.MINUTES_TO_SUMMARY, TimeUnit.MINUTES);		
	}
}
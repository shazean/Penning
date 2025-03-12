package bot.penning.commmands;

import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import bot.penning.BotUtil;
import bot.penning.EncounterInfo;
import bot.penning.encounters.Battle;
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

public class BattleCommand implements SlashCommand {

	static ArrayList<Object> writersEntered = new ArrayList<Object>();

	@Override
	public String getName() {
		return "battle";
	}

	@Override
	public Mono<Void> handle(ChatInputInteractionEvent event) {

		Double duration = event.getOption("time") //duration of battle
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asDouble).get();

		Long startTime = event.getOption("start") //how long from now the battle should begin
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asLong).get();

		Long warIndex = EncounterInfo.getEncounterIndex();
		Battle battle = new Battle(warIndex, duration, startTime);
		EncounterInfo.encounterRegistry.put(battle.getIndex() % 50, battle);
		GatewayDiscordClient client = event.getClient();
		Long finalTime;
		
		//stop user from creating a battle with length 0 (because calculating the average for total creates a divide by zero scenario)
		if (duration == 0) {
			return event.reply("Length of battle cannot be zero! Try again!").withEphemeral(true);
		}

		//Let's user know the length is too long
		if (duration < BotUtil.MIN_BATTLE_LENGTH_HRS) {
			return event.reply("Length is too short! Try starting a word battle instead.").withEphemeral(true);
		}
		if (duration > BotUtil.MAX_BATTLE_LENGTH_HRS) {
			return event.reply("Length is too long! A word battle cannot exceed " + BotUtil.MAX_BATTLE_LENGTH_HRS + " hours.").withEphemeral(true);
		}


		Button alertButton = Button.primary("alert_button_" + battle.getIndex(), "Ping me!");

		EncounterInfo.incrementEncounterIndex();

		client.on(ButtonInteractionEvent.class, embedEvent -> {
			if (embedEvent.getCustomId().equals("alert_button_" + battle.getIndex())) {
				Member writerMention = embedEvent.getInteraction().getMember().get();
				battle.addPingableMember(writerMention);
				return embedEvent.reply(writerMention.getNicknameMention() + ", you have joined alerts for the battle!");
			}
			else {
				return Mono.empty();
			}
		}).timeout(Duration.ofMinutes(startTime)).subscribe();

		battle.setIsWar(false);
		runBattle(event, battle);

		return event.reply("Battle #" + battle.getIndex() + " created for " + battle.getLength() + " minutes, and will start in " + battle.getStartTime() + " minutes.")
				.withComponents(ActionRow.of(alertButton));
	}


	public void runBattle(ChatInputInteractionEvent event, Battle battle) {
		GatewayDiscordClient client = event.getClient();
		Snowflake guildID = event.getInteraction().getGuildId().get();

		client.on(MessageCreateEvent.class, embedEvent -> {
			if (embedEvent.getMember().get().equals(client.getSelfMember(guildID).block())) { //if message was sent by ourselves
				String botMessage = embedEvent.getMessage().getContent().substring(0, 22 + getNumDigits(battle.getIndex())); //length based off how many digits the battle index is

				if (botMessage.equals("Battle #" + battle.getIndex() + " created for")) {

					ScheduledExecutorService schedule = battle.getSchedule();

					if (battle.getStartTime() > 1) {
						schedule.schedule(() -> {

							battle.createMessage(embedEvent, "Battle #" + battle.getIndex() + " starts in one minute!");

						}, battle.getStartTime() - 1, TimeUnit.MINUTES);	
					}
					
					schedule.schedule(() -> {

						battle.createMessage(embedEvent, "Battle #" + battle.getIndex() + " starts now! " + battle.getPingableMembers());

					}, battle.getStartTime(), TimeUnit.MINUTES);	
					
					long penningsWords = Math.abs(BotUtil.PENNING_WRITING_SPEED * battle.getLength() + ((int)(Math.random() * (50- -50+1)+ -50)));
//					Button totalButton = Button.primary("total-button", "Add your total!");

					schedule.schedule(() -> {

						battle.setComplete();
						battle.createMessage(embedEvent, "Battle #" + battle.getIndex() + " ends now! " + battle.getPingableMembers());
						battle.createMessage(embedEvent, "How much did you write? I wrote " + penningsWords + " words. Use `/total " + battle.getIndex() + "` to add your total. Summary in 8 minutes.");

						printSummary(embedEvent, battle);

					}, battle.getLength() + battle.getStartTime(), TimeUnit.MINUTES);		

				}	
			}
			return Mono.empty();
		}).timeout(Duration.ofMinutes(battle.getLength() + battle.getStartTime() + 1)).subscribe();
	}
	
	private int getNumDigits(long num) {
		return (int) (Math.log10(num) + 1);
	}

	public void printSummary(MessageCreateEvent event, Battle battle) {
		ScheduledExecutorService schedule = battle.getSchedule();

		schedule.schedule(() -> {

			//compile battle info TODO
			battle.setExpired();
			battle.createMessage(event, battle.createParticipantSummary());

		}, BotUtil.MINUTES_TO_SUMMARY, TimeUnit.MINUTES);		
	}
}
package bot.penning.encounters;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import discord4j.core.event.domain.message.MessageCreateEvent;

public class Battle {

	int battleIndex;
	int battleLengthTotalMinutes;
	int battleLengthHours;
	int battleLengthMinutes;
	int battleStartTime;
	Boolean complete = false;
	ScheduledExecutorService schedule = Executors.newScheduledThreadPool(3);


	public Battle() {
	}

	public Battle(int battleIndex) {
		this.battleIndex = battleIndex;
	}
	
	public Battle(int battleIndex, int lengthHours, int lengthMinutes, int startTime) {
		this.battleIndex = battleIndex;
		setLength(lengthHours, lengthMinutes);
		setStartTime(startTime);
	}

	public void setLength(int skirmishLengthHours, int skirmishLengthMinutes) {
		this.battleLengthHours = skirmishLengthHours;
		this.battleLengthMinutes = skirmishLengthMinutes;
		
		battleLengthTotalMinutes = (battleLengthHours * 60) + battleLengthMinutes;
	}

	public int getLength() {
		return battleLengthTotalMinutes;
	}
	
	public int getIndex() {
		return battleIndex;
	}

	public void setStartTime(int startTime) {
		battleStartTime = startTime;
	}

	public int getStartTime() {
		return battleStartTime;
	}
	
	public Boolean isComplete() {
		return complete;
	}
	
	public void setComplete() {
		complete = true;
	}

	public void createMessage(MessageCreateEvent event, String message) {
		event.getMessage().getChannel().block().createMessage(message).block();
	}

	public void runBattle(Battle battle, MessageCreateEvent event) {
		//FIXME
		
		int penningsWords;
		Random random = new Random();
		penningsWords = Math.abs(29 * battle.getLength() + ((int)(Math.random() * (50- -50 + 1) + -50)));
		
		
		createMessage(event, "Battle #" + battle.getIndex() + " created for " + battle.getLength() + " minutes, and will start in " + battle.getStartTime() + " minutes.");

		schedule.schedule(() -> {

			createMessage(event, "Battle #" + battle.getIndex() + " starts now!");

		}, battle.getStartTime(), TimeUnit.MINUTES);
		
		schedule.schedule(() -> {

			createMessage(event, "Battle #" + battle.getIndex() + " ends now!");
			createMessage(event, "How much did you write? I wrote " + penningsWords + " words.");
			createMessage(event, "Use `'!total " + battle.getIndex() + " [amount written]'` to add your total.");

		}, battle.getLength() + battle.getStartTime(), TimeUnit.MINUTES);
		
	}	
	
}

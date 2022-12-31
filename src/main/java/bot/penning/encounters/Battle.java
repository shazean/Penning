package bot.penning.encounters;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import discord4j.core.event.domain.message.MessageCreateEvent;

public class Battle extends Encounter {

	private Long battleLengthHours;
	private Long battleLengthMinutes;
	private Long battleLengthTotalMinutes;

	public Battle(Long index, Long lengthHours, Long start) {
		this(index, lengthHours, (Long) 0L, start);
	}


	public Battle(Long index, Long lengthHours, Long lengthMinutes, Long startTime) {
		super(index, lengthHours * 60 + lengthMinutes, startTime);
		this.index = index;
		setLength(lengthHours, lengthMinutes);
		setStartTime(startTime);
	}
	

	public void setLength(Long skirmishLengthHours, Long skirmishLengthMinutes) {
		this.battleLengthHours = skirmishLengthHours;
		this.battleLengthMinutes = skirmishLengthMinutes;
		
		this.battleLengthTotalMinutes = (battleLengthHours * 60) + battleLengthMinutes;
	}

//	public void runBattle(Battle battle, MessageCreateEvent event) {
//		//FIXME
//		
//		int penningsWords;
//		Random random = new Random();
//		penningsWords = Math.abs(29 * battle.getLength() + ((int)(Math.random() * (50- -50 + 1) + -50)));
//		
//		
//		createMessage(event, "Battle #" + battle.getIndex() + " created for " + battle.getLength() + " minutes, and will start in " + battle.getStartTime() + " minutes.");
//
//		schedule.schedule(() -> {
//
//			createMessage(event, "Battle #" + battle.getIndex() + " starts now!");
//
//		}, battle.getStartTime(), TimeUnit.MINUTES);
//		
//		schedule.schedule(() -> {
//
//			createMessage(event, "Battle #" + battle.getIndex() + " ends now!");
//			createMessage(event, "How much did you write? I wrote " + penningsWords + " words.");
//			createMessage(event, "Use `'!total " + battle.getIndex() + " [amount written]'` to add your total.");
//
//		}, battle.getLength() + battle.getStartTime(), TimeUnit.MINUTES);
//		
//	}	
	
}

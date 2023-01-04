package bot.penning.encounters;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import bot.penning.encounters.Encounter.Participant;
import discord4j.core.event.domain.message.MessageCreateEvent;

public class Battle extends Encounter {

	private Long battleLengthHours;
	private Long battleLengthMinutes;
	private Long battleLengthTotalMinutes;

	public Battle(Long index, Double lengthHours, Long start) {
		super(index, (long) (lengthHours * 60), start);
	}

	@Override
	public String createParticipantSummary() {
		participantSummary = "**Battle Summary:**\n\n";
		for (Participant i : enteredParticipants) {
			participantSummary += (i + "\n");
		}
		return participantSummary;
	}
	
	public Long getLengthHours() {
		return (long) Math.floor(getLength() / 60);
	}
	
	public Long getLengthMinutes() {
		return getLength() % 60;
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

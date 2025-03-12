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
	
//	public Long getLengthHours() {
//		return (long) Math.floor(getLength() / 60);
//	}
//	
//	public Long getLengthMinutes() {
//		return getLength() % 60;
//	}
}

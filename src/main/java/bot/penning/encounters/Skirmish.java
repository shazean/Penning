package bot.penning.encounters;

import java.util.ArrayList;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import bot.penning.encounters.Encounter.Participant;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;

public class Skirmish extends Encounter {
	
	public Skirmish(Long index, Long length, Long start) {
		super(index, length, start);
	}
		
	@Override
	public String createParticipantSummary() {
		participantSummary = "**Skirmish Summary:**\n\n";
		for (Participant i : enteredParticipants) {
			participantSummary += (i + "\n");
		}
		return participantSummary;
	}
	
}
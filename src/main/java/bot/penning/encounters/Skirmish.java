package bot.penning.encounters;

import java.util.ArrayList;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;

public class Skirmish extends Encounter {
	
	public Skirmish() {
	}

	public Skirmish(Long index) {
		this.index = index;
	}
	
	public Skirmish(Long skirmishIndex, Long length, Long start) {
		this.index = skirmishIndex;
		setLength(length);
		setStartTime(start);
	}

	public void createMessage(MessageCreateEvent event, String message) {
		event.getMessage().getChannel().block().createMessage(message).block();
	}
	
}
package bot.penning;

import java.util.ArrayList;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import discord4j.core.event.domain.message.MessageCreateEvent;

public class Skirmish /*extends TimerTask*/ {
	
	int skirmishIndex;
	int skirmishLength;
	int skirmishStartTime;
	Boolean complete = false;
	ScheduledExecutorService schedule = Executors.newScheduledThreadPool(3);

	public Skirmish() {
	}

	public Skirmish(int skirmishIndex) {
		this.skirmishIndex = skirmishIndex;
	}
	
	public Skirmish(int skirmishIndex, int length, int start) {
		this.skirmishIndex = skirmishIndex;
		setLength(length);
		setStartTime(start);
	}

	public void setLength(int skirmishLength) {
		this.skirmishLength = skirmishLength;

	}

	public int getLength() {
		return skirmishLength;
	}
	
	public int getIndex() {
		return skirmishIndex;
	}

	public void setStartTime(int startTime) {
		skirmishStartTime = startTime;
	}

	public int getStartTime() {
		return skirmishStartTime;
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

	public void runSkirmish(Skirmish skirmish, MessageCreateEvent event) {
		//message: skirmish created
		//wait until skirmish starts
		//start skirmish
		//wait until skirmish ends
		//end skirmish
		//ask for total
		int penningsWords;
		Random random = new Random();
		penningsWords = Math.abs(29 * skirmish.getLength() + ((int)(Math.random() * (50- -50+1)+ -50)));
		
		
		createMessage(event, "Skirmish #" + skirmish.getIndex() + " created for " + skirmish.getLength() + " minutes, and will start in " + skirmish.getStartTime() + " minutes.");

		schedule.schedule(() -> {

			createMessage(event, "Skirmish #" + skirmish.getIndex() + " starts now!");

		}, skirmish.getStartTime(), TimeUnit.MINUTES);
		
		schedule.schedule(() -> {

			createMessage(event, "Skirmish #" + skirmish.getIndex() + " ends now!");
			createMessage(event, "How much did you write? I wrote " + penningsWords + " words.");
			createMessage(event, "Use `'!total " + skirmish.getIndex() + " [amount written]'` to add your total.");


		}, skirmish.getLength() + skirmish.getStartTime(), TimeUnit.MINUTES);
		

	}
	
	public void printSkirmishSummary(Skirmish skirmish, MessageCreateEvent event, ArrayList writersSubmitted) {
		int i;
		
		createMessage(event, "#" + skirmish.getIndex() + " Summary:");
		for (i = 0; i < writersSubmitted.size(); i += 3) {
			createMessage(event, writersSubmitted.get(i) + " wrote " + writersSubmitted.get(i + 1) + " " + writersSubmitted.get(i + 2) + " for an average of " + ((float)writersSubmitted.get(i + 1) / skirmish.getLength()) + " " + writersSubmitted.get(i + 2) + " per minute.");
			
			
		}

		
	}
	
}
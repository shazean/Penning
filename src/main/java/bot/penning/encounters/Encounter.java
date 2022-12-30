package bot.penning.encounters;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import discord4j.core.event.domain.message.MessageCreateEvent;

public class Encounter {

	Long index;
	Long length;
	Long startTime;
	Boolean complete = false;
	Boolean expired = false;
	ScheduledExecutorService schedule = Executors.newScheduledThreadPool(3);

	
	public void setLength(Long length) {
		this.length = length;

	}

	public Long getLength() {
		return length;
	}
	
	public Long getIndex() {
		return index;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getStartTime() {
		return startTime;
	}
	
	public Boolean isComplete() {
		return complete;
	}
	
	public void setComplete() {
		complete = true;
	}
	
	public Boolean isExpired() {
		return expired;
	}
	
	public void setExpired() {
		expired = true;
	}
	
	public ScheduledExecutorService getSchedule() {
		return schedule;
	}

	public void createMessage(MessageCreateEvent event, String message) {
		event.getMessage().getChannel().block().createMessage(message).block();
	}
	
}

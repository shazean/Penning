package bot.penning;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import discord4j.core.event.domain.message.MessageCreateEvent;

public class Battle {

	int battleIndex;
	int battleLength;
	int battleStartTime;
	Boolean complete = false;
	ScheduledExecutorService schedule = Executors.newScheduledThreadPool(3);


	public Battle() {
	}

	public Battle(int battleIndex) {
		this.battleIndex = battleIndex;
	}
	
	public Battle(int battleIndex, int length, int time) {
		this.battleIndex = battleIndex;
		setLength(length);
		setStartTime(time);
	}

	public void setLength(int skirmishLength) {
		this.battleLength = skirmishLength;

	}

	public int getLength() {
		return battleLength;
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
	}	
	
}

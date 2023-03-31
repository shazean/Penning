package bot.penning.encounters;

import java.util.ArrayList;
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
	public ArrayList<Participant> enteredParticipants = new ArrayList<Participant>();
	String participantSummary;


	public Encounter(Long index, Long length, Long start) {
		this.index = index;
		setLength(length);
		setStartTime(start);
	}

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
	
	public void createParticipant(String nickname, Long totalWords, Double averageWPM, String writtenType, String writtenTypeAbbr) {
		Participant participant = new Participant(nickname, totalWords, averageWPM, writtenType, writtenTypeAbbr);
		enteredParticipants.add(participant);
	}
	
	public String createParticipantSummary() {
		participantSummary = "**Summary:**\n";
		for (Participant i : enteredParticipants) {
			participantSummary += (i + "\n");
		}
		
		return participantSummary;
	}	
	
	public class Participant {
		String nickname;
		Long totalWords;
		Double averageWPM;
		String writtenType;
		String writtenTypeAbbr;
		Long timeToGoal;
		
		public Participant(String nickname, Long totalWords, Double averageWPM, String writtenType, String writtenTypeAbbr) {
			this.nickname = nickname;
			this.totalWords = totalWords;
			this.averageWPM = averageWPM;
			this.writtenType = writtenType;
			this.writtenTypeAbbr = writtenTypeAbbr;
		}
		
		public Participant(String nickname, Long totalWords, Double averageWPM, Long timeToGoal) {
			this.nickname = nickname;
			this.timeToGoal = timeToGoal;
		}
		
		public String toString() {
			return nickname + ": " + totalWords + " " + writtenType + " (" + averageWPM + " " + writtenTypeAbbr + ")";
			
		}
		
		public String onslaughtToString() {
			return nickname + ": " + timeToGoal + " minutes (" + averageWPM + " wpm)";
 		}
		
	}
	
}

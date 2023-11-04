package bot.penning.encounters;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;

public class Encounter {

	Long index;
	Long length;
	Long startTime;
	Boolean complete = false;
	Boolean expired = false;
	ScheduledExecutorService schedule = Executors.newScheduledThreadPool(3);
	public ArrayList<Participant> enteredParticipants = new ArrayList<Participant>();
	public ArrayList<Member> pingableMembers = new ArrayList<Member>();
	String participantSummary;
	String pingList;


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
	
	public void createParticipant(Member user, Long totalWords, Double averageWPM, String writtenType, String writtenTypeAbbr) {
		Participant participant = new Participant(user, totalWords, averageWPM, writtenType, writtenTypeAbbr);
		enteredParticipants.add(participant);
	}
	
	public boolean hasParticipantAlready(Member user) {
		for (Participant i : enteredParticipants) {
			if (i.user == user) return true;
		}
		return false;
	}
	
	public String createParticipantSummary() {
		participantSummary = "**Summary:**\n";
		for (Participant i : enteredParticipants) {
			participantSummary += (i + "\n");
		}
		
		return participantSummary;
	}	
	
	public String getPingableMembers() {
		pingList = "";
		for (Member i : pingableMembers) {
			pingList += (i.getNicknameMention() + " ");
		}
		return pingList;
	}
	
	public void addPingableMember(Member user) {
		pingableMembers.add(user);
	}
 
	
	public class Participant {
		String mentionNickname;
		Long totalWords;
		Double averageWPM;
		String writtenType;
		String writtenTypeAbbr;
		Long timeToGoal;
		Member user;
		
		public Participant(Member user, Long totalWords, Double averageWPM, String writtenType, String writtenTypeAbbr) {
			this.user = user;
			this.mentionNickname = user.getNicknameMention();
			this.totalWords = totalWords;
			this.averageWPM = averageWPM;
			this.writtenType = writtenType;
			this.writtenTypeAbbr = writtenTypeAbbr;
		}
		
		public Participant(Member user, Long totalWords, Double averageWPM, Long timeToGoal) {
			this.user = user;
			this.mentionNickname = user.getNickname().get();
			this.timeToGoal = timeToGoal;
		}
		
		public String toString() {
			return mentionNickname + ": " + totalWords + " " + writtenType + " (" + averageWPM + " " + writtenTypeAbbr + ")";
			
		}
		
		public String onslaughtToString() {
			return mentionNickname + ": " + timeToGoal + " minutes (" + averageWPM + " wpm)";
 		}	
	}
}

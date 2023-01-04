package bot.penning.encounters;

import java.util.ArrayList;

import bot.penning.encounters.Encounter.Participant;

public class Onslaught extends Encounter {

	public Onslaught(Long index, Long length, Long start) {
		super(index, length, start);
		// TODO Auto-generated constructor stub
	}
	
	public Long getGoal() {
		return length;
	}
	
	public void createParticipant(String nickname, Long totalWords, Double averageWPM, Long timeToGoal) {
		Participant participant = new Participant(nickname, totalWords, averageWPM, timeToGoal);
		enteredParticipants.add(participant);
	}
	
	@Override
	public String createParticipantSummary() {
		participantSummary = "**Summary:**\n\n";
		for (Participant i : enteredParticipants) {
			participantSummary += (i.onslaughtToString() + "\n");
		}
		
		return participantSummary;
	}	
	
	
}

package bot.penning.encounters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bot.penning.WritingType;
import discord4j.core.object.entity.Member;

public class War extends Encounter {

	Long quantity;
	Long remainingQuantity;
	Long interval;
	public Map<Member, Participant> warriors = new HashMap<>();
	public ArrayList<Skirmish> skirmishes = new ArrayList<>();
	
	public War(Long index, Long length, Long start) {
		this(index, length, start, 1L, start);
	}

	public War(Long index, Long length, Long start, Long quantity, Long interval) {
		super(index, length, start);
		setQuantity(quantity);
		remainingQuantity = quantity;
		this.setIsWar(true);
		this.interval = interval;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}
	
	public Long getQuantity() {
		return quantity;
	}
	
	public void reduceRemainingQty() {
		remainingQuantity--;
	}
	
	public Long getRemainingQty() {
		return remainingQuantity;
	}

	public void setInterval(Long interval) {
		this.interval = interval;
	}
	
	public Long getInterval() {
		return interval;
	}
	
	@Override
	public void createParticipant(Member member, Long totalWords, Double averageWPM, WritingType writtenType) {
		if (warriors.containsKey(member)) {
			Participant warrior = warriors.get(member);
			warrior.updateWrittenInWar(writtenType, totalWords);
		} else {			
			Participant participant = new Participant(member, totalWords, averageWPM, writtenType);
			warriors.put(member, participant);
		}
	}

	@Override
	public String createParticipantSummary() {
		participantSummary = "**War Summary:**\n";
		for (Participant i : warriors.values()) {
			participantSummary += (i.warToString() + "\n");
		}
		return participantSummary;
	}
}
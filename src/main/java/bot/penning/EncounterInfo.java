package bot.penning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import bot.penning.encounters.Encounter;
import bot.penning.encounters.Encounter.Participant;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

public class EncounterInfo {

//	public static ArrayList<Skirmish> skirmishes = new ArrayList<Skirmish>();
//	public static ArrayList<Battle> battles = new ArrayList<Battle>();
//	public static ArrayList<War> wars = new ArrayList<War>();
//	public static ArrayList<Sprint> sprints = new ArrayList<Sprint>();
	public static final Map<Member, Goal> writerIndexTemp = new HashMap<>();
	
	public static final Map<Member, Writer> writerIndex = new HashMap<>();
	public static Map<Long, Encounter> encounterRegistry = new HashMap<>();
	public static Boolean warRunning = false;
//	private static WarSummary currentWarSummary;
//	public ArrayList<Participant> enteredWarriors = new ArrayList<Participant>();
	public static Map<Member, Warrior> warriorsEntered = new HashMap<>();
	public static ArrayList<Object> writersEntered = new ArrayList<Object>();

	static Long encounterIndex = 1L;

	public void setEncounterIndex(Long index) {
		encounterIndex = index;
	}

	public static void incrementEncounterIndex() {
		encounterIndex++;
	}

	public static Long getEncounterIndex() {
		return encounterIndex;
	}
	
	public static Boolean isWarRunning() {
		return warRunning;
	}
	
	public static void setWarRunning(Boolean stateOfWar) {
		warRunning = stateOfWar;
	}
	
	public static void resetWarSummary() {
		warriorsEntered = new HashMap<>();
	}
	
	public static void addToWarSummary(Member user, Long totalWritten, double wordsPerMin, String goalType) {
		if (!warriorsEntered.containsKey(user)) {
			warriorsEntered.put(user, new Warrior(user));
		} else {
			if (goalType.equals("words")) {
				warriorsEntered.get(user).addWords(totalWritten, wordsPerMin);
			} else if (goalType.equals("lines")) {
				warriorsEntered.get(user).addLines(totalWritten, wordsPerMin);
			} else if (goalType.equals("pages")) {
				warriorsEntered.get(user).addPages(totalWritten, wordsPerMin);
			} else {
				warriorsEntered.get(user).addMinutes(totalWritten);
			}
		}
	}
	
	public static String createWarSummary() {
		String warSummary = "**War Summary:**\n";
		
		for (Warrior i : warriorsEntered.values()) {
			warSummary += (i.toString() + "\n");
		}
		
		return warSummary;
	}
		
	protected static class Warrior {
		Member user;
		Long totalWordsWritten;
		double averageWPM;
		Long totalPagesWritten;
		double averagePPM;
		Long totalLinesWritten;
		double averageLPM;
		Long totalMinutesWritten;
		
		public Warrior(Member user) {
			this.user = user;
			totalWordsWritten = 0L;
			averageWPM = 0L;
			totalPagesWritten = 0L;
			averagePPM = 0L;
			totalLinesWritten = 0L;
			averageLPM = 0L;
			totalMinutesWritten = 0L;
		}
	
		public void addWords(Long words, double average) {
			totalWordsWritten += words;
			averageWPM = (averageWPM + average ) / 2;
		}
		
		public void addPages(Long pages, double average) {
			totalPagesWritten += pages;
			averagePPM = (averageWPM + average ) / 2;
		}
		
		public void addLines(Long lines, double average) {
			totalLinesWritten += lines;
			averageLPM = (averageWPM + average ) / 2;
		}
		
		public void addMinutes(Long minutes) {
			totalMinutesWritten += minutes;
		}
		
		public String toString() {
			String warriorSummary = user.getNicknameMention() + ": ";
			
			if (totalWordsWritten > 0) {
				warriorSummary += totalWordsWritten + " total words (" + averageWPM + " wpm) ";
			}
			if (totalLinesWritten > 0) {
				warriorSummary += totalLinesWritten + " total lines (" + averageLPM + " lpm) ";
			}
			if (totalPagesWritten > 0) {
				warriorSummary += totalPagesWritten + " total pages (" + averagePPM + " ppm) ";
			}
			if (totalMinutesWritten > 0) {
				warriorSummary +=  totalMinutesWritten + " total minutes\n";
			}
			
			return warriorSummary;
		}
		
	}
}
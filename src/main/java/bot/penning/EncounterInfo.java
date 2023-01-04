package bot.penning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import bot.penning.encounters.Encounter;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

public class EncounterInfo {

//	public static ArrayList<Skirmish> skirmishes = new ArrayList<Skirmish>();
//	public static ArrayList<Battle> battles = new ArrayList<Battle>();
//	public static ArrayList<War> wars = new ArrayList<War>();
//	public static ArrayList<Sprint> sprints = new ArrayList<Sprint>();
	public static final Map<Optional<Member>, Goal> writerIndexTemp = new HashMap<>();
	
	public static final Map<Optional<Member>, Writer> writerIndex = new HashMap<>();
	public static Map<Long, Encounter> warRegistry = new HashMap<>();
	public static Boolean warRunning = false;


	public static ArrayList<Object> writersEntered = new ArrayList<Object>();


	static Long warIndex = 1L;

	public void setWarIndex(Long index) {
		warIndex = index;
	}

	public static void incrementWarIndex() {
		warIndex++;
	}

	public static Long getWarIndex() {
		return warIndex;
	}
	
	public static Boolean isWarRunning() {
		return warRunning;
	}
	
	public static void setWarRunning(Boolean stateOfWar) {
		warRunning = stateOfWar;
	}

}

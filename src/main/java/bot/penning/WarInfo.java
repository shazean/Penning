package bot.penning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

public class WarInfo {

	public static ArrayList<Skirmish> skirmishes = new ArrayList<Skirmish>();
	public static ArrayList<Battle> battles = new ArrayList<Battle>();
	public static ArrayList<War> wars = new ArrayList<War>();
	public static ArrayList<Sprint> sprints = new ArrayList<Sprint>();
	public static final Map<Optional<Member>, Goal> writerIndex = new HashMap<>();

	
	public static ArrayList<Object> writersEntered = new ArrayList<Object>();

	
	static int warIndex = 0;
	
	public void setWarIndex(int index) {
		warIndex = index;
	}
	
	public static void incrementWarIndex() {
		warIndex++;
	}
	
	public static int getWarIndex() {
		return warIndex;
	}
	
}

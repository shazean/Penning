package bot.penning.quests;

import java.util.Random;

import bot.penning.Goal;

public class Quest {
	
	Long questObjective;
	String questGoalType;
	Goal questGoal;
	
	public Quest() {
		this(0L, "words");
	}
	
	public Quest(Long questObjective) {
		this(questObjective, "words");
	}
	
	public Quest(Long questObjective, String questGoalType) {
		this.questObjective = questObjective;
		this.questGoalType = questGoalType;
		questGoal = new Goal(questObjective);
	}
	
	public String toString() {
		return "Write " + questObjective + " " + questGoalType + "!";
	}
	
	public void generateQuest() {
		Random rand = new Random();
		questObjective = rand.nextInt(400) + 100L;
	}
	
	public void generateQuest(Goal goal) {
		
	}
	
	public Goal getQuestGoal() {
		return questGoal;
	}

}

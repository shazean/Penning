package bot.penning.quests;

import java.util.Random;

import bot.penning.Goal;
import bot.penning.WritingType;

public class Quest {
	
	Long questObjective;
	WritingType questGoalType;
	Goal questGoal;
	
	public Quest() {
		this(0L, WritingType.WORDS);
	}
	
	public Quest(Long questObjective) {
		this(questObjective, WritingType.WORDS);
	}
	
	public Quest(Long questObjective, WritingType questGoalType) {
		this.questObjective = questObjective;
		this.questGoalType = questGoalType;
		questGoal = new Goal(questObjective, questGoalType);
	}
	
	public Quest(Long goalAmount, String goalType) {
		this(goalAmount, getQuestType(goalType));
	}
	
	public static WritingType getQuestType(String goalType) {
		for (WritingType type : WritingType.values()) {
			if (type.getType().equals(goalType)) 
				return type;
		
		}
		return WritingType.WORDS;
	}
	
	public String toString() {
		return "Write " + questObjective + " " + questGoalType + "!";
	}
	
	public void generateQuest() {
		Random rand = new Random();
		int randNum = rand.nextInt(400);
		questObjective = randNum + 100L;
		questGoal.setGoal(questObjective);
	}
	
	public void generateQuest(Goal goal) {
		
	}
	
	public Goal getQuestGoal() {
		return questGoal;
	}

}
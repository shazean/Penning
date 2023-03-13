package bot.penning.quests;

import java.util.Random;

import bot.penning.Goal;

public class GoalQuest extends Quest {

	public GoalQuest() {
		super();
	}

	public GoalQuest(Long questObjective) {
		super(questObjective);
		// TODO Auto-generated constructor stub
	}

	public GoalQuest(Long questObjective, String questGoalType) {
		super(questObjective);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void generateQuest(Goal goal) {
		Random rand = new Random();

		if (goal.getGoalPercent() > .75 && goal.getGoalPercent() < .85 && (rand.nextInt(2) == 0)) {
			//if goal has between 15% and 25% remaining
			//33% chance of getting remaining goal as quest
			questObjective = goal.getRemaining();		
		} else { 
			//else, 15% to 40% of goal total is quest
			questObjective = (long) (goal.getGoal() * ((rand.nextInt(25) + 15.0) / 100.0));
		}
		questGoal.setGoal(questObjective);
	}
}

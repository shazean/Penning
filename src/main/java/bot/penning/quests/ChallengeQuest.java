package bot.penning.quests;

import java.util.Random;

import bot.penning.Goal;

public class ChallengeQuest extends Quest {

	Long questTime;
	Boolean hasTimeLimit = false;

	public ChallengeQuest() {
		super();
	}

	public ChallengeQuest(Long questObjective) {
		super(questObjective);
		// TODO Auto-generated constructor stub
	}

	public ChallengeQuest(Long questObjective, String questGoalType) {
		super(questObjective);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		if (hasTimeLimit) {
			return "Write " + questObjective + " " + questGoalType + " in " + questTime + " minutes! Try starting a skirmish or battle!"; //TODO add a time limit
		} else {
			return "Write " + questObjective + " " + questGoalType + "!";
		}
	}

	@Override
	public void generateQuest(Goal goal) {
		Random rand = new Random();

		//get baseline words from goal
		//25% to 40% of goal total is quest
		questObjective = (long) (goal.getGoal() * ((rand.nextInt(15) + 25.0) / 100.0));

		if (rand.nextInt(2) == 0) {
			//timed quest

			//TODO
			//check if a user has an average WPM stored
			//if so, use that to calculate reasonable time
			//if not, use a base WPM
			//FIXME
			questTime = questObjective / 20;
			hasTimeLimit = true;


		} else {
			//extra words quest
			//additional 15% of goal
			questObjective += (long) (goal.getGoal() * (rand.nextInt(15) / 100.0));
		}
	}

	@Override
	public void generateQuest() {
		Random rand = new Random();
		if (rand.nextInt(2) == 0) {
			//timed quest
			questObjective = rand.nextInt(500) + 200L;
			questTime = questObjective / 20; //20WPM
			hasTimeLimit = true;

		} else {
			//extra words quest
			questObjective = rand.nextInt(500) + 200L;

		}
	}

	public Long getTimeLimit() {
		return questTime;
	}

	public Boolean isTimed() {
		return hasTimeLimit;
	}

}

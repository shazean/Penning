package bot.penning.quests;

public class ChallengeQuest extends Quest {

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
		return "Write " + questObjective + " " + questGoalType + "!"; //TODO add a time limit
	}
	
}

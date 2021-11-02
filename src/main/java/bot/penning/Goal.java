package bot.penning;

public class Goal {

	int goal;
	String goalType = "words";
	Boolean resetGoal = true;
	int progress;
	String goalAvg = "wpm";

	
	public Goal() {	
	}
	
	public Goal(int goalAmount) {
		setGoal(goalAmount);
	}
	
	public Goal(int goalAmount, String goalType) {
		setGoal(goalAmount, goalType);
	}

	public void setGoal(int goal, String goalType) {
		this.goal = goal;
		this.goalType = goalType;
	}

	public void setGoal(int goal) {
		this.goal = goal;
	}

	public int getGoal() {
		return goal;
	}

	public String getGoalType() {
		return goalType;
	}
	
	public void setProgress(int progress) {
		this.progress = progress;
	}
	
	public int getProgress() {
		return progress;
	}
	
	public void addWords(int progress) {
		this.progress += progress;
	}
	
	public String getGoalTypeAvg() {
		if (goalType.equals("words")) {
			goalAvg = "wpm";
		} else if (goalType.equals("lines")) {
			goalAvg = "lpm";
		} else if (goalType.equals("pages")) {
			goalAvg = "ppm";
		} else if (goalType.equals("minutes")) {
			goalAvg = "minutes";
		}
		
		return goalAvg;
	}
	
}

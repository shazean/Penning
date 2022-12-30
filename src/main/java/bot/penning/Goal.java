package bot.penning;

public class Goal {

	Long goal;
	String goalType = "words";
	Boolean resetGoal = true;
	Long progress;
	String goalAvg = "wpm";
	double progressPercent;
	Boolean goalComplete;


	public Goal() {	
	}

	public Goal(Long goalAmount) {
		setGoal(goalAmount);
	}

	public Goal(Long goalAmount, String goalType) {
		setGoal(goalAmount, goalType);
	}

	public void setGoal(Long goal, String goalType) {
		this.goal = goal;
		this.goalType = goalType;
		this.progress = 0L;
	}

	public void setGoal(Long goal) {
		this.goal = goal;
		this.progress = 0L;
	}

	public Long getGoal() {
		return goal;
	}

	public String getGoalType() {
		return goalType;
	}

	public void setProgress(Long words) {
		this.progress = words;

	}

	public Long getProgress() {
		return progress;
	}

	public void addWords(Long words) {
		this.progress += words;

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

	public double goalPercent() {
		this.progressPercent = (double)this.progress / (double)this.goal * 100.0;

		if (this.progressPercent == 100) {
			goalComplete = true;
		}

		return this.progressPercent;
	}


}

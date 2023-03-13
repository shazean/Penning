package bot.penning;

public class Goal {

	Long goal;
	String goalType = "words";
	Boolean resetGoal = true;
	Long progress;
	String goalAbbr = "wpm";
	double progressPercent;
	Boolean goalComplete = false;


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
		if (this.progress > goal) {
			goalComplete = true;
		}
	}

	public Long getProgress() {
		return progress;
	}
	
	public Long getRemaining() {
		return goal - progress;
	}

	public void addWords(Long words) {
		this.progress += words;

		if (this.progress > goal) {
			goalComplete = true;
		}
	}

	public String getGoalTypeAbbr() {
		if (goalType.equals("words")) {
			goalAbbr = "wpm";
		} else if (goalType.equals("lines")) {
			goalAbbr = "lpm";
		} else if (goalType.equals("pages")) {
			goalAbbr = "ppm";
		} else if (goalType.equals("minutes")) {
			goalAbbr = "minutes";
		}

		return goalAbbr;
	}

	public double getGoalPercent() {
		this.progressPercent = (double)this.progress / (double)this.goal * 100.0;

//		if (this.progressPercent == 100) {
//			goalComplete = true;
//		}

		return this.progressPercent;
	}
	
	public Boolean isComplete() {
		return goalComplete;
	}

}

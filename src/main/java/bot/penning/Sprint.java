package bot.penning;

public class Sprint {

	int index;
	int goal;
	int startTime;
	Boolean complete = false;

	public Sprint() {
	}

	public Sprint(int index) {
		this.index = index;
	}

	public void setGoal(int goal) {
		this.goal = goal;
	}
	
	public int getGoal() {
		return goal;
	}
	
	
	public int getIndex() {
		return index;
	}

	public void setStartTime(int startTime) {
		startTime = startTime;
	}

	public int getStartTime() {
		return startTime;
	}

	
	public Boolean isComplete() {
		return complete;
	}
	
	public void setComplete() {
		complete = true;
	}


	
}

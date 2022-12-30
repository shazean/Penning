package bot.penning.encounters;

public class War {

	int index;
	int length;
	int startTime;
	Boolean complete = false;

	public War() {
	}

	public War(int index) {
		this.index = index;
	}

	public void setLength(int length) {
		this.length = length;

	}

	public int getLength() {
		return length;
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

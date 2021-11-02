package bot.penning;

public class Battle {

	int battleIndex;
	int battleLength;
	int battleStartTime;
	Boolean complete = false;

	public Battle() {
	}

	public Battle(int battleIndex) {
		this.battleIndex = battleIndex;
	}

	public void setLength(int skirmishLength) {
		this.battleLength = skirmishLength;

	}

	public int getLength() {
		return battleLength;
	}
	
	public int getIndex() {
		return battleIndex;
	}

	public void setStartTime(int startTime) {
		battleStartTime = startTime;
	}

	public int getStartTime() {
		return battleStartTime;
	}
	
	public Boolean isComplete() {
		return complete;
	}
	
	public void setComplete() {
		complete = true;
	}

	
}

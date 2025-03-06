package bot.penning.collectibles;

import bot.penning.TaskType;

public class WeightedChance {
	
	int weight;
	TaskType task;
	
	public WeightedChance(TaskType task, int weight) {
		this.task = task;
		this.weight = weight;
	}

}

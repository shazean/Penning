package bot.penning.collectibles;

import bot.penning.TaskType;

public class Animal {

	String animalType;
	WeightedChance skirmishWeight;
	WeightedChance onslaughtWeight;
	WeightedChance questWeight;
	WeightedChance challengeWeight;
	WeightedChance goalWeight;
	String defaultArticle = "a";
	
	public Animal(String animalType, int skirmishChance, int onslaughtChance, int questChance, int challengeChance, int goalChance) {
		this.animalType = animalType;
		this.skirmishWeight = new WeightedChance(TaskType.SKIRMISH, skirmishChance);
		this.onslaughtWeight = new WeightedChance(TaskType.ONSLAUGHT, onslaughtChance);
		this.questWeight = new WeightedChance(TaskType.QUEST, questChance);
		this.challengeWeight = new WeightedChance(TaskType.CHALLENGE_QUEST, challengeChance);
		this.goalWeight = new WeightedChance(TaskType.GOAL, goalChance);
	}
	
	public WeightedChance getChance(TaskType type) {
		switch(type) {
		
		case SKIRMISH:
			return skirmishWeight;
		case ONSLAUGHT:
			return onslaughtWeight;
		case QUEST:
			return questWeight;
		case CHALLENGE_QUEST:
			return challengeWeight;
		case GOAL:
			return goalWeight;
		default:
			return skirmishWeight;
		}
	}
	
	public void setArticleToAn() {
		this.defaultArticle = "an";
	}
	
	public String getArticle() {
		return this.defaultArticle;
	}
	
	public String toString() {
		return this.animalType;
	}
}
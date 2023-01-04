package bot.penning;

import java.util.Optional;

import bot.penning.quests.Quest;
import discord4j.core.object.entity.Member;

public class Writer {

	String name;
	Optional<Member> user;
	Goal writerGoal;
	Double averageWPM;
	int totalXP;
	Rank currentRank;
	Long bestGoal;
	Boolean usingFlavorText = true;
	Boolean hasGoal = false;
	Quest currentQuest;
	Boolean hasQuest;


	public Writer(Optional<Member> discordUser) {
		user = discordUser;
		hasGoal = false;
		hasQuest = false;
	}

	public Writer(Optional<Member> discordUser, Goal writerGoal) {
		user = discordUser;
		this.writerGoal = writerGoal;
		hasGoal = true;
		hasQuest = false;
	}


	public Optional<Member> getUser() {
		return user;
	}

	public void updateGoal(Goal newGoal) {
		writerGoal = newGoal;
	}

	public Goal getGoal() {
		return writerGoal;
	}	

	public Long getGoalNum() {
		return writerGoal.getGoal();
	}
	
	public void clearGoal() {
		writerGoal = null;
	}

	public void setFlavorText(Boolean flavorText) {
		usingFlavorText = flavorText;
	}

	public Boolean isUsingFlavorText() {
		return usingFlavorText;
	}

	public Boolean hasGoalSet() {
		return hasGoal;
	}

	public int getXP() {
		return totalXP;
	}

	public void updateXP(int newXP) {
		totalXP += newXP;
	}

	public void addQuest(Quest quest) {
		currentQuest = quest;
		hasQuest = true;
		System.out.println("Quest added! Quest is: " + quest.toString());
	}
	
	public void updateQuests(Long words) {
		currentQuest.getQuestGoal().addWords(words);
		if (currentQuest.getQuestGoal().isComplete()) {
			hasQuest = false;
		}
	}
	
	public Quest getQuest() {
		return currentQuest;
	}
	
	public Boolean hasQuest() {
		return hasQuest;
	}

}

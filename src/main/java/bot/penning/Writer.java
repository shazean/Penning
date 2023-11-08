package bot.penning;

import java.util.ArrayList;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import bot.penning.quests.ChallengeQuest;
import bot.penning.quests.Quest;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.Channel;

public class Writer {

	String name;
	Member user;
	Goal writerGoal;
	ArrayList<Double> averageWPM;
	int averageWPMIndex;
	int totalXP;
	int maxWPMsRecord = 20;
	Rank currentRank;
	Long bestGoal;
	Boolean usingFlavorText = true;
	Boolean hasGoal = false;
	Quest currentQuest;
	ChallengeQuest currentChallengeQuest;
	Boolean hasQuest;
	Boolean hasChallengeQuest;
	TimeZone timeZone;
	int dailyRewards;
	Channel preferedChannel;
	AnimalData animalData;

	public Writer(Member member) {
		this.user = member;
		hasGoal = false;
		hasQuest = false;
		hasChallengeQuest = false;
		averageWPM = new ArrayList<Double>(10);
		averageWPMIndex = 0;
		animalData = new AnimalData();
	}


	public Writer(Member member, Goal writerGoal) {
		user = member;
		this.writerGoal = writerGoal;
		hasGoal = true;
		hasQuest = false;
		hasChallengeQuest = false;
		averageWPM = new ArrayList<Double>(10);
		averageWPMIndex = 0;
		animalData = new AnimalData();
	}

	public Member getUser() {
		return user;
	}
	
	public void setPreferedChannel(Channel channel) {
		preferedChannel = channel;
	}
	
	public Channel getPreferredChannel() {
		return preferedChannel;
	}
	
	public AnimalData getAnimalData() {
		return animalData;
	}

	public void updateAverageWPM(Double averageWPM) {
		if (this.averageWPM.size() >= maxWPMsRecord) {
			this.averageWPM.set(averageWPMIndex, averageWPM);
			averageWPMIndex++;
			if (averageWPMIndex >= maxWPMsRecord) {
				averageWPMIndex = 0;
			}
		} else {
			this.averageWPM.add(averageWPM);
		}
	}

	public int getAverageWPM() {
		if (averageWPM.size() < 5) { //if we haven't established at least 5 average wpms
			return 12;
		}

		int tempAverageWPM = 0;

		for(int i = 0; i < this.averageWPM.size(); i++) {
			tempAverageWPM += this.averageWPM.get(i);
		}
		tempAverageWPM = tempAverageWPM / this.averageWPM.size();

		return tempAverageWPM;
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
		hasGoal = false;
	}

	public void addGoal(Goal goal) {
		this.writerGoal = goal;
		hasGoal = true;
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
	}

	public void updateQuests(Long words) {
		currentQuest.getQuestGoal().addWords(words);
		if (currentQuest.getQuestGoal().isComplete()) {
			ScheduledExecutorService schedule = Executors.newScheduledThreadPool(1);
			schedule.schedule(() -> {
				hasQuest = false;
			}, 1, TimeUnit.SECONDS);		
		}
	}

	public Quest getQuest() {
		return currentQuest;
	}

	public Boolean hasQuest() {
		return hasQuest;
	}

	public void addChallengeQuest(ChallengeQuest quest) {
		currentChallengeQuest = quest;
		hasChallengeQuest = true;
	}

	public void updateChallengeQuests(Boolean isTimed, Long words) {
		if (isTimed) { //assume we only get this far if the words were written in the needed time frame
			if (words > currentChallengeQuest.getQuestGoal().getGoal()) { //quest only updated if needed words were written
				currentChallengeQuest.getQuestGoal().addWords(words);
				if (currentChallengeQuest.getQuestGoal().isComplete()) {
					ScheduledExecutorService schedule = Executors.newScheduledThreadPool(1);
					schedule.schedule(() -> {
						hasChallengeQuest = false;
					}, 1, TimeUnit.SECONDS);		
				}
			}
		} else { //untimed quest, can just update the words & mark completed like regular quest
			currentChallengeQuest.getQuestGoal().addWords(words);
			if (currentChallengeQuest.getQuestGoal().isComplete()) {
				ScheduledExecutorService schedule = Executors.newScheduledThreadPool(1);
				schedule.schedule(() -> {
					hasChallengeQuest = false;
				}, 1, TimeUnit.SECONDS);		
			}
		}
	}

	public ChallengeQuest getChallengeQuest() {
		return currentChallengeQuest;
	}

	public Boolean hasChallengeQuest() {
		return hasChallengeQuest;
	}
	
	public int calculateRewards() {
		Goal goal = getGoal();
	
		if (goal == null) return 0;

		Double percent = goal.getGoalPercent();
		if (percent < 50) { //no rewards if less than 50% completed
			return 0;
		}
		if (percent > 100) {
			return 75;
		}
		return (int) (percent / 2);
	}
	
	public void setTimezone(TimeZone timeZone) {
		timeZone = this.timeZone;
	}
	
	public TimeZone getTimeZone() {
		return timeZone;
	}

}

package bot.penning;

import java.util.Optional;

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
	
	public Writer(Optional<Member> discordUser, Goal writerGoal) {
		user = discordUser;
		this.writerGoal = writerGoal;
	}
	
	
	public Optional<Member> getUser() {
		return user;
	}
	
	public void setFlavorText(Boolean flavorText) {
		usingFlavorText = flavorText;
	}
	
	public Boolean isUsingFlavorText() {
		return usingFlavorText;
	}
	
	
	public int getXP() {
		return totalXP;
	}
	
	public void updateXP(int newXP) {
		totalXP += newXP;
	}
	
		
}

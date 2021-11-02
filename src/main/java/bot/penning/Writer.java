package bot.penning;

import java.util.Optional;

import discord4j.core.object.entity.Member;

public class Writer {

	String name;
	Optional user;
	Goal writerGoal;
	
	public Writer(Optional<Member> discordUser, Goal writerGoal) {
		user = discordUser;
		this.writerGoal = writerGoal;
	}
	
	
	public Optional getUser() {
		return user;
	}
	
}

package bot.penning.listeners;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bot.penning.Bot;
import bot.penning.EncounterInfo;
import bot.penning.Writer;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.MessageChannel;
import reactor.core.publisher.Mono;

public class MidnightListener {

	ScheduledExecutorService schedule = Executors.newScheduledThreadPool(1);
	TimeZone currentTimeZone = TimeZone.getTimeZone("America/Chicago");
	Calendar rightNow = Calendar.getInstance(currentTimeZone); //get the current time in Chicago
	GatewayDiscordClient client;

	//	protected Map<Integer, ArrayList<Writer>> timezoneMap = new HashMap<>();
	protected ArrayList<ArrayList<Writer>> timezoneMap = new ArrayList<ArrayList<Writer>>();
	//FIXME assumes all writers have the same midnight currently

	public MidnightListener(GatewayDiscordClient client) {
		this.client = client;

	}

	public void begin() {

		int offset = doInitialOffset();
		System.out.println("Initial offset calculated.  Offset is: " + offset + "*********");
		schedule.schedule(() -> {

			beginListening();
			System.out.println("Listening begun.*******");


		}, offset + 1, TimeUnit.MINUTES);
	}

	public int doInitialOffset() {
		rightNow = Calendar.getInstance(currentTimeZone);
		Date date = rightNow.getTime();

		int timeFromMidnight = 60 - date.getMinutes();

		return timeFromMidnight;
		//		return 2;
	}

	public void beginListening() {
		//calculate rewards
		//grant rewards
		//wait 60 minutes
		//repeat
		rightNow = Calendar.getInstance(currentTimeZone);
		Date date = rightNow.getTime();
		int currentHour = date.getHours();
		System.out.println("Current hour is: " + currentHour + "*********");


		
		if (currentHour == 17) {
			System.out.println("Current hour is 14.******");

			grantRewards();

		}

		schedule.schedule(() -> {

			beginListening();

		}, 1, TimeUnit.HOURS);	

	}

	public void grantRewards() {
		for (Entry<Optional<Member>, Writer> entry : EncounterInfo.writerIndex.entrySet()) { //all of the writers that have interacted with the bot
			Writer writer = entry.getValue();
			int writerRewards = writer.calculateRewards();
			writer.clearGoal(); //TODO when multiple goals are added, change this to only clear the daily goal.

			if (!writer.isUsingFlavorText()) { //user is not using flavor text, and does not get XP rewards
				return;
			}

			if (writerRewards > 0) { //grant rewards
				String nickname = writer.getUser().get().getDisplayName();
				Snowflake channelID = writer.getPreferredChannel().getId();

				entry.getValue().updateXP(writerRewards);

				client.getChannelById(channelID).ofType(MessageChannel.class).flatMap(channel -> channel.createMessage(nickname + ", you have earned " + writerRewards + " XP!"))
				.subscribe();

			} else {
				return; //don't send a message with writer rewards if reward is 0
			}

		}

	}

}
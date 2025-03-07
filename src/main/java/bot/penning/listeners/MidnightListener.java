package bot.penning.listeners;

import bot.penning.Bot;
import bot.penning.EncounterInfo;
import bot.penning.TaskType;
import bot.penning.Writer;
import bot.penning.collectibles.Animal;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.MessageChannel;

import static java.util.Map.entry;

import java.time.ZoneId;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class MidnightListener {

	ScheduledExecutorService schedule = Executors.newScheduledThreadPool(1);
//	TimeZone currentTimeZone = TimeZone.getTimeZone("America/Chicago");
	Calendar rightNow; // = Calendar.getInstance(currentTimeZone); //get the current time in Chicago
	GatewayDiscordClient client;

	//	protected Map<Integer, ArrayList<Writer>> timezoneMap = new HashMap<>();
//	protected ArrayList<ArrayList<Writer>> timezoneMap = new ArrayList<ArrayList<Writer>>();
	//to future me… a hashmap instead of arraylist, with timezone as key? perhaps using an enum?
	protected HashMap<TimeZone, ArrayList<Writer>> timezoneMap;
	protected ArrayList<String> timezoneIds = new ArrayList<String>(Arrays.asList("Australia/Darwin", "Australia/Sydney", "America/Argentina/Buenos_Aires", "Africa/Cairo",
			"America/Anchorage", "America/Sao_Paulo", "Asia/Dhaka", "Africa/Harare", "America/St_Johns", "America/Chicago", "Asia/Shanghai", "Africa/Addis_Ababa", "Europe/Paris",
			"America/Indiana/Indianapolis", "Asia/Kolkata", "Asia/Tokyo", "Pacific/Apia", "Asia/Yerevan", "Pacific/Auckland", "Asia/Karachi", "America/Phoenix",
			"America/Puerto_Rico", "America/Los_Angeles", "Pacific/Guadalcanal", "Asia/Ho_Chi_Minh", "America/New_York", "America/Denver", "Pacific/Honolulu"));
	
	public MidnightListener(GatewayDiscordClient client) {
		this.client = client;
	}

	public void begin() {
		int offset = getInitialOffset();
		schedule.schedule(this::listen, offset + 1, TimeUnit.MINUTES);
	}

	public int getInitialOffset() {
		rightNow = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"));
		return 60 - rightNow.get(Calendar.MINUTE);
//		return 2;

	}

	public void listen() {
		//calculate rewards
		//grant rewards
		//wait 60 minutes
		//repeat
//		TimeZone currentZone = getTimezoneClosestToMidnight();
//		rightNow = Calendar.getInstance(currentZone);
//		rightNow = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"));
//
//		int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
//		int currentMin = rightNow.get(Calendar.MINUTE);
//
		createMessage(847148917056602132L, "listening. grant rewards now.");

		
//		if ((currentMin / 4) == 0) { //currentHour == 0
//			Bot.LOGGER.log(Level.WARNING, "listening" );
			grantRewards();
//		}

//		schedule.schedule(this::listen, 5, TimeUnit.MINUTES);
		schedule.schedule(this::listen, 1, TimeUnit.HOURS);

	}
	
	public TimeZone getTimezoneClosestToMidnight() {
		int currentHour = 1;
		int i = 0;
		TimeZone currentTimeZone = null;
		
		while(currentHour != 0) {
			currentTimeZone = TimeZone.getTimeZone(timezoneIds.get(i));
			rightNow = Calendar.getInstance(currentTimeZone);
			currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
			Bot.LOGGER.log(Level.INFO, "timezone: " + currentTimeZone.toZoneId().toString() + ", currentHour: " + currentHour + ", i: " + i + "/" + timezoneIds.size());
			i++;
		}
		
		return currentTimeZone;
	}

	public void grantRewards() {
		Random rand = new Random();
		
		createMessage(847148917056602132L, "grant rewards.");


		for (Entry<Member, Writer> entry : EncounterInfo.writerIndex.entrySet()) { //all of the writers that have interacted with the bot
			Writer writer = entry.getValue();

			TimeZone zone = writer.getTimeZone();
			if (Calendar.getInstance(zone).get(Calendar.HOUR_OF_DAY) != 0) {
				return;
			}

			if (!writer.hasGoalSet()) {
				return;
			}

			double percent = writer.getGoal().getGoalPercent();
			Snowflake channelID = writer.getPreferredChannel().getId();
			
			double chanceOfAnimal = rand.nextDouble();

//			if (chanceOfAnimal <= percent) {
				Animal animal = writer.getAnimalData().generateRandomAnimal(TaskType.GOAL);
				client.getChannelById(channelID).ofType(MessageChannel.class).flatMap(channel -> channel.createMessage(writer.getUser().getMention() + ", you have found " + animal.getArticle() + " " + animal.toString() + "!")).subscribe();
//			}
			
			createMessage(847148917056602132L, writer.getUser().getDisplayName() + " goal: " + writer.getGoal().getProgress() + "/" + writer.getGoal().getGoal() + " " + writer.getGoal().getGoalType() + ", chance of animal: " + chanceOfAnimal + "vs " + writer.getGoal().getGoalPercent() + "% of goal");
			

			writer.clearGoal(); //TODO when multiple goals are added, change this to only clear the daily goal.
			createMessage(847148917056602132L,writer.getUser().getDisplayName() + " goal cleared: " + writer.hasGoalSet());

		}
	}
	
	public void createMessage(Long channelId, String message) {
		client.getChannelById(Snowflake.of(channelId)).ofType(MessageChannel.class).flatMap(channel -> channel.createMessage(message)).subscribe();

	}
}
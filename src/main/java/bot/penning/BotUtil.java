package bot.penning;

public class BotUtil {
	
	public static final int minutesTilSummary = 8;
	public static final int secondDelayBeforeAnimalReward = 3;
	public static final int encountersBeforeReset = 50;
	public static final int maxSkirmishLengthMin = 60;
	public static final int maxTimeInFutureToStartEvent = 30;
	public static final int minBattleLengthHrs = maxSkirmishLengthMin / 60;
	public static final int maxBattleLengthHrs = 12;
	
	public static final int PENNING_WRITING_SPEED = 20;

	public static String dataFolder = "src/main/resources/data";
	
}

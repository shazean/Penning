package bot.penning;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import discord4j.common.ReactorResources;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.User;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.scheduler.Schedulers;

public class Bot {

	//	static HashMap writers = new HashMap();
	static int warIndex = 0;
	static ArrayList<Skirmish> skirmishes = new ArrayList<Skirmish>();
	static ArrayList<Battle> battles = new ArrayList<Battle>();
	static ArrayList<War> wars = new ArrayList<War>();
	static ArrayList<Sprint> sprints = new ArrayList<Sprint>();
	
	static ArrayList<Object> writersEntered = new ArrayList<Object>();
	
	static Boolean canSubmit = true;


	
	public static void main(String[] args) {

		//		  ReactorResources reactorResources = ReactorResources.builder()
		//				    .timerTaskScheduler(Schedulers.newParallel("my-scheduler"))
		//				    .blockingTaskScheduler(Schedulers.boundedElastic())
		//				    .build();

		GatewayDiscordClient client = DiscordClientBuilder.create("ODQ2ODUwOTEyMjM2NjAxMzU1.GqW0ye.nYT3RdQNuUerN3bs5ItHYY0zro9gOGkVo70GxM")
				//				  .setReactorResources(reactorResources)
				.build()
				.login()
				.block();

		
		client.getEventDispatcher().on(ReadyEvent.class)
		.subscribe(event -> {
			final User self = event.getSelf();
			System.out.println(String.format(
					"Logged in as %s#%s", self.getUsername(), self.getDiscriminator()
					));
		});

		client.getEventDispatcher().on(MessageCreateEvent.class)
		// subscribe is like block, in that it will *request* for action
		// to be done, but instead of blocking the thread, waiting for it
		// to finish, it will just execute the results asynchronously.
		.subscribe(event -> {
			// 3.1 Message.getContent() is a String
			final String content = event.getMessage().getContent();

			for (final Map.Entry<String, Command> entry : commands.entrySet()) {
				// We will be using ! as our "prefix" to any command in the system.
				if (content.startsWith('!' + entry.getKey())) {
					entry.getValue().execute(event);
					break;
				}
			}
		});

		//			    client.getEventDispatcher().on(MessageCreateEvent.class)
		//			        .map(MessageCreateEvent::getMessage)
		//			        .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
		//			        .filter(message -> message.getContent().equalsIgnoreCase("!ping"))
		//			        .flatMap(Message::getChannel)
		//			        .flatMap(channel -> channel.createMessage("Pong!"))
		//			        .subscribe();

		client.onDisconnect().block();		    


	}

	private static final Map<String, Command> commands = new HashMap<>();
	static final Map<Optional, Goal> writerIndex = new HashMap<>();
	//	    static final Map<warIndex, Battle> battleIndex = new HashMap<>();
	//	    static final Map<warIndex, Skirmish> skirmishIndex = new HashMap<>();



	static {
		//"!ping" responds with "Pong!"
		commands.put("ping", event -> event.getMessage().getChannel().block().createMessage("Pong!").block());

		//Get info/progress on goal
		commands.put("datagoal", event -> {

			if (writerIndex.containsKey(event.getMember())) {
				//				System.out.println(writerIndex.get(event.getMember()));
				//				System.out.println(writerIndex.get(event.getMember()).getGoal());
				//				System.out.println(writerIndex.get(event.getMember()).getGoalType());
				event.getMessage().getChannel().block().createMessage("You have written " + writerIndex.get(event.getMember()).getProgress() + " of " + writerIndex.get(event.getMember()).getGoal() + " " + writerIndex.get(event.getMember()).getGoalType()).block();
			}
			else {
				event.getMessage().getChannel().block().createMessage("Create a goal first!").block();
			}

		});

		//Create goal
		commands.put("goal", event -> {


			//If the writerIndex map contains an object with the key of the user who called the command,
			//then the object is completely cleared.
			//this makes it so creating a new goal replaces the last one, and a person can not have 2 goals at once.
			if (writerIndex.containsKey(event.getMember())) {
				writerIndex.remove(event.getMember());
			}


			final String content = event.getMessage().getContent();
			//			System.out.println("String content = event.getMessage().getContent(): " + content);
			String[] commandContent = content.split(" ");
			if (commandContent.length >= 3) {
				Goal writerGoal = new Goal(Integer.parseInt(commandContent[1]), commandContent[2]);
				event.getMessage().getChannel().block().createMessage("Goal of " + writerGoal.getGoal() + " " + writerGoal.getGoalType()
				+ " set!").block();

				writerIndex.put(event.getMember(), writerGoal);

				System.out.println("Created goal: " + writerGoal.getGoal() + " " + writerGoal.getGoalType());


			}
			else {
				Goal writerGoal = new Goal(Integer.parseInt(commandContent[1]));
				event.getMessage().getChannel().block().createMessage("Goal of " + writerGoal.getGoal() + " " + writerGoal.getGoalType()
				+ " set!").block();

				writerIndex.put(event.getMember(), writerGoal);

			}


//			System.out.println(writerIndex.values());

		});
		commands.put("resetgoal", event -> {

			if (writerIndex.containsKey(event.getMember())) {
				writerIndex.remove(event.getMember());
				event.getMessage().getChannel().block().createMessage("Goal has been reset!").block();
			} else {
				event.getMessage().getChannel().block().createMessage("There is no goal to reset! Create a goal!").block();
			}

		});

		commands.put("progress", event -> {
			int progress;

			final String content = event.getMessage().getContent();
			System.out.println("String content = event.getMessage().getContent(): " + content);
			String[] commandContent = content.split(" ");

			progress = Integer.parseInt(commandContent[1]);

			writerIndex.get(event.getMember()).setProgress(progress);

			event.getMessage().getChannel().block().createMessage("Progress updated! You have written " + writerIndex.get(event.getMember()).getProgress() + " " + writerIndex.get(event.getMember()).getGoalType() + " of " + writerIndex.get(event.getMember()).getGoal() + " " + writerIndex.get(event.getMember()).getGoalType()).block();

			if (writerIndex.get(event.getMember()).getProgress() / writerIndex.get(event.getMember()).getGoal() == 0) {
				
			}
						
			
		});

		commands.put("add", event -> {
			int progress;

			final String content = event.getMessage().getContent();
			System.out.println("String content = event.getMessage().getContent(): " + content);
			String[] commandContent = content.split(" ");

			progress = Integer.parseInt(commandContent[1]);

			writerIndex.get(event.getMember()).addWords(progress);

			event.getMessage().getChannel().block().createMessage("Progress updated! You have written " + writerIndex.get(event.getMember()).getProgress() + " " + writerIndex.get(event.getMember()).getGoalType() + " of " + writerIndex.get(event.getMember()).getGoal() + " " + writerIndex.get(event.getMember()).getGoalType()).block();

		});

		commands.put("info", event -> {

			event.getMessage().getChannel().block().createMessage("Word skirmish: set amount of brief time (i.e. 10 minutes), try to write as many words as you can. \nWord battle: Much like a skirmish, but over a longer period of time (i.e. by the end of the day) \nWord war: a series of skirmishes over a period of time, i.e. hours or days. \nWord sprint: get to a set amount of words as quickly as possible.").block();

		});

		commands.put("skirmish", event -> {
			
			System.out.println("warIndex: " + warIndex);
			
			int time = 0;
			int start = 0;
			ScheduledExecutorService schedule = Executors.newScheduledThreadPool(3);

			
			final String content = event.getMessage().getContent();
			String[] commandContent = content.split(" ");

			if (commandContent.length > 3) {
				event.getMessage().getChannel().block().createMessage("Invalid number of arguments. Try again!").block();
				return;
			}

			start = Integer.parseInt(commandContent[2]);
			time = Integer.parseInt(commandContent[1]);

			//stop user from creating a skirmish with length 0 (because calculating the average for total creates a divide by zero scenario)
			if (time == 0) {
				event.getMessage().getChannel().block().createMessage("Length of skirmish cannot be zero! Try again!");
				return;
			}

			//Let's user know the length is too long
			if (time > 60) {
				event.getMessage().getChannel().block().createMessage("Length is too long! Try starting a word battle instead.").block();
				return;
			}

			Skirmish skirmish = new Skirmish(warIndex, time, start);
			skirmishes.add(skirmish.getIndex(), skirmish);

			skirmish.runSkirmish(skirmish, event);
			skirmish.setComplete();
			warIndex++;

			schedule.schedule(() -> {

				skirmish.printSkirmishSummary(skirmish, event, writersEntered);

			}, skirmish.getLength() + skirmish.getStartTime(), TimeUnit.MINUTES);
			
		});

		commands.put("total", event -> {

			
			final String content = event.getMessage().getContent();
			String[] commandContent = content.split(" ");

			if (commandContent.length > 4) {
				event.getMessage().getChannel().block().createMessage("Invalid number of arguments. Try again!").block();
				return;
			}

			int totalWords;
			float averageWords;
			int length = 1;
			int index = Integer.parseInt(commandContent[1]);
			totalWords = Integer.parseInt(commandContent[2]);
			String typeWritten = null;
			ScheduledExecutorService schedule = Executors.newScheduledThreadPool(3);

			
			if (commandContent.length == 4) {
				
				typeWritten = commandContent[3];
				
			}

			System.out.println("index: " + index);
			System.out.println("skirmishes.get(index): " + skirmishes.get(index));

			
			try {

				if (skirmishes.get(index) != null) {
					if (skirmishes.get(index).isComplete())  {
						length = skirmishes.get(index).getLength();
						averageWords = (float)totalWords / (float)length;
						DecimalFormat df = new DecimalFormat("##.#");
						df.setRoundingMode(RoundingMode.DOWN);
						averageWords = Float.parseFloat(df.format(averageWords));
						
						writersEntered.add(event.getMember());
						writersEntered.add(totalWords);

						if (writerIndex.containsKey(event.getMember())) { //if user has created a goal; updates goal progress
							writerIndex.get(event.getMember()).addWords(totalWords);
							event.getMessage().getChannel().block().createMessage("You have written " + totalWords + " " + writerIndex.get(event.getMember()).getGoalType() + " for an average of " + averageWords + " " + writerIndex.get(event.getMember()).getGoalTypeAvg()).block();
							event.getMessage().getChannel().block().createMessage("Progress updated! You have written " + writerIndex.get(event.getMember()).getProgress() + " " + writerIndex.get(event.getMember()).getGoalType() + " of " + writerIndex.get(event.getMember()).getGoal() + " " + writerIndex.get(event.getMember()).getGoalType()).block();
							writersEntered.add(writerIndex.get(event.getMember()).getGoalType());
						} else if (typeWritten != null) { //if user does NOT have goal, and adds !total with a keyword (i.e. "lines");							
							event.getMessage().getChannel().block().createMessage("You have written " + totalWords + " " + typeWritten + " for an average of " + averageWords + " " + typeWritten + " per minute.").block();
							writersEntered.add(typeWritten);
						} else { //if user does NOT have goal, and does not specify with keyword; defaults to words
							event.getMessage().getChannel().block().createMessage("You have written " + totalWords + " words for an average of " + averageWords + " wpm.").block();
							writersEntered.add("words");
						}
						
					

					} else if (!skirmishes.get(index).isComplete()) {
						event.getMessage().getChannel().block().createMessage("Skirmish is incomplete!  Try again after it has finished.").block();						
					}


				} /*else if (battles.get(index) != null) {
            			if (battles.get(Integer.parseInt(commandContent[1])).isComplete())  {
            				length = battles.get(Integer.parseInt(commandContent[1])).getLength();
            				averageWords = totalWords / length;
            				writerIndex.get(event.getMember()).setProgress(totalWords);
            				event.getMessage().getChannel().block().createMessage("You have written " + totalWords + " " + writerIndex.get(event.getMember()).getGoalType()).block();
                		} else {}


            		} else if (wars.get(index) != null) {
            			if (wars.get(Integer.parseInt(commandContent[1])).isComplete())  {
            				length = wars.get(Integer.parseInt(commandContent[1])).getLength();
            				averageWords = totalWords / length;
            				writerIndex.get(event.getMember()).setProgress(totalWords);
            				event.getMessage().getChannel().block().createMessage("You have written " + totalWords + " " + writerIndex.get(event.getMember()).getGoalType()).block();
                		} else {}


            		} else if (sprints.get(index) != null) {
            			sprints.notify();


            		} */ else {
            			event.getMessage().getChannel().block().createMessage(commandContent[1] + " not found. Please try with a valid war ID#.").block();

            		}
				
				schedule.schedule(() -> {

					
					try {
						canSubmit = false;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						
						e.printStackTrace();
					}

				}, 5, TimeUnit.MINUTES);



			}
			catch (Exception e) {
				e.printStackTrace();
				event.getMessage().getChannel().block().createMessage("Invalid command.").block();
			}




		});

		commands.put("battle", event -> {
			
//			System.out.println("warIndex: " + warIndex);
//			
//			int timeHours = 0;
//			int timeMinutes = 0;
//			int start = 0;
//			
//			System.out.println("Battle created");
//
//			final String content = event.getMessage().getContent();
//			String[] commandContent = content.split(" ");
//
//			if (commandContent.length > 4) {
//				event.getMessage().getChannel().block().createMessage("Invalid number of arguments. Try again!").block();
//				return;
//			}
//
//			timeHours = Integer.parseInt(commandContent[1]);
//			timeMinutes = Integer.parseInt(commandContent[2]);
//			start = Integer.parseInt(commandContent[3]);
//			
//
//			//stop user from creating a battle with length 0 (because calculating the average for total creates a divide by zero scenario)
//			//not that anyone would do that, so really this is just to stop *me* from testing with length 0
//			if (timeMinutes == 0 && timeHours == 0) {
//				event.getMessage().getChannel().block().createMessage("Length of skirmish cannot be zero! Try again!");
//				return;
//			}
//
//			//Let's user know the length is too short
//			/*if (timeHours == 0 && timeMinutes < 60) {
//				event.getMessage().getChannel().block().createMessage("Length is too short! Try starting a word skirmish instead.").block();
//				return;
//			}*/
//
//			Battle battle = new Battle(warIndex, timeHours, timeMinutes, start);
//			battles.add(battle.getIndex(), battle);
//			
//			
//
//			battle.setComplete();
//			warIndex++; 

		});

		commands.put("sprint", event -> {

			System.out.println("warIndex: " + warIndex);

			int goal = 0;
			int start = 0;
			int time = 0;

			final String content = event.getMessage().getContent();
			String[] commandContent = content.split(" ");
			System.out.println("content: " + content);


			try {
				goal = Integer.parseInt(commandContent[1]);
				time = Integer.parseInt(commandContent[2]);
				start = Integer.parseInt(commandContent[3]);
				System.out.println("goal: " + goal + " | time: " + time + " | start: " + start);
			}
			catch (Exception e) {
				e.printStackTrace();
				event.getMessage().getChannel().block().createMessage("Invalid sprint command. Try again!");
			}

			Sprint sprint = new Sprint(warIndex);

			sprints.add(sprint.getIndex(), sprint);

			sprint.setGoal(goal);

			event.getMessage().getChannel().block().createMessage("Sprint #" + sprint.getIndex() + " created for " + time + " minutes, and will start in " + start + " minutes.").block();


			TimeUnit timeyWimey = TimeUnit.MINUTES;
			long longTime = TimeUnit.MINUTES.toMillis(time);
			long longStart = TimeUnit.MINUTES.toMillis(start);

			try {
				sprints.wait(longStart);
				event.getMessage().getChannel().block().createMessage("Sprint #" + sprint.getIndex() + " starts now! Get to " + goal + " as quickly as you can! Enter `!total` when finished!").block();

			} catch (InterruptedException e) {
				e.printStackTrace();
			}


			try {

				sprints.wait(longTime);
				event.getMessage().getChannel().block().createMessage("Sprint #" + sprint.getIndex() + " ends now!").block();
				sprint.setComplete();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			event.getMessage().getChannel().block().createMessage("Use `'!total " + sprint.getIndex() + " [amount written]'` to add your total.").block();  


			warIndex++;


		});


	}



}

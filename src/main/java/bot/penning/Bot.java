package bot.penning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bot.penning.listeners.SlashCommandListener;

public class Bot {

	// static HashMap writers = new HashMap();
	// static int warIndex = 0;
	// static ArrayList<Skirmish> skirmishes = new ArrayList<Skirmish>();
	// static ArrayList<Battle> battles = new ArrayList<Battle>();
	// static ArrayList<War> wars = new ArrayList<War>();
	// static ArrayList<Sprint> sprints = new ArrayList<Sprint>();

	static ArrayList<Object> writersEntered = new ArrayList<Object>();

	static final Map<Optional, Goal> writerIndex = new HashMap<>();

	static Boolean canSubmit = true;

	private static final Logger LOGGER = LoggerFactory.getLogger(Bot.class);

	public static void main(String[] args) {

		String token = System.getenv("DISCORD_TOKEN");

		final GatewayDiscordClient client = DiscordClientBuilder.create(token).build()
				.login()
				.block();

		
		//Call our code to handle creating/deleting/editing our global slash commands.
		List<String> commands = List.of("add.json", "battle.json", "clear.json", "greet.json", "goal.json", "hey.json",
				"info.json", "onslaught.json", "progress.json", "quest.json", "skirmish.json", "total.json", "war.json",
				"writing_prompt.json", "writers_block_prompt.json");
		try {
			new GlobalCommandRegistrar(client.getRestClient()).registerCommands(commands);
		} catch (Exception e) {
			LOGGER.error("Error trying to register global slash commands", e);
		}

		// Register our listeners
		client.on(ChatInputInteractionEvent.class, SlashCommandListener::handle)
				.then(client.onDisconnect())
				.block(); // We use .block() as there is not another non-daemon thread and the jvm would
							// close otherwise.
	}
}
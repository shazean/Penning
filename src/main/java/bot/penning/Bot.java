package bot.penning;

import bot.penning.data.EncounterDataReader;
import bot.penning.data.EncounterDataWriter;
import bot.penning.data.WriterDataReader;
import bot.penning.data.WriterDataWriter;
import bot.penning.listeners.MidnightListener;
import bot.penning.listeners.SlashCommandListener;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.slf4j.LoggerFactory;

public class Bot {

	public static final Logger LOGGER = Logger.getLogger("Penning"); //LoggerFactory.getLogger("Penning");

	public static void main(String[] args) {

		String token = System.getenv("DISCORD_TOKEN");

		final GatewayDiscordClient client = DiscordClientBuilder.create(token).build().login().block();

		//Call our code to handle creating/deleting/editing our global slash commands.
		List<String> commands = List.of("add.json", "battle.json", "challenge_quest.json", "clear.json", "goal.json", "info.json", "onslaught.json",
				"progress.json", "quest.json", "skirmish.json", "total.json",
				"timezone.json",
				"war.json", "writing_prompt.json", "writers_block_prompt.json");
		
		try {
			new GlobalCommandRegistrar(client.getRestClient()).registerCommands(commands);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error trying to register global slash commands", e);
//			LOGGER.error("Error trying to register global slash commands", e);
		}

		try {
			MidnightListener midnight = new MidnightListener(client);
			midnight.begin();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error trying to initialize MidnightListener", e);
		}
		
		try {
			new EncounterDataReader().run();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error with encounter data json reader", e);	
		}
		
		try {
			new WriterDataReader().run(client);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error with writer data json reader", e);	
		}
		
//		updateEncounterData();
//		updateWriterData();

		
		// Register our listeners
		client.on(ChatInputInteractionEvent.class, SlashCommandListener::handle).then(client.onDisconnect()).block();
			
	}
	
	public static void updateEncounterData() {
		try {
			new EncounterDataWriter().run();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error with encounter data json writer", e);
		}
		
	}
	
	public static void updateWriterData() {
		try {
			new WriterDataWriter().run();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error with writer data json writer", e);
		}
	
	}
}
package bot.penning.commmands;

import java.util.TimeZone;

import bot.penning.Bot;
import bot.penning.EncounterInfo;
import bot.penning.Writer;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.Member;
import reactor.core.publisher.Mono;

public class TimezoneCommand implements SlashCommand {
	@Override
	public String getName() {
		return "timezone";
	}

	@Override
	public Mono<Void> handle(ChatInputInteractionEvent event) {

		String timezone = event.getOption("timezone")
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asString)
				.orElse("America/Chicago");	        
		
		Member user = event.getInteraction().getMember().get();
		Writer writer = EncounterInfo.writerIndex.get(user);
		
		writer.setTimezone(TimeZone.getTimeZone(timezone));
		Bot.updateWriterData();

		return event.reply("Timezone set to " + timezone);
	}
}
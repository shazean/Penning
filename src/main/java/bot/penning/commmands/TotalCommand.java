package bot.penning.commmands;

import java.util.Optional;

import bot.penning.WarInfo;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.Member;
import reactor.core.publisher.Mono;

public class TotalCommand implements SlashCommand {
	@Override
	public String getName() {
		return "total";
	}

	@Override
	public Mono<Void> handle(ChatInputInteractionEvent event) {

		Long ID = event.getOption("id")
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asLong)
				.get(); //This is warning us that we didn't check if its present, we can ignore this on required options

		Long totalWritten = event.getOption("total")
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asLong)
				.get(); //This is warning us that we didn't check if its present, we can ignore this on required options

		String type = event.getOption("type")
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asString)
				.orElse("words");

		Optional<Member> user = event.getInteraction().getMember();

		//TODO
		//get skirmish/battle/etc information
		//calculate WPM

		//check if user has an established goal
		if (WarInfo.writerIndex.containsKey(user)) {
			//update goal
			//TODO
		}



		//TODO
		//tell user what their WPM was
		return event.reply("Total added!");
	}
}
package bot.penning.commmands;

import java.util.Optional;

import bot.penning.EncounterInfo;
import bot.penning.Goal;
import bot.penning.Writer;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.Member;
import reactor.core.publisher.Mono;

public class ClearCommand implements SlashCommand {

	@Override
	public String getName() {
		return "clear";
	}

	@Override
	public Mono<Void> handle(ChatInputInteractionEvent event) {

		Optional<Member> user = event.getInteraction().getMember();
		Writer writer = EncounterInfo.writerIndex.get(user);

		if (writer == null) {
			return event.reply("You don't have a goal to clear!  Create a goal first!").withEphemeral(true);
		}

		writer.clearGoal();

		return event.reply("Goal cleared!");
	}
}

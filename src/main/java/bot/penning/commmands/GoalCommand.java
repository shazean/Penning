package bot.penning.commmands;

import bot.penning.Goal;
import bot.penning.Writer;
import bot.penning.Bot;
import bot.penning.EncounterInfo;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.MessageChannel;
import reactor.core.publisher.Mono;

public class GoalCommand implements SlashCommand {

	@Override
	public String getName() {
		return "goal";
	}

	@Override
	public Mono<Void> handle(ChatInputInteractionEvent event) {
		Long target = event.getOption("target")
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asLong)
				.get();

		String type = event.getOption("type")
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asString)
				.orElse("words");	        

		Member user = event.getInteraction().getMember().get();
		Goal writerGoal;
		Writer writer = EncounterInfo.writerIndex.get(user);
		
		//FIXME or TODO?
		//perhaps add a confirmation message with a button about whether or not the user wants to overwrite their old goal
		//instead of forcing them to use the /clear command
		
		if (writer != null && writer.hasGoalSet()) {
			event.reply("Please clear your old goal before you create a new one! Use `/clear`");
		}
				
		if (type != null) { //type was specified FIXME?
			writerGoal = new Goal(target, type);
			if (writer == null) {
				writer = new Writer(user, writerGoal);
				EncounterInfo.writerIndex.put(user, writer);
				EncounterInfo.writerIndex.get(user).updateGoal(writerGoal);
			} else {
				writer.addGoal(writerGoal);
			}
		}
		else { //type was not specified, only target was
			writerGoal = new Goal(target);

			if (writer == null) {
				writer = new Writer(user, writerGoal);
				EncounterInfo.writerIndex.put(user, writer);
				EncounterInfo.writerIndex.get(user).updateGoal(writerGoal);
			} else {
				writer.addGoal(writerGoal);
			}
		}
		
		EncounterInfo.writerIndex.get(user).setPreferedChannel(event.getInteraction().getChannel().block());
		Bot.updateWriterData();

		return event.reply("Goal of " + writerGoal.getGoal() + " " + writerGoal.getGoalType() + " created!");
	}
}

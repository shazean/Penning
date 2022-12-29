package bot.penning.events;

import discord4j.core.event.domain.message.MessageCreateEvent;

public interface SkirmishEvent {
	void execute(MessageCreateEvent event);
}

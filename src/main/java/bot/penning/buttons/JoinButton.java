package bot.penning.buttons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import reactor.core.publisher.Mono;

public class JoinButton implements PenningButton {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	
	@Override
	public String getName() {
		return "join_button";
	}

	@Override
	public Mono<Void> handle(ButtonInteractionEvent event) {
		//FIXME
		
		return event.reply("You have joined the skirmish!");
	
	}

}

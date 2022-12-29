package bot.penning.buttons;

import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import reactor.core.publisher.Mono;

public interface PenningButton {

    String getName();

    Mono<Void> handle(ButtonInteractionEvent event);
}
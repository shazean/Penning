package bot.penning.commmands;

import java.util.Random;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

public class WritersBlockCommand implements SlashCommand {
	@Override
	public String getName() {
		return "writers_block_prompt";
	}

	@Override
	public Mono<Void> handle(ChatInputInteractionEvent event) {

		Random rand = new Random(); 

		String writingPrompt = getWritingPrompt(rand.nextInt(17));

		return event.reply(writingPrompt);
	}


	private String getWritingPrompt(int rand) {
		switch (rand) {
		case 0:
			return "Take a break from writing and go read.";

		case 1:
			return "Rearrange your desk/writing space.";
			
		case 2:
			return "Set your alarm to 3am (or an hour you’re not usually awake), and write for an hour then go back to bed.";

		case 3:
			return "Take a notebook and pen and get on a public bus or train.  Write what’s happening around you.";
			
		case 4:
			return "Write a story you know would make you mad as a reader.";

		case 5:
			return "Narrate into a voice recording app.";
			
		case 6:
			return "Find the oldest piece of writing you have and rewrite it.";

		case 7:
			return "Check on your self care. Get a drink of water.";
			
		case 8:
			return "Check on your self care.  Go take a walk or stretch and get the blood flowing.";

		case 9:
			return "Try changing the weather in the scene you’re writing.";

		case 10:
			return "Reread something you’ve written before that you enjoy.";

		case 11:
			return "Try changing up the sounds around you!  Try a different genre of music, or try some white noise, or total silence, or trying going somewhere busy with other people.";

		case 12:
			return "Try doing a puzzle or something else mentally-stimulating.";

		case 13:
			return "Set a deadline.  Have a friend hold you accountable.";

		case 14:
			return "Change your surroundings.  Go write in a place different from where you usually write.";

		case 15:
			return "Change up what medium you’re writing with!  Try a notebook and pens, or index cards, or get some window markers and try writing on the bathroom mirror!";

		case 16:
			return "Set up a reward system. Make sure you stick to the requirements!";

		default:
			return "Sample writer's block prompt #3";


		}
	}
}
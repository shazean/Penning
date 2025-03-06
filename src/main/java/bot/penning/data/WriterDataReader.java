package bot.penning.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.TimeZone;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import bot.penning.BotUtil;
import bot.penning.EncounterInfo;
import bot.penning.Goal;
import bot.penning.Writer;
import bot.penning.quests.ChallengeQuest;
import bot.penning.quests.Quest;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Member;

public class WriterDataReader {

	public void run(GatewayDiscordClient client) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(new File(BotUtil.dataFolder + "/writer_data.json"));

		//        ArrayNode jsonArray = jsonNode.get("Writer").withArray("Writer");


		JsonNode arrayNode = jsonNode.get("Writer");
		
		Iterator<JsonNode> iterator = arrayNode.elements();
		
		while (iterator.hasNext()) {
			JsonNode node = iterator.next();

			Member member = client.getMemberById(Snowflake.of(node.get("guildId").asLong()), Snowflake.of(node.get("userId").asLong())).block();
			Writer writer;

			if (node.get("hasGoal").asBoolean()) {
				Goal goal = new Goal(node.get("goalTotal").asLong(), node.get("goalType").asText());
				goal.setProgress(node.get("goalProgress").asLong());

				writer = new Writer(member, goal);
			} else {
				writer = new Writer(member);
			}

			writer.getAnimalData().fromJson(node.get("hedgehog").asInt(), node.get("dragon").asInt(), node.get("unicorn").asInt(), 
					node.get("axolotl").asInt(), node.get("turtle").asInt());

			if (node.get("hasQuest").asBoolean()) {
				Quest quest = new Quest(node.get("questTotal").asLong(), node.get("questType").asText());
				quest.getQuestGoal().setProgress(node.get("questProgress").asLong());
				writer.addQuest(quest);
			}

			if (node.get("hasChallengeQuest").asBoolean()) {
				ChallengeQuest challenge = new ChallengeQuest(node.get("challengeQuestTotal").asLong(), node.get("challengeQuestType").asText());
				challenge.getQuestGoal().setProgress(node.get("challengeQuestProgress").asLong());
				writer.addChallengeQuest(challenge);
			}

			String timezone = node.get("timezone").asText();
			writer.setTimezone(TimeZone.getTimeZone(timezone));

			EncounterInfo.writerIndex.put(member, writer);
		}

	}







	//	for (Member member: EncounterInfo.writerIndex.keySet()) {
	//		
	//		
	//		ObjectNode memberJson = objectMapper.createObjectNode();
	//
	//		Writer writer = EncounterInfo.writerIndex.get(member);
	//		
	//		memberJson.put("userId", member.getId().asLong());
	//		
	//		memberJson.put("hasGoal", writer.hasGoalSet());			
	//		//TODO, make array?
	//		if (writer.hasGoalSet()) {
	//			memberJson.put("goalTotal", writer.getGoal().getGoal());
	//			memberJson.put("goalProgress", writer.getGoal().getProgress());
	//			memberJson.put("goalType", writer.getGoal().getGoalType().toString());
	//		}
	//		
	//		writer.getAnimalData().toJson(memberJson);
	//
	//		memberJson.put("hasQuest", writer.hasQuest());
	//		if (writer.hasQuest()) {
	//			memberJson.put("questTotal", writer.getQuest().getQuestGoal().getGoal());
	//			memberJson.put("questProgress", writer.getQuest().getQuestGoal().getProgress());
	//			memberJson.put("questType", writer.getQuest().getQuestGoal().getGoalType().toString());
	//		}
	//		
	//		memberJson.put("hasChallengeQuest", writer.hasChallengeQuest());
	//		if (writer.hasQuest()) {
	//			memberJson.put("challengeQuestTotal", writer.getChallengeQuest().getQuestGoal().getGoal());
	//			memberJson.put("challengeQuestProgress", writer.getChallengeQuest().getQuestGoal().getProgress());
	//			memberJson.put("challengeQuestType", writer.getChallengeQuest().getQuestGoal().getGoalType().toString());
	//		}
	//		
	//		memberJson.put("timezone", writer.getTimeZone().getID());
	//
	////		String name;
	////		ArrayList<Double> averageWPM;
	////		int averageWPMIndex;
	////		int totalXP;
	////		int maxWPMsRecord = 20;
	////		Long bestGoal;
	////		Channel preferedChannel;
	//
	//		
	//		jsonArray.add(memberJson);
	//		
	//	}
	//	
	//	jsonNode.set("Writer", jsonArray);

}

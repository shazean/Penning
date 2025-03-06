package bot.penning.data;

import java.io.File;

import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import bot.penning.BotUtil;
import bot.penning.EncounterInfo;
import bot.penning.Writer;
import discord4j.common.JacksonResources;
import discord4j.core.object.entity.Member;

public class WriterDataWriter {
	
	public void run() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();

		ObjectNode jsonNode = objectMapper.createObjectNode();
		ArrayNode jsonArray = objectMapper.createArrayNode();
		
		for (Member member: EncounterInfo.writerIndex.keySet()) {
			
			ObjectNode memberJson = objectMapper.createObjectNode();

			Writer writer = EncounterInfo.writerIndex.get(member);
			
			memberJson.put("userId", member.getId().asLong());
			memberJson.put("guildId", member.getGuildId().asLong());
			
			memberJson.put("hasGoal", writer.hasGoalSet());			
			//TODO, make array?
			if (writer.hasGoalSet()) {
				memberJson.put("goalTotal", writer.getGoal().getGoal());
				memberJson.put("goalProgress", writer.getGoal().getProgress());
				memberJson.put("goalType", writer.getGoal().getGoalType().toString());
			}
			
			writer.getAnimalData().toJson(memberJson);

			memberJson.put("hasQuest", writer.hasQuest());
			if (writer.hasQuest()) {
				memberJson.put("questTotal", writer.getQuest().getQuestGoal().getGoal());
				memberJson.put("questProgress", writer.getQuest().getQuestGoal().getProgress());
				memberJson.put("questType", writer.getQuest().getQuestGoal().getGoalType().toString());
			}
			
			memberJson.put("hasChallengeQuest", writer.hasChallengeQuest());
			if (writer.hasQuest()) {
				memberJson.put("challengeQuestTotal", writer.getChallengeQuest().getQuestGoal().getGoal());
				memberJson.put("challengeQuestProgress", writer.getChallengeQuest().getQuestGoal().getProgress());
				memberJson.put("challengeQuestType", writer.getChallengeQuest().getQuestGoal().getGoalType().toString());
			}
			
			memberJson.put("timezone", writer.getTimeZone().getID());

//			String name;
//			ArrayList<Double> averageWPM;
//			int averageWPMIndex;
//			int totalXP;
//			int maxWPMsRecord = 20;
//			Long bestGoal;
//			Channel preferedChannel;

			
			jsonArray.add(memberJson);
			
		}
		
		jsonNode.set("Writer", jsonArray);
		
		
		objectMapper.writerWithDefaultPrettyPrinter();

		objectMapper.writeValue(new File(BotUtil.dataFolder + "/writer_data.json"), jsonNode);
		
		
	}
}
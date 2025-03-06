package bot.penning.data;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import bot.penning.BotUtil;
import bot.penning.EncounterInfo;

public class EncounterDataWriter {
	
	public void run() throws IOException {
		
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode jsonNode = objectMapper.createObjectNode();
		
		jsonNode.put("Encounter Index", EncounterInfo.getEncounterIndex());
		
		
		objectMapper.writeValue(new File(BotUtil.dataFolder + "/encounter_data.json"), jsonNode);
		
	}
}
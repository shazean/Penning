package bot.penning.data;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import bot.penning.BotUtil;
import bot.penning.EncounterInfo;

public class EncounterDataReader {
    
	public void run() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(new File(BotUtil.dataFolder + "/encounter_data.json"));
        
        Long index = jsonNode.get("Encounter Index").asLong();
        
        EncounterInfo.setEncounterIndex(index);
        
	}
	
}

package bot.penning;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class Midnight {

	//purpose: keep track of all timezones, and cycle through which one is "currently" at midnight
	//todo:
	//at initialization:
	//check what time it is now in "default" timezone
	//determine which timezone is at midnight
	//check if any writers are stored in that timezone and if they have rewards to be granted
	//grant rewards and reset/clear said writers' daily goals
	//wait ~60 minutes and repeat with next timezone
	
//	protected Map<Integer, ArrayList<Writer>> timezoneMap = new HashMap<>();
	protected ArrayList<ArrayList<Writer>> timezoneMap = new ArrayList<ArrayList<Writer>>();
	
	
	public Midnight() {
		TimeZone currentTimeZone = TimeZone.getTimeZone("America/Los_Angeles");
		Calendar rightNow = Calendar.getInstance(currentTimeZone); //get the current time in LA
		Calendar midnight;
		Date date = rightNow.getTime();
		int timeFromMidnight = date.getHours();
		//determine which timezone is at midnight currently
	}
	
	
	public void grantRewards() {
		//TODO
	}
	
	public void clearGoals() {
		//TODO
	}
	
}
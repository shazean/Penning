package bot.penning;

public enum WritingType {
	WORDS("words", "wpm"),
	LINES("lines", "lpm"),
	PAGES("pages", "pages/minute"),
	PARAGRAPHS("paragraphs", "paragraphs/minute"),
	MINUTES("minutes", "minutes"),
	CHAPTERS("chapters", "chapters/hour", true),
	PERIWINKLES("periwinkles", "periwinkles/minute"),
	KEYBOARD_SLAMS("asdfghjkl", "keyboard slams/minute"),
	SCREAMS("screams", "screams/minute"),
	MEASURES("measures", "measures/minute");
	
	final String type;
	final String abbreviation;
	final boolean calculateByHour;
	
	WritingType(String type, String abbreviation) {
		this.type = type;
		this.abbreviation = abbreviation;
		this.calculateByHour = false;
	}
	
	WritingType(String type, String abbreviation, boolean calculateByHour) {
		this.type = type;
		this.abbreviation = abbreviation;
		this.calculateByHour = calculateByHour;
	}
	
	public boolean getCalculateByHour() {
		return calculateByHour;
	}
	
	public String getAbbreviation() {
		return abbreviation;
	}
	
	@Override
	public String toString() {
		return getType();
	}

	public String getType() {
		return type;
	}

}

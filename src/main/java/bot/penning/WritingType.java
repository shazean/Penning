package bot.penning;

public enum WritingType {
	WORDS("words", "wpm"),
	LINES("lines", "lpm"),
	PAGES("pages", "pages/minute"),
	PARAGRAPHS("paragraphs", "paragraphs/minute"),
	MINUTES("minutes", "minutes"),
	CHAPTERS("chapters", "chapters/hour"),
	PERIWINKLES("periwinkles", "periwinkles/minute"),
	ASDFGHJKL("keyboard slams", "keyboard slams/minute"),
	SCREAMS("screams into the void", "screams/minute"),
	MEASURES("measures", "measures/minute");
	
	final String type;
	final String abbreviation;
		
	WritingType(String type, String abbreviation) {
		this.type = type;
		this.abbreviation = abbreviation;
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
	
	public static double getWordsPerMin(WritingType type, Long totalWritten, Long length) {
		double wordsPerMin;
		if (type.equals(CHAPTERS)) {
			wordsPerMin = Math.round((totalWritten / ((double) length / 60.0)) * 100.0) / 100.0;
		} else {
			wordsPerMin = Math.round((totalWritten / (double) length) * 100.0) / 100.0;
		}
	
		return wordsPerMin;
	}
	
	public static String getAverageText(WritingType type, double wordsPerMin, Long length) {
		if (type.equals(MINUTES)) {
			return " of " + length + " minutes (" + (wordsPerMin * 100.0) + "%).";
		} else {
			return " for an average of " + wordsPerMin + " " + type.getAbbreviation() + ".";
		}
	}
	
	public static String getSummaryString(WritingType type, Long words, double wordsPerMin) {
		if (type.equals(MINUTES)) {
			return words + " " + type.type + " (" + (wordsPerMin * 100.0) + "%)";
		} else {
			return words + " " + type.type + " (" + wordsPerMin + " " + type.abbreviation + ")";
		}

	}
}
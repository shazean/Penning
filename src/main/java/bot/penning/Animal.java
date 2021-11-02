package bot.penning;

public class Animal {

	public Animal(String animalType) {
		setType(animalType);
	}
	
	public Animal() {
		
	}

	String animalType;
	int animalLevel;

	public void setType(String animalType) {
		this.animalType = animalType;
	}

	public String getType() {
		return animalType;
	}

	public void setLevel(int animalLevel) {
		this.animalLevel = animalLevel;
	}

	public int getLevel() {
		return animalLevel;
	}
	
}

package bot.penning;

import java.util.Random;

public class AnimalData {
	
	private int numHedgehogs;
	private int numUnicorns;
	private int numDragons;
	
	
	public AnimalData() {
		numHedgehogs = 0;
		numUnicorns = 0;
		numDragons = 0;
	}
	
	public int getHedgehogCount() {
		return numHedgehogs;
	}

	public int getUnicornCount() {
		return numUnicorns;
	}
	
	public int getDragonCount() {
		return numDragons;
	}
	
	public void incrementHedgehogs() {
		numHedgehogs++;
	}
	
	public void incrementUnicorns() {
		numUnicorns++;
	}
	
	public void incrementDragons() {
		numDragons++;
	}
	
	public String generateRandomAnimal() {
		int rand = new Random().nextInt(100);
		
		if (rand < 40) {
			incrementHedgehogs();
			return "hedgehog";
		} else if (rand < 80) {
			incrementUnicorns();
			return "unicorn";
		} else {
			incrementDragons();
			return "dragon";
		}
			
	}
	
	public boolean hasAnyAnimals() {
		if (numHedgehogs > 0) return true;
		if (numUnicorns > 0) return true;
		if (numDragons > 0) return true;
		return false;
	}
	
	public String toString() {
		String numAnimals = "";
		if (numHedgehogs > 0) numAnimals += "hedgehogs: + " + numHedgehogs + " ";
		if (numUnicorns > 0) numAnimals += "unicorns: + " + numUnicorns + " ";
		if (numDragons > 0) numAnimals += "dragons: + " + numDragons + " ";

		return numAnimals;
	}
	
}
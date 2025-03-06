package bot.penning.collectibles;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.fasterxml.jackson.databind.node.ObjectNode;

import bot.penning.TaskType;

public class AnimalData {
	
	private Map<Animal, Integer> animals = new HashMap<>(); //animal, count
	private static final Animal HEDGEHOG = new Animal("hedgehog", 50, 30, 10, 20, 30);
	  private static final Animal UNICORN = new Animal("unicorn", 10, 10, 50, 30, 30);
	    private static final Animal DRAGON = new Animal("dragon", 10, 50, 30, 30, 10);
	  private static final Animal GRIFFIN = new Animal("griffin", 30, 50, 30, 20, 10);
  private static final Animal FRUIT_BAT = new Animal("fruit_bat", 30, 10, 50, 10, 30);
  	  private static final Animal AXOLOTL = new Animal("axolotl", 30, 30, 10, 20, 50);
	      private static final Animal FOX = new Animal("fox", 50, 30, 30, 10, 10);
	      
	private static final Animal TURTLE = new Animal("turtle", 5, 5, 5, 5, 5);

	public AnimalData() {
		animals.put(HEDGEHOG, 0);
		animals.put(UNICORN, 0);
		animals.put(DRAGON, 0);
		animals.put(GRIFFIN, 0);
		animals.put(FRUIT_BAT, 0);
		animals.put(FOX, 0);
		animals.put(AXOLOTL, 0);
		animals.put(TURTLE, 0);
	}
	
	public int getAnimalCount(String animalName) {
		for (Animal animal : animals.keySet()) {
			if (animal.equals(animalName))
				return animals.get(animal);
		}
		
		return 0;
	}
	
	public String generateRandomAnimal(TaskType type) {
		int totalWeight = 0;
		int currentWeight = 0;

		for (Animal animal : animals.keySet()) {
			totalWeight += animal.getChance(type).weight;
		}
		
		int rand = new Random().nextInt(totalWeight);

		for (Animal animal : animals.keySet()) {
			currentWeight += animal.getChance(type).weight;
			if (currentWeight > rand) {
				
				animals.replace(animal, animals.get(animal) + 1);
				return animal.animalType;
			}
		}
 				
		return "";
	}
	
	public String rewardTurtle() {
		animals.replace(TURTLE, animals.get(TURTLE) + 1);
		return TURTLE.animalType;
	}
	
	
	public boolean hasAnyAnimals() {
		for (Animal animal : animals.keySet()) {
			if (animals.get(animal) > 0) return true;
		}
		return false;
	}
	
	public String toString() {
		String numAnimals = "";
		for (Animal animal : animals.keySet()) {
			int num = animals.get(animal);
			if (num > 0) numAnimals += animal.animalType + ": " + num + " ";
		}

		return numAnimals;
	}
	
	public ObjectNode toJson(ObjectNode json) {
		for (Animal animal : animals.keySet()) {
			json.put(animal.animalType, animals.get(animal));
		}
		
		return json;
	}
	
	public void fromJson(Map<String, Integer> animals) {
		this.animals = new HashMap<>();
		this.animals.put(HEDGEHOG, animals.get("hedgehog"));
		this.animals.put(UNICORN, animals.get("unicorn"));
		this.animals.put(DRAGON, animals.get("dragon"));
		this.animals.put(AXOLOTL, animals.get("axolotl"));
		this.animals.put(TURTLE, animals.get("turtle"));
		this.animals.put(GRIFFIN, animals.get("griffin"));
		this.animals.put(FRUIT_BAT, animals.get("fruit_bat"));
		this.animals.put(FOX, animals.get("fox"));
	}
	
	@Deprecated
	public void fromJson(int hedgehogCount, int dragonCount, int unicornCount, int axolotlCount, int turtleCount) {
		animals = new HashMap<>();
		animals.put(HEDGEHOG, hedgehogCount);
		animals.put(UNICORN, unicornCount);
		animals.put(DRAGON, dragonCount);
		animals.put(AXOLOTL, axolotlCount);
		animals.put(TURTLE, turtleCount);
	}
	
}
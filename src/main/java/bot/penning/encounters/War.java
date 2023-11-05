package bot.penning.encounters;

import java.util.ArrayList;

public class War extends Encounter {

	Long quantity;
	Long remainingQuantity;
	Long interval;
	public ArrayList<Skirmish> skirmishes = new ArrayList<Skirmish>();

	
	public War(Long index, Long length, Long start) {
		this(index, length, start, 1L, start);
		// TODO Auto-generated constructor stub
	}

	public War(Long index, Long length, Long start, Long quantity, Long interval) {
		super(index, length, start);
		setQuantity(quantity);
		remainingQuantity = quantity;
		this.interval = interval;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}
	
	public Long getQuantity() {
		return quantity;
	}
	
	public void reduceRemainingQty() {
		remainingQuantity--;
	}
	
	public Long getRemainingQty() {
		return remainingQuantity;
	}

	public void setInterval(Long interval) {
		this.interval = interval;
	}
	
	public Long getInterval() {
		return interval;
	}
	
	public boolean isWar() {
		return true;
	}	
}

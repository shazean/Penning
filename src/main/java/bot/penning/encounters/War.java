package bot.penning.encounters;

public class War extends Encounter {

	Long quantity;
	Long remainingQuantity;
	
	public War(Long index, Long length, Long start) {
		this(index, length, start, 1L);
		// TODO Auto-generated constructor stub
	}

	public War(Long index, Long length, Long start, Long quantity) {
		super(index, length, start);
		setQuantity(quantity);
		remainingQuantity = quantity;
		// TODO Auto-generated constructor stub
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

}

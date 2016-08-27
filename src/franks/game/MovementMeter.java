/*
 * see license.txt 
 */
package franks.game;

/**
 * Meter for managing movement costs
 * 
 * @author Tony
 *
 */
public class MovementMeter {

	private int movementAmount;
	
	/**
	 * 
	 */
	public MovementMeter(int startingAmount) {
		this.movementAmount = startingAmount;
	}
	
	public void reset(int movementAount) {
		this.movementAmount = movementAount;
	}
	
	/**
	 * @return the movementAmount
	 */
	public int getMovementAmount() {
		return movementAmount;
	}
	
	public boolean hasEnough(int amount) {
		return movementAmount >= amount;
	}

	public int decrementMovement(int amountDelta) {
		movementAmount -= amountDelta;
		if(movementAmount<0) {
			throw new IllegalArgumentException();
		}
		return movementAmount;
	}
	
	public int increaseMovement(int amountDelta) {
		this.movementAmount += amountDelta;
		return this.movementAmount;
	}
}

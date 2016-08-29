/*
 * see license.txt 
 */
package franks.game;

/**
 * @author Tony
 *
 */
public class Turn {

	private int number;
	
	/**
	 * 
	 */
	public Turn(int number) {
		this.number = number;
	}
	
	/**
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}

	
	/**
	 * Ends the current turn
	 * @param game
	 */
	public void endTurn(Game game) {
		game.getEntities().endTurn();		
	}

}

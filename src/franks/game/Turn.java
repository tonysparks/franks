/*
 * see license.txt 
 */
package franks.game;

import franks.game.entity.Entity;

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
		for(Entity ent : game.getEntities()) {
			ent.endTurn();
		}
	}

}

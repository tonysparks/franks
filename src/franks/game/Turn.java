/*
 * see license.txt 
 */
package franks.game;

import franks.game.entity.Entity;
import franks.game.entity.Entity.Type;

/**
 * @author Tony
 *
 */
public class Turn {

	private MovementMeter movementMeter;
	private int number;
	
	/**
	 * 
	 */
	public Turn(int number, MovementMeter meter) {
		this.number = number;
		this.movementMeter = meter;
	}
	
	/**
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}
	
	/**
	 * @return the movementMeter
	 */
	public MovementMeter getMovementMeter() {
		return movementMeter;
	}
	
	public void endTurn(Game game) {
		Resources resources = game.getResources();
		
		// apply the food		
		for(Entity ent : game.getEntities()) {
			if(ent.getType() == Type.HUMAN) {
				resources.feed(ent);
			}
		}
		
		// finally calculate their status
		for(Entity ent : game.getEntities()) {
			ent.calculateStatus();
		}
		
		
	}

}

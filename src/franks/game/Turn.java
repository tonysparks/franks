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
		
		
		for(Entity ent : game.getEntities()) {
			ent.endTurn();
		}
		
	}

}

/*
 * see license.txt 
 */
package franks.game.meta;

import franks.game.Game;
import franks.game.entity.EntityList;

/**
 * @author Tony
 *
 */
public class Army {

	private EntityList mapEntitiyes; 
	
	/**
	 * 
	 */
	public Army(Game game) {
		this.mapEntitiyes = new EntityList(game);
	}

}

/*
 * see license.txt 
 */
package franks.game.entity;

import franks.game.Game;
import franks.gfx.Art;
import franks.math.Vector2f;

/**
 * @author Tony
 *
 */
public class Building extends Entity {

	/**
	 * @param game
	 * @param type
	 * @param pos
	 * @param width
	 * @param height
	 */
	public Building(Game game, int width, int height) {
		super(game, Type.BUILDING, Art.homeBuilding, new Vector2f(), width, height);
	}
}

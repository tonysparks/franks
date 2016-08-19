/*
 * see license.txt 
 */
package franks.game.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import franks.game.Game;
import franks.math.Vector2f;

/**
 * @author Tony
 *
 */
public class ResourceEntity extends TempEntity {

	private String resourceName;
	/**
	 * @param game
	 * @param type
	 * @param pos
	 * @param width
	 * @param height
	 */
	public ResourceEntity(Game game, Type type, String resourceName, TextureRegion image, int starting) {
		super(game, type, image, new Vector2f(), 32, 32);
		this.resourceName = resourceName;
		
		attribute(resourceName , starting);
	}

	@Override
	public void checkStatus() {
		if(attributeAsInt(this.resourceName) <= 0) {
			kill();
		}
	}
}

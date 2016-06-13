/*
 * see license.txt 
 */
package newera.game.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import newera.game.Game;
import newera.gfx.Camera;
import newera.gfx.Canvas;
import newera.math.Vector2f;

/**
 * @author Tony
 *
 */
public class ResourceEntity extends Entity {

	private String resourceName;
	private TextureRegion image;
	/**
	 * @param game
	 * @param type
	 * @param pos
	 * @param width
	 * @param height
	 */
	public ResourceEntity(Game game, Type type, String resourceName, TextureRegion image, int starting) {
		super(game, type, new Vector2f(), 32, 32);
		this.resourceName = resourceName;
		this.image = image;
		
		attribute(resourceName , starting);
	}

	@Override
	public void checkStatus() {
		if(attributeAsInt(this.resourceName) <= 0) {
			kill();
		}
	}
	
	@Override
	protected void doRender(float dx, float dy, Canvas canvas, Camera camera, float alpha) {
		if(isSelected()) {
			canvas.fillCircle(16, dx, dy, 0xcfffffff);
		}
		canvas.drawImage(this.image, dx, dy, null);
	}
}

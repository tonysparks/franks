/*
 * see license.txt 
 */
package franks.game.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import franks.game.Game;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.math.Vector2f;

/**
 * @author Tony
 *
 */
public class TempEntity extends Entity {

	private TextureRegion image;
	
	/**
	 * @param game
	 * @param type
	 * @param pos
	 * @param width
	 * @param height
	 */
	public TempEntity(Game game, Type type, TextureRegion image, Vector2f pos, int width, int height) {
		super(game, type, pos, width, height);
		this.image = image;
	}

	protected void doRender(float dx, float dy, Canvas canvas, Camera camera, float alpha) {
		if(isSelected()) {
			canvas.fillCircle(getDiameter()/2.8f, dx, dy, 0xcfffffff);
		}
		canvas.drawImage(this.image, dx, dy, null);
	}
}

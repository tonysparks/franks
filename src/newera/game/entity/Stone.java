/*
 * see license.txt 
 */
package newera.game.entity;

import newera.game.Game;
import newera.gfx.Art;
import newera.gfx.Camera;
import newera.gfx.Canvas;
import newera.math.Vector2f;

/**
 * @author Tony
 *
 */
public class Stone extends Entity {

	/**
	 * 
	 */
	public Stone(Game game) {
		this(game, new Vector2f());
	}
	
	/**
	 * @param game
	 * @param type
	 * @param pos
	 * @param width
	 * @param height
	 */
	public Stone(Game game, Vector2f pos) {
		super(game, Type.STONE, pos, 32, 32);
		attribute("Stone" , 40 + game.getRandomizer().nextInt(50));
	}
	
	@Override
	public void checkStatus() {
		if(attributeAsInt("Stone") <= 0) {
			kill();
		}
	}

	@Override
	protected void doRender(float dx, float dy, Canvas canvas, Camera camera, float alpha) {
		if(isSelected()) {
			canvas.fillCircle(16, dx, dy, 0xcfffffff);
		}
		canvas.drawImage(Art.stone, dx, dy, null);
	}
}

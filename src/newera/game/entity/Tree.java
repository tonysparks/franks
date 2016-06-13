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
public class Tree extends Entity {

	
	public Tree(Game game) {
		this(game, new Vector2f());
	}
	
	/**
	 * @param game
	 * @param pos
	 */
	public Tree(Game game,Vector2f pos) {
		super(game, Type.TREE, pos, 32, 32);
		attribute("Wood" , 100 + game.getRandomizer().nextInt(50));
	}

	@Override
	public void checkStatus() {
		if(attributeAsInt("Wood") <= 0) {
			kill();
		}
	}

	/* (non-Javadoc)
	 * @see newera.game.Entity#doRender(float, float, newera.gfx.Canvas, newera.gfx.Camera, float)
	 */
	@Override
	protected void doRender(float dx, float dy, Canvas canvas, Camera camera, float alpha) {
		if(isSelected()) {
			canvas.fillCircle(16, dx, dy, 0xcfffffff);
		}
		canvas.drawImage(Art.tree, dx, dy, null);
	}
}

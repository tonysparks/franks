/*
 * see license.txt 
 */
package franks.game;

import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.gfx.Renderable;
import franks.math.Rectangle;
import franks.math.Vector2f;
import franks.util.TimeStep;

/**
 * @author Tony
 *
 */
public class Region implements Renderable {

	private Rectangle bounds;
	private Vector2f pos;
	/**
	 * 
	 */
	public Region(int x, int y, int width, int height) {
		this.bounds = new Rectangle(x, y, width, height);
		this.pos = new Vector2f(x*width,y*height);
	}
	
	/**
	 * @return the pos
	 */
	public Vector2f getPos() {
		return pos;
	}
	
	/**
	 * @return the bounds
	 */
	public Rectangle getBounds() {
		return bounds;
	}
	
	public int getWidth() {
		return bounds.width;
	}
	
	public int getHeight() {
		return this.bounds.height;
	}
	
	/**
	 * @return the x
	 */
	public int getX() {
		return bounds.x;
	}
	
	/**
	 * @return the y
	 */
	public int getY() {
		return bounds.y;
	}
	
	/* (non-Javadoc)
	 * @see newera.gfx.Renderable#update(newera.util.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
	}
	
	/* (non-Javadoc)
	 * @see newera.gfx.Renderable#render(newera.gfx.Canvas, newera.gfx.Camera, float)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {
		Vector2f cameraPos = camera.getRenderPosition(alpha);
		float dx = pos.x - cameraPos.x;
		float dy = pos.y - cameraPos.y;
		
		canvas.fillRect(dx, dy, getWidth(), getHeight(), 0xff81aa81);
		canvas.drawRect(dx, dy, getWidth(), getHeight(), 0xff000000);
	}

}

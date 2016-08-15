/*
 * see license.txt 
 */
package franks.map;

import franks.gfx.Camera;
import franks.gfx.Canvas;

/**
 * @author Tony
 *
 */
public class ColoredTile extends AbstractTile {

	private int borderColor;
	private int color;
	
	/**
	 * @param layer
	 * @param width
	 * @param height
	 */
	public ColoredTile(int color, int borderColor, int layer, int width, int height) {
		super(layer, width, height);
		this.color = color;
		this.borderColor = borderColor;
	}

	/**
	 * @param borderColor the borderColor to set
	 */
	public void setBorderColor(int borderColor) {
		this.borderColor = borderColor;
	}
	
	/**
	 * @return the borderColor
	 */
	public int getBorderColor() {
		return borderColor;
	}
	
	/**
	 * @param color the color to set
	 */
	public void setColor(int color) {
		this.color = color;
	}
	
	/**
	 * @return the color
	 */
	public int getColor() {
		return color;
	}

	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {
		if(!this.isDestroyed) {	    	
	    	canvas.fillRect(renderX, renderY, width, height, color);
	    	canvas.drawRect(renderX, renderY, width, height, borderColor);	    	
		}
	}

}

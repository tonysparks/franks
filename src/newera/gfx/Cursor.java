/*
 * see license.txt 
 */
package newera.gfx;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import newera.math.Vector2f;

/**
 * Represents the mouse pointer during menu screens, but more importantly acts as the players cursor/reticle in game. 
 * 
 * @author Tony
 *
 */
public class Cursor {

	private Vector2f cursorPos;
	private TextureRegion cursorImg;
	private boolean isVisible;
	
	private float mouseSensitivity;
	
	private int prevX, prevY;
	
	/**
	 */
	public Cursor() {
		this(Art.cursorImg);
	}
	
	/**
	 * @param image 
	 * 			the cursor image to use
	 */
	public Cursor(TextureRegion image) {
		this.cursorImg= image;
		this.cursorPos = new Vector2f();
		this.isVisible = true;
		this.mouseSensitivity = 1.0f;
	}

	/**
	 * @param mouseSensitivity the mouseSensitivity to set
	 */
	public void setMouseSensitivity(float mouseSensitivity) {
		this.mouseSensitivity = mouseSensitivity;	
	}
	
	/**
	 * Centers the mouse
	 */
	public void centerMouse() {
		int x = Gdx.graphics.getWidth()/2;
		int y = Gdx.graphics.getHeight()/2;	
		moveNativeMouse(x, y);
		moveTo(x,y);
	}
	
	/**
	 * Moves the native mouse
	 * @param x
	 * @param y
	 */
	private void moveNativeMouse(int x, int y) {
		Gdx.input.setCursorPosition(x,y);
	}
	
	
	/**
	 * Clamp the cursor position so that it doesn't
	 * move outside of the screen
	 */
	private void clamp() {
		if(this.cursorPos.x < 0)  {
			this.cursorPos.x = 0f;
		}
		
		if(this.cursorPos.y < 0)  {
			this.cursorPos.y = 0f;
		}
		
		if(this.cursorPos.x > Gdx.graphics.getWidth()) {
			this.cursorPos.x = Gdx.graphics.getWidth();
		}
		
		if(this.cursorPos.y > Gdx.graphics.getHeight()) {
			this.cursorPos.y = Gdx.graphics.getHeight();
		}
	}
	
	/**
	 * @return the mouseSensitivity
	 */
	public float getMouseSensitivity() {
		return mouseSensitivity;
	}
	
	/**
	 * @return the isVisible
	 */
	public boolean isVisible() {
		return isVisible;
	}
	
	/**
	 * @param isVisible the isVisible to set
	 */
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	/**
	 * Moves the cursor to the specified location
	 * 
	 * @param x
	 * @param y
	 */
	public void moveTo(int x, int y) {
		if(isVisible()) {			
			float deltaX = this.mouseSensitivity * (this.prevX - x);
			float deltaY = this.mouseSensitivity * (this.prevY - y);
						
			this.cursorPos.x -= deltaX;			
			this.cursorPos.y -= deltaY;
				
			this.prevX = x;
			this.prevY = y;			
			
			clamp();
		}
	}
	
	/**
	 * Moves the cursor based on the delta movement
	 * @param dx either 1, -1 or 0
	 * @param dy either 1, -1 or 0
	 */
	public void moveByDelta(float dx, float dy) {
		float deltaX = this.mouseSensitivity * (dx*20);
		float deltaY = this.mouseSensitivity * (dy*20);
		
		this.prevX = (int)cursorPos.x;
		this.prevY = (int)cursorPos.y;
		
				
		this.cursorPos.x += deltaX;
		this.cursorPos.y += deltaY;		
		
		clamp();
	}
	
	/**
	 * @return the x position
	 */
	public int getX() {
		return (int)this.cursorPos.x;
	}
	
	/**
	 * @return the y position
	 */
	public int getY() {
		return (int)this.cursorPos.y;
	}
	
	/**
	 * @return the cursorPos
	 */
	public Vector2f getCursorPos() {
		return cursorPos;
	}
	
	/**
	 * Draws the cursor on the screen
	 * @param canvas
	 */
	public void render(Canvas canvas) {
		if(isVisible()) {
			int imageWidth = cursorImg.getRegionWidth();
			int imageHeight = cursorImg.getRegionHeight();
			canvas.drawImage(cursorImg, (int)cursorPos.x - imageWidth/2, (int)cursorPos.y - imageHeight/2, null);
		}
	}
}

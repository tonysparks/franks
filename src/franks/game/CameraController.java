/*
 * see license.txt 
 */
package franks.game;

import franks.gfx.Camera;
import franks.map.Map;
import franks.math.Rectangle;
import franks.math.Vector2f;
import franks.sfx.Sounds;
import franks.util.TimeStep;
import franks.util.Updatable;

/**
 * @author Tony
 *
 */
public class CameraController implements Updatable {

	private Map map;
	private Camera camera;
	private Vector2f cameraCenterAround;
	private Vector2f cameraDest;
	private Vector2f playerVelocity;
	private Rectangle bounds;
	
	private int viewportWidth, viewportHeight;
	/**
	 * 
	 */
	public CameraController(Map map, Camera camera) {
		this.camera = camera;
		this.map = map;
		
		this.bounds = new Rectangle();
		this.cameraCenterAround = new Vector2f();
		this.cameraDest = new Vector2f();
		this.playerVelocity = new Vector2f();
		
		this.viewportWidth = this.camera.getViewPort().width;
		this.viewportHeight = this.camera.getViewPort().height;
		
		resetToCameraPos();
	}
	
	public void resetToCameraPos() {
		// center the camera position
		cameraDest.set(camera.getPosition());
		cameraDest.x += camera.getViewPort().width/2;
		cameraDest.y += camera.getViewPort().height/2;
	}
	
	/**
	 * Applies the mouse/joystick inputs
	 * 
	 * @param mx
	 * @param my
	 */
	public void applyPlayerMouseInput(float mx, float my) {
		this.playerVelocity.zeroOut();
		
		final float threshold = 25.0f;
		if(mx < threshold) {
			this.playerVelocity.x = -1;
		}
		else if(mx > this.viewportWidth-threshold) {
			this.playerVelocity.x = 1;
		}
		
		if(my < threshold) {
			this.playerVelocity.y = -1;
		}
		else if(my > this.viewportHeight-threshold) {
			this.playerVelocity.y = 1;
		}
	}
	
	/**
	 * Updates the camera roaming position
	 * 
	 * @param timeStep
	 */
	private void updateCameraForRoamingMovements(TimeStep timeStep) {
		final int movementSpeed = 780;
		if(playerVelocity.lengthSquared() > 0) {			
			Vector2f pos = cameraDest;		
			
			bounds.set(camera.getViewPort());					
			bounds.setLocation(pos);
						
			double dt = timeStep.asFraction();
			int newX = (int)Math.round(pos.x + playerVelocity.x * movementSpeed * dt);
			int newY = (int)Math.round(pos.y + playerVelocity.y * movementSpeed * dt);					
			
			
			bounds.x = newX;
			if( map.checkBounds(bounds.x, bounds.y) || 
				((bounds.x < bounds.width/2) || (bounds.y < bounds.height/2)) ||
				map.checkBounds(bounds.x + bounds.width/2, bounds.y + bounds.height/2) ) {
				bounds.x = (int)pos.x;
			}
					
			
			bounds.y = newY;
			if( map.checkBounds(bounds.x, bounds.y) ||
				((bounds.x < bounds.width/2) || (bounds.y < bounds.height/2)) ||
				map.checkBounds(bounds.x + bounds.width/2, bounds.y + bounds.height/2) ) {
				bounds.y = (int)pos.y;
			}
			
						
			pos.x = bounds.x;
			pos.y = bounds.y;
		
			cameraCenterAround.set(pos);
			
			Vector2f.Vector2fRound(cameraCenterAround, cameraCenterAround);;
			camera.centerAround(cameraCenterAround);
			Sounds.setPosition(cameraCenterAround);
		}
	}
	
	
	/* (non-Javadoc)
	 * @see newera.util.Updatable#update(newera.util.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {		
		this.updateCameraForRoamingMovements(timeStep);
	}

}

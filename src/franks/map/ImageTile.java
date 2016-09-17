/*
 *	leola-live 
 *  see license.txt
 */
package franks.map;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.gfx.TextureUtil;

/**
 * A Tile represents the smallest element in a game map.
 * 
 * @author Tony
 *
 */
public class ImageTile extends AbstractTile {

	private Sprite sprite;

	/**
	 * 
	 */
	public ImageTile(TextureRegion image, int layer, int width, int height) {
		super(layer, width, height);
		
		if(image!=null) {
			this.sprite = new Sprite(image);
		}
	}
	
	
	/* (non-Javadoc)
	 * @see newera.map.MapTile#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getX();
		result = prime * result + getY();
		return result;
	}

	/* (non-Javadoc)
	 * @see newera.map.MapTile#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImageTile other = (ImageTile) obj;
		if (getX() != other.getX())
			return false;
		if (getY() != other.getY())
			return false;
		return true;
	}


	    
                
    /* (non-Javadoc)
	 * @see newera.map.MapTile#setFlips(boolean, boolean, boolean)
	 */
    @Override
	public void setFlips(boolean isFlippedHorizontal, boolean isFlippedVert, boolean isFlippedDiagnally) {
    	if(isFlippedDiagnally) this.flipMask |= MapTile.isFlippedDiagnally;
    	if(isFlippedHorizontal)	this.flipMask |= MapTile.isFlippedHorizontal;
		if(isFlippedVert) this.flipMask |= MapTile.isFlippedVert;
		if(this.sprite==null) return;
    	
    	TextureUtil.setFlips(this.sprite, isFlippedHorizontal, isFlippedVert, isFlippedDiagnally);    	
    }
    
	
	/* (non-Javadoc)
	 * @see newera.map.MapTile#getImage()
	 */
	public Sprite getImage() {
		return this.sprite;
	}
	
	
	/* (non-Javadoc)
	 * @see newera.map.MapTile#render(newera.gfx.Canvas, newera.gfx.Camera, float)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {		
	    if(!this.isDestroyed) {
	    	this.sprite.setPosition(renderX, renderY);
	    	switch(getVisibility()) {
	    		case BLACKED_OUT: 
	    			this.sprite.setColor(0.0f, 0.0f, 0.0f, 1f);
	    			break;
	    		case VISITED:
	    			this.sprite.setColor(0.65f, 0.65f, 0.65f, 1f);
	    			break;
    			default: 
    				this.sprite.setColor(1.0f, 1.0f, 1.0f, 1f);
		    			
	    	}
	    	
	    	canvas.drawRawSprite(sprite);
//	    	if(cell!=null) {
//	    		Rectangle b = cell.getTileBounds();
//	    		canvas.drawString(b.x +"," + b.y, renderX+16, renderY+16, 0xffffffff);
//	    	}
	       // canvas.drawScaledImage(image, renderX, renderY, width, height, 0xFFFFFFFF);

//	    	Vector2f pos = camera.getRenderPosition(alpha);
//	    	float x = (this.x - pos.x);
//	    	float y = (this.y - pos.y);
//	    	canvas.drawScaledImage(image, x, y, width, height, 0xFFFFFFFF);
	    	
	    	
//	    	Vector2f pos = camera.getRenderPosition(alpha);
//	    	float x = (int)(this.x - pos.x);
//	    	float y = (int)(this.y - pos.y);
//	    	sprite.setPosition(x, y);
//	    	canvas.drawRawSprite(sprite);	
	    }
	}

}

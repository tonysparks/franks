/*
 * see license.txt 
 */
package franks.map;

import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.math.Vector2f;

/**
 * @author Tony
 *
 */
public class IsometricMap extends OrthoMap {

	private int tileWidth, baseTileHeight;
	private int halfTileWidth, halfTileHeight; // half of the iso tile size
	// the tile incremental position
	// is using this value
	private int offsetTileHeight; // tile height offset from base tile height
	private int startX, startY; // starting top x, y coordinate
	private int offsetX, offsetY;        
	
	private int tileX;
	
	private int horiz, vert; // total horizontal, vertical tiles

	private int width, height;
	
	private Vector2f posA = new Vector2f();
	private Vector2f posB = new Vector2f();
	
	/**
	 * @param loadAssets
	 */
	public IsometricMap(boolean loadAssets) {
		super(loadAssets);
	}

	/* (non-Javadoc)
	 * @see newera.map.OrthoMap#getTileWidth()
	 */
	@Override
	public int getTileWidth() {	
		return super.getTileWidth()/2;
	}
	
	/* (non-Javadoc)
	 * @see newera.map.OrthoMap#init(newera.map.Map.SceneDef)
	 */
	@Override
	public void init(SceneDef info) {	
		super.init(info);
				
		int offsetTileHeight = 0;
		this.tileWidth = getTileWidth() * 2;
		this.baseTileHeight = getTileHeight() - offsetTileHeight;
		this.offsetTileHeight = offsetTileHeight;
		
		this.halfTileWidth = getTileWidth();// / 2;
		this.halfTileHeight = this.baseTileHeight / 2;
		
		this.horiz = this.maxX;
		this.vert = this.maxY;
		
		
		this.offsetX = 300;
		this.offsetY = 100;
		
		this.startX = ((vert - 1) * this.halfTileWidth) + this.offsetX;
		this.startY = this.offsetY;
		
		width = (horiz+vert) * (tileWidth / 2);
		height = ((horiz+vert) * ((getTileHeight() - offsetTileHeight) / 2)) + startY;
		
		mapWidth = width + startX;
		mapHeight = height + startY + 200;//100;
		
		initRenderPositions();
	}
	
	
	/**
	 * This actually takes in screen coordinates relative to the current camera position.
	 * This will convert those coordinates to the appropriate {@link MapTile} if there
	 * exists one.
	 */
	@Override
	public MapTile getWorldTile(int layerIndex, float worldX, float worldY) {				
		worldX -= this.startX+this.halfTileWidth; //this.vert * this.halfTileWidth;
		worldY -= this.startY;
		
		int x = (int)((worldY / this.baseTileHeight) + (worldX / this.tileWidth));
		int y = (int)((worldY / this.baseTileHeight) - (worldX / this.tileWidth));
		
		if(x<0 || x > this.horiz-1 || y < 0 || y > this.vert -1) {
			return null;
		}
		
		return getTile(layerIndex, x, y);
	}
	
	/**
	 * @return the startX
	 */
	public int getStartX() {
		return startX;
	}
	
	/**
	 * @return the startY
	 */
	public int getStartY() {
		return startY;
	}
	
	/**
	 * @return the offsetX
	 */
	public int getOffsetX() {
		return offsetX;
	}
	/**
	 * @return the offsetY
	 */
	public int getOffsetY() {
		return offsetY;
	}
		
	/**
	 * Converts the supplied isometric tile coordinates to screen coordinates
	 * 
	 * @param isoX
	 * @param isoY
	 * @param out
	 * @return screen coordinates
	 */
	public Vector2f isoIndexToScreen(float isoX, float isoY, Vector2f out) {
		float startX = this.startX;//(this.vert - 1) * this.halfTileWidth;
		float startY = this.startY;//(this.vert-1) * this.halfTileHeight;
		out.x = startX + ((isoX - isoY) * this.halfTileWidth) + this.halfTileHeight;// - 192;
		out.y = startY + ((isoX * this.halfTileHeight) + (isoY * this.halfTileHeight));// + 96;
		
		return out;
	}

	
	/**
	 * Renders an isometric rectangle
	 * 
	 * @param canvas
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 */
	public void renderIsoRect(Canvas canvas, float x, float y, float width, float height, Integer color) {
				
//		canvas.drawString(x + "," + y, x, y, color);
//		canvas.drawCircle(2, x, y, color);
		
		float hw = width;
		float hh = height/2f;
		
		posA.set(x,y+hh);//this.halfTileHeight);		
		posB.set(x+hw/*this.halfTileWidth*/, y);		
		canvas.drawLine(posA.x, posA.y, posB.x, posB.y, color);
		
		
		posA.set(x+hw/*this.halfTileWidth*/,y);		
		posB.set(x+width*2f/*this.tileWidth*/, y+hh/*this.halfTileHeight*/);		
		canvas.drawLine(posA.x, posA.y, posB.x, posB.y, color);
		
		
		posA.set(x+width*2f/*this.tileWidth*/, y+hh/*this.halfTileHeight*/);		
		posB.set(x+hw/*this.halfTileWidth*/, y+height/*this.getTileHeight()*/);		
		canvas.drawLine(posA.x, posA.y, posB.x, posB.y, color);
		
		posA.set(x+hw/*this.halfTileWidth*/, y+height/*this.getTileHeight()*/);				
		posB.set(x, y+hh/*this.halfTileHeight*/);		
		canvas.drawLine(posA.x, posA.y, posB.x, posB.y, color);
		
//		a.set(x,y+hh);//this.halfTileHeight);		
//		b.set(x+hw/*this.halfTileWidth*/, y);		
//		canvas.drawLine(a.x, a.y, b.x, b.y, color);
//		
//		
//		a.set(x+hw/*this.halfTileWidth*/,y);		
//		b.set(x+width*2f/*this.tileWidth*/, y+hh/*this.halfTileHeight*/);		
//		canvas.drawLine(a.x, a.y, b.x, b.y, color);
//		
//		
//		a.set(x+width*2f/*this.tileWidth*/, y+hh/*this.halfTileHeight*/);		
//		b.set(x+hw/*this.halfTileWidth*/, y+height/*this.getTileHeight()*/);		
//		canvas.drawLine(a.x, a.y, b.x, b.y, color);
//		
//		a.set(x+hw/*this.halfTileWidth*/, y+height/*this.getTileHeight()*/);				
//		b.set(x, y+hh/*this.halfTileHeight*/);		
//		canvas.drawLine(a.x, a.y, b.x, b.y, color);
	}

	
	private void initRenderPositions() {
		int xbg = 0;
		int ybg = 0;
		int x = 0;
		int y = 0;
		
		int w = getMapWidth() + startX;
		int h = getMapHeight() + startY;
		
		int x0 = x - xbg + this.startX, // start x, y
		    y0 = y - ybg + this.startY - this.offsetTileHeight;
		int x1 = 0, // x, y coordinate counter
		    y1 = 0;
		int x2 = x + w, // right boundary
			y2 = y + h; // bottom boundary
		// - offsetY;
		
		int xTile = -1;
		int yTile = -1;
		int tileXTemp = this.tileX; // temporary to hold tileX var
		// since we need to modified its value
		
		int skip = 0;
		while (true) {
			y1 = y0;
			yTile++;
			
			x1 = x0;
			xTile = --tileXTemp;
			// can't be lower than tileX = 0
			if (xTile < -1) {
				xTile = -1;
			}
			
			// adjust x, y for the next tile based on tile x
			x1 += (xTile + 1) * this.halfTileWidth;
			y1 += (xTile + 1) * this.halfTileHeight;
			
			if (x1 + this.tileWidth <= x) {
				// the drawing is out of view area (too left)
				// adjust the position
				
				// calculate how many tiles must be skipped
				skip = ((x - (x1 + this.tileWidth)) / this.halfTileWidth) + 1;
				
				xTile += skip;
				x1 += skip * this.halfTileWidth;
				y1 += skip * this.halfTileHeight;
			}
			
			// if (x1 >= x2 || y1 >= y2 || xTile >= horiz-1) ++count;
			while (true) {
				if (x1 >= x2 || y1 >= y2 || xTile >= this.horiz - 1) {
					break;
				}
				
				xTile++;
				if (x1 + this.tileWidth > x) {
					//this.renderTile(g, xTile, yTile, x1, y1);
					for(int layerIndex = 0; layerIndex < this.backgroundLayers.length; layerIndex++) {
						MapTile tile = this.backgroundLayers[layerIndex].getRow(yTile)[xTile];
						if(tile!=null) {
							tile.setIsoPosition(x1,y1);							
						}
					}					
				}
				
				// increment x, y for the next tile
				x1 += this.halfTileWidth;
				y1 += this.halfTileHeight;
			}
			
			if (yTile >= this.vert - 1) {
				break;
			}
			
			// adjust start x, y for the next tile
			x0 -= this.halfTileWidth;
			y0 += this.halfTileHeight;
		}
	}
	
	
	/* (non-Javadoc)
	 * @see newera.map.OrthoMap#render(newera.gfx.Canvas, newera.gfx.Camera, float)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {
		int xbg = (int)camera.getRenderPosition(alpha).x;
		int ybg = (int)camera.getRenderPosition(alpha).y;
		int x = 0;
		int y = 0;
		
		int w = camera.getViewPort().width;
		int h = camera.getViewPort().height;
		
		int x0 = x - xbg + this.startX, // start x, y
		    y0 = y - ybg + this.startY - this.offsetTileHeight;
		int x1 = 0, // x, y coordinate counter
		    y1 = 0;
		int x2 = x + w, // right boundary
			y2 = y + h; // bottom boundary
		// - offsetY;
		
		int xTile = -1;
		int yTile = -1;
		int tileXTemp = this.tileX; // temporary to hold tileX var
		// since we need to modified its value
		
		int skip = 0;
		while (true) {
			y1 = y0;
			yTile++;
			
			x1 = x0;
			xTile = --tileXTemp;
			// can't be lower than tileX = 0
			if (xTile < -1) {
				xTile = -1;
			}
			
			// adjust x, y for the next tile based on tile x
			x1 += (xTile + 1) * this.halfTileWidth;
			y1 += (xTile + 1) * this.halfTileHeight;
			
			if (x1 + this.tileWidth <= x) {
				// the drawing is out of view area (too left)
				// adjust the position
				
				// calculate how many tiles must be skipped
				skip = ((x - (x1 + this.tileWidth)) / this.halfTileWidth) + 1;
				
				xTile += skip;
				x1 += skip * this.halfTileWidth;
				y1 += skip * this.halfTileHeight;
			}
			
			// if (x1 >= x2 || y1 >= y2 || xTile >= horiz-1) ++count;
			while (true) {
				if (x1 >= x2 || y1 >= y2 || xTile >= this.horiz - 1) {
					break;
				}
				
				xTile++;
				if (x1 + this.tileWidth > x) {
					//this.renderTile(g, xTile, yTile, x1, y1);
					for(int layerIndex = 0; layerIndex < this.backgroundLayers.length; layerIndex++) {
						MapTile tile = this.backgroundLayers[layerIndex].getRow(yTile)[xTile];
						if(tile!=null) {
							tile.setRenderingPosition(x1,y1);
							tile.render(canvas, camera, alpha);							
						}
					}					
				}
				
				// increment x, y for the next tile
				x1 += this.halfTileWidth;
				y1 += this.halfTileHeight;
			}
			
			if (yTile >= this.vert - 1) {
				break;
			}
			
			// adjust start x, y for the next tile
			x0 -= this.halfTileWidth;
			y0 += this.halfTileHeight;
		}
	}
}

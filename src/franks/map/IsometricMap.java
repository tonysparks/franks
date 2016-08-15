/*
 * see license.txt 
 */
package franks.map;

import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.math.Rectangle;
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
	        
	private int tileX, tileY;
	
	private int horiz, vert; // total horizontal, vertical tiles

	private int width, height;
	
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
		
//		this.offsetTileHeight = 0;
//		this.halfTileHeight = 16;
//		this.halfTileWidth = 32;
		
		int offsetTileHeight = 0;
		this.tileWidth = getTileWidth() * 2;
		this.baseTileHeight = getTileHeight() - offsetTileHeight;
		this.offsetTileHeight = offsetTileHeight;
		
		this.halfTileWidth = getTileWidth();// / 2;
		this.halfTileHeight = this.baseTileHeight / 2;
		
		this.horiz = this.maxX;
		this.vert = this.maxY;
		
//		this.startX = (vert - 1) * this.halfTileWidth;
//		this.startY = 0;
		
		this.startX = (vert - 1) * this.halfTileWidth;
		this.startY = 0;
		
		width = (horiz+vert) * (tileWidth / 2);
		height = ((horiz+vert) * ((getTileHeight() - offsetTileHeight) / 2)) + startY;
		
		initRenderPositions();
	}
	
	
	/* (non-Javadoc)
	 * @see newera.map.OrthoMap#getWorldTile(int, int, int)
	 */
	@Override
	public MapTile getWorldTile(int layerIndex, float worldX, float worldY) {				
		worldX -= this.vert * this.halfTileWidth;
		worldY -= this.startY;
		
		int x = (int)((worldY / this.baseTileHeight) + (worldX / this.tileWidth));
		int y = (int)((worldY / this.baseTileHeight) - (worldX / this.tileWidth));
		
		if(x<0 || x > this.horiz-1 || y < 0 || y > this.vert -1) {
			return null;
		}
		
		return getTile(layerIndex, x, y);
	}
	
	public Vector2f worldToIsoIndex(float worldX, float worldY, Vector2f out) {
		worldX -= this.vert * this.halfTileWidth;
		worldY -= this.startY;
		
		int x = (int)((worldY / this.baseTileHeight) + (worldX / this.tileWidth));
		int y = (int)((worldY / this.baseTileHeight) - (worldX / this.tileWidth));
		
		if(x<0 || x > this.horiz-1 || y < 0 || y > this.vert-1 ) {
			return null;
		}
		
		out.set(x,y);
		
		return out;
	}
	
	public Vector2f worldToIsoPosition(float worldX, float worldY, Vector2f out) {		
		MapTile tile = getWorldTile(0, worldX, worldY);
		if(tile!=null) {
			out.set(tile.getRenderX(), tile.getRenderY());
		}
		
		return out;
	}
	
	public Vector2f isoIndexToWorld(int isoX, int isoY, Vector2f out) {
//		int startX = 0;//(this.vert-1) * this.halfTileWidth;
//		int startY = (this.vert-1) * this.halfTileHeight;
//		out.x = startX + ((isoY - isoX) * this.halfTileWidth);
//		out.y = ((isoY + isoX) * this.halfTileHeight) + startY;
		
		
		int startX = (this.vert-1) * this.halfTileWidth;
		int startY = (this.vert-1) * this.halfTileHeight;
		out.x = startX + ((isoX - isoY) * this.halfTileWidth);
		out.y = startY + ((isoY + isoX) * this.halfTileHeight);
		
		return out;
	}

	public void renderIsoRect(Canvas canvas, int x, int y, int width, int height, Integer color) {
		Vector2f a = new Vector2f();
		Vector2f b = new Vector2f();
				
//		canvas.drawString(x + "," + y, x, y, color);
//		canvas.drawCircle(2, x, y, color);
		
		a.set(x,y+this.halfTileHeight);		
		b.set(x+this.halfTileWidth, y);		
		canvas.drawLine(a.x, a.y, b.x, b.y, color);
		
		
		a.set(x+this.halfTileWidth,y);		
		b.set(x+this.tileWidth, y+this.halfTileHeight);		
		canvas.drawLine(a.x, a.y, b.x, b.y, color);
		
		
		a.set(x+this.tileWidth, y+this.halfTileHeight);		
		b.set(x+this.halfTileWidth, y+this.getTileHeight());		
		canvas.drawLine(a.x, a.y, b.x, b.y, color);
		
		a.set(x+this.halfTileWidth, y+this.getTileHeight());				
		b.set(x, y+this.halfTileHeight);		
		canvas.drawLine(a.x, a.y, b.x, b.y, color);
	}

	
	private void initRenderPositions() {
		int xbg = 0;
		int ybg = 0;
		int x = 0;
		int y = 0;
		
		int w = getMapWidth();
		int h = getMapHeight();
		
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
							canvas.setFont("Courier New", 8);
							//canvas.drawString("I:"+xTile+","+yTile, x1, y1, 0xff00ffff);
							//canvas.drawString("T:"+tile.getXIndex()+","+tile.getYIndex(), x1, y1+10, 0xff00ffff);
							//renderIsoRect(canvas, x1, y1, tile.getWidth(), tile.getHeight(), 0xff00ffff);
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
	 * @see newera.map.OrthoMap#renderTile(newera.gfx.Canvas, newera.gfx.Camera, float, newera.map.MapTile, int, int)
	 */
	@Override
	protected void renderTile(Canvas canvas, Camera camera, float alpha, MapTile tile, int indexX, int indexY, int pixelX, int pixelY) {
		Vector2f pos = //new Vector2f(120, 230) ;
				camera.getRenderPosition(alpha);
//		System.out.println(pos);
		int normX = indexX * getTileWidth();
		int normY = indexY * getTileHeight();
		int renderX = normX - normY + (canvas.getHeight() / 2) - (int)pos.x;
		int renderY = (int)((normX + normY) / 2f) - (int)pos.y;
		
//		int hw = getTileWidth()/2;
//		int hh = getTileHeight()/2;
//		int renderX = (indexX * hw) - (indexY * hw);
//		int renderY = (indexX * hh) + (indexY * hh);
		
		tile.setRenderingPosition(renderX, renderY);
		tile.render(canvas, camera, alpha);
	}

}

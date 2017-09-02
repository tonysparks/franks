/*
 * see license.txt 
 */
package franks.map;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;

import franks.game.GameState;
import franks.gfx.Art;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.map.MapTile.Visibility;
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
    private int offsetX, offsetY;        
    
    private int tileX;
    
    private int horiz, vert; // total horizontal, vertical tiles

    private int width, height;
    
    private Vector2f posA = new Vector2f();
    private Vector2f posB = new Vector2f();
    
    private IsometricTiledMapRenderer mapRenderer;
    private com.badlogic.gdx.graphics.OrthographicCamera mapCamera;
    
    /**
     * @param loadAssets
     */
    public IsometricMap() {
        super(false);
        
        
    }
    
    public void initTiledMap(TiledMap tiledMap, GameState gameState) {
        this.mapCamera = gameState.getMapCamera();        
        this.mapRenderer = new IsometricTiledMapRenderer(tiledMap, gameState.getSpriteBatch());
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
    
    /* (non-Javadoc)
     * @see leola.live.game.Map#getTilesInCircle(int, int, float, java.util.List)
     */
    @Override
    public List<MapTile> getTilesInCircle(int layer, int centerX, int centerY, int radius,
            List<MapTile> tiles) {
        List<MapTile> result = (tiles == null) ? new ArrayList<MapTile>() : tiles;
        result.clear();
        
        int length = (radius * 2) + 1;
        
        for(int y = centerY - (length /2); 
            y <= (centerY + (length/2)); 
            y+=getTileHeight()/8) {
            
            for(int x = centerX - (length/2);
                x <= (centerX + (length/2));
                x+=getTileWidth()/8 ) {
                
                if(!checkBounds(x, y)) {
                    MapTile tile = getWorldTile(layer, x, y); 
                    if(tile!=null) {
                        result.add(tile);
                    }
                }
            }
        }
                
        return result;
    }
    
    @Override
    public List<MapTile> getTilesInRect(int layer, Rectangle bounds, List<MapTile> tiles) {
        List<MapTile> result = (tiles == null) ? new ArrayList<MapTile>() : tiles;
        result.clear();
        
        
        
        for(int y = bounds.y; 
                y <= (bounds.y + bounds.height); 
                y+=getTileHeight()) {
            
            for(int x = bounds.x;
                x <= (bounds.x + bounds.width);
                x+=getTileWidth()/2 ) {
                
                if(!checkBounds(x, y)) {
                    MapTile tile = getWorldTile(layer, x, y); 
                    if(tile!=null) {
                        result.add(tile);
                    }
                }
            }
        }
                
        return result;
    }
    
    public List<MapTile> getAllTilesInRect(Rectangle bounds, List<MapTile> tiles) {
        List<MapTile> result = (tiles == null) ? new ArrayList<MapTile>() : tiles;
        result.clear();
        
        
        
        for(int y = bounds.y; 
                y <= (bounds.y + bounds.height); 
                y+=getTileHeight()) {
            
            for(int x = bounds.x;
                x <= (bounds.x + bounds.width);
                x+=getTileWidth()/2 ) {
                
                if(!checkBounds(x, y)) {
                    for(int layer = 0; layer < getNumberOfLayers(); layer++) {
                        MapTile tile = getWorldTile(layer, x, y); 
                        if(tile!=null) {
                            result.add(tile);
                        }
                    }
                }
            }
        }
                
        return result;
    }
    
    public List<MapTile> getAllTilesInCircle(int centerX, int centerY, int radius,
            List<MapTile> tiles) {
        List<MapTile> result = (tiles == null) ? new ArrayList<MapTile>() : tiles;
        result.clear();
        
        int length = (radius * 2) + 1;
        
        for(int y = centerY - (length /2); 
            y <= (centerY + (length/2)); 
            y+=getTileHeight()/8) {
            
            for(int x = centerX - (length/2);
                x <= (centerX + (length/2));
                x+=getTileWidth()/8 ) {
                
                if(!checkBounds(x, y)) {
                    for(int layer = 0; layer < getNumberOfLayers(); layer++) {
                        MapTile tile = getWorldTile(layer, x, y); 
                        if(tile!=null) {
                            result.add(tile);
                        }
                    }
                }
            }
        }
                
        return result;
    }
    
    /**
     * This actually takes in screen coordinates relative to the current camera position 
     * (i.e., it expects the worldX/worldY to be screenPos + cameraPos)
     *  
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
     * Gets the tile at the Tile Position.  The Tile Position is the {@link MapTile#getX()} 
     * and {@link MapTile#getY()} position of the tile.
     * 
     * @param layerIndex
     * @param tilePosX
     * @param tilePosY
     * @return the {@link MapTile} located at the tile position. Null if out of bounds
     */
    public MapTile getTileAtTilePos(int layerIndex, float tilePosX, float tilePosY) {
        int indexX = (int)tilePosX / getTileWidth();
        int indexY = (int)tilePosY / getTileHeight();
        
        if(!checkTileBounds(indexX, indexY)) {
            return getTile(0, indexX, indexY);
        }
        
        return null;
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
                
//        canvas.drawString(x + "," + y, x, y, color);
//        canvas.drawCircle(2, x, y, color);
        
//        float hw = width;
//        float hh = height/2f;
//        
//        posA.set(x,y+hh);//this.halfTileHeight);        
//        posB.set(x+hw/*this.halfTileWidth*/, y);        
//        canvas.drawLine(posA.x, posA.y, posB.x, posB.y, color);
//        
//        
//        posA.set(x+hw/*this.halfTileWidth*/,y);        
//        posB.set(x+width*2f/*this.tileWidth*/, y+hh/*this.halfTileHeight*/);        
//        canvas.drawLine(posA.x, posA.y, posB.x, posB.y, color);
//        
//        
//        posA.set(x+width*2f/*this.tileWidth*/, y+hh/*this.halfTileHeight*/);        
//        posB.set(x+hw/*this.halfTileWidth*/, y+height/*this.getTileHeight()*/);        
//        canvas.drawLine(posA.x, posA.y, posB.x, posB.y, color);
//        
//        posA.set(x+hw/*this.halfTileWidth*/, y+height/*this.getTileHeight()*/);                
//        posB.set(x, y+hh/*this.halfTileHeight*/);        
//        canvas.drawLine(posA.x, posA.y, posB.x, posB.y, color);
        
        float cols = (width/getTileWidth()   + ((width%getTileWidth()  >0) ? 1 : 0));
        float rows = (height/getTileHeight() + ((height%getTileHeight()>0) ? 1 : 0));
        
        //width  = (width/getTileWidth()   + ((width%getTileWidth()  >0) ? 1 : 0)) * getTileWidth();
        //height = (height/getTileHeight() + ((height%getTileHeight()>0) ? 1 : 0)) * getTileHeight();
        
        float c60 = (float)Math.cos(Math.toRadians(30));
        float c30 = (float)Math.cos(Math.toRadians(60));
        float s30 = (float)Math.sin(Math.toRadians(60));
        float s60 = (float)Math.sin(Math.toRadians(30));
        
        float ow = width;
        float oh = height;
        
        width  = getTileWidth() * c30 * cols + getTileHeight() * c60 * rows;
        height = getTileWidth() * s30 * cols + getTileHeight() * s60 * rows;
        
        width  *= 1;
        height *= 1;
        
        width = (int)width;
        height = (int)height;
        
       // x -= ow / 3;
        //y -= oh / 10;
        
        /////
        float hw = width;
        float hh = height/2f;
        
        canvas.fillCircle(5, x, y+hh, color);
        
        posA.set(x,y+hh);        
        posB.set(x+hw, y);        
        canvas.drawLine(posA.x, posA.y, posB.x, posB.y, color);
        
        
        posA.set(x+hw,y);     
        posB.set(x+width*2f, y+hh);        
        canvas.drawLine(posA.x, posA.y, posB.x, posB.y, color);
        
        
        posA.set(x+width*2f, y+hh);        
        posB.set(x+hw, y+height);     
        canvas.drawLine(posA.x, posA.y, posB.x, posB.y, color);
        
        posA.set(x+hw, y+height);             
        posB.set(x, y+hh);       
        canvas.drawLine(posA.x, posA.y, posB.x, posB.y, color);
        
        /////
        
        
        
//        a.set(x,y+hh);//this.halfTileHeight);        
//        b.set(x+hw/*this.halfTileWidth*/, y);        
//        canvas.drawLine(a.x, a.y, b.x, b.y, color);
//        
//        
//        a.set(x+hw/*this.halfTileWidth*/,y);        
//        b.set(x+width*2f/*this.tileWidth*/, y+hh/*this.halfTileHeight*/);        
//        canvas.drawLine(a.x, a.y, b.x, b.y, color);
//        
//        
//        a.set(x+width*2f/*this.tileWidth*/, y+hh/*this.halfTileHeight*/);        
//        b.set(x+hw/*this.halfTileWidth*/, y+height/*this.getTileHeight()*/);        
//        canvas.drawLine(a.x, a.y, b.x, b.y, color);
//        
//        a.set(x+hw/*this.halfTileWidth*/, y+height/*this.getTileHeight()*/);                
//        b.set(x, y+hh/*this.halfTileHeight*/);        
//        canvas.drawLine(a.x, a.y, b.x, b.y, color);
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
    
    
    @Override
    public void render(Canvas canvas, Camera camera, float alpha) {
        Vector2f pos = camera.getRenderPosition(alpha);
         
        // HACK - fixme, remove these magic numbers and make it work with any map size/tile sizes
        if(this.tileWidth == 64) {
            // Meta Map
            this.mapCamera.position.set(pos.x + this.offsetX - 88, (Gdx.graphics.getHeight() - pos.y) + 566, 0f);
        }
        else {
            // Battle Map
            this.mapCamera.position.set(pos.x + this.offsetX - 88, (Gdx.graphics.getHeight() - pos.y) - 636, 0f);        
        }
                
        this.mapCamera.update();
        this.mapRenderer.setView(mapCamera);
        this.mapRenderer.render();
        
        //mapRender(canvas, camera, alpha);
    }
    
    public void mapRenderFoW(Canvas canvas, Camera camera, float alpha) {
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
            

            while (true) {
                if (x1 >= x2 || y1 >= y2 || xTile >= this.horiz - 1) {
                    break;
                }
                
                xTile++;
                if (x1 + this.tileWidth > x) {
                    MapTile tile = getTile(0, xTile, yTile);
                    if(tile!=null) {
                        Visibility visibility = tile.getVisibility();
                        if(visibility==Visibility.BLACKED_OUT) {
                            canvas.drawImage(Art.blackedOutTile, x1, y1, 0xffffffff);
                        }
                        else if(visibility==Visibility.VISITED) {
                            canvas.drawImage(Art.fadedOutTile, x1, y1, 0xafffffff);
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
    //@Override
    //public void render(Canvas canvas, Camera camera, float alpha) {
    public void mapRender(Canvas canvas, Camera camera, float alpha) {
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
            

            while (true) {
                if (x1 >= x2 || y1 >= y2 || xTile >= this.horiz - 1) {
                    break;
                }
                
                xTile++;
                if (x1 + this.tileWidth > x) {
                    Layer[] layers = this.collidableLayers;
                    
                    for(int layerIndex = 0; layerIndex < layers.length; layerIndex++) {
                        Layer layer = layers[layerIndex];
                        if(layer!=null) {                        
                            MapTile tile = layer.getRow(yTile)[xTile];
                            if(tile!=null) {
                                tile.setRenderingPosition(x1,y1);
                                tile.render(canvas, camera, alpha);
                                //canvas.resizeFont(8f);
                                //canvas.drawString(xTile +"," + yTile, x1, y1+16, 0xffffffff);
                            }
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

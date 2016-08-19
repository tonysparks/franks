/*
 * see license.txt 
 */
package franks.game;

import java.util.function.Consumer;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.gfx.Cursor;
import franks.gfx.Renderable;
import franks.gfx.TextureUtil;
import franks.graph.GraphNode;
import franks.map.GraphNodeFactory;
import franks.map.ImageTile;
import franks.map.IsometricMap;
import franks.map.Layer;
import franks.map.Map;
import franks.map.MapGraph;
import franks.map.MapTile;
import franks.map.Map.SceneDef;
import franks.math.Vector2f;
import franks.util.TimeStep;

/**
 * @author Tony
 *
 */
public class World implements Renderable {
	public static final int RegionWidth = 32;
	public static final int RegionHeight = 32;
	
	private Region[][] regions;
	private IsometricMap map;
	private Cursor cursor;
	private Camera camera;
	
	private MapGraph<Void> graph;
	
	private TextureRegion tileImg;
	private Vector2f cacheVector;
	/**
	 * 
	 */
	public World(Camera camera, Cursor cursor) {
		this.camera = camera;
		this.cursor = cursor;
		
		this.cacheVector = new Vector2f();
		this.regions = new Region[64][64];
		for(int y = 0; y < this.regions.length; y++) {
			for(int x = 0; x < this.regions[y].length; x++) {
				this.regions[y][x] = new Region(x,y, RegionWidth, RegionHeight);
			}
		}
		
		int tileWidth = RegionWidth*2;
		int tileHeight = RegionHeight;
		
		SceneDef scene = new SceneDef();
		//scene.setTileWidth(tileWidth);
		//scene.setTileHeight(tileHeight);
		scene.setTileWidth(RegionWidth*2);
		scene.setTileHeight(RegionHeight);
		Layer background = new Layer("ground", false, false, false, false, true, 0, 0, this.regions.length);
		scene.setDimensionY(this.regions.length);
		scene.setDimensionX(this.regions[0].length);
		scene.setBackgroundLayers(new Layer[] { background });
		scene.setForegroundLayers(new Layer[] {});
		
		int xrow = 1024 / tileWidth;
		int xcol = 1024 / tileHeight;
		TextureRegion tex = TextureUtil.loadImage("./assets/gfx/tiles.png", xrow, xcol);
		TextureRegion texTile = new TextureRegion(tex.getTexture(), 0, 0, tileWidth, tileHeight);
		tileImg = texTile;
		
		for(int y = 0; y < this.regions.length; y++) {
			MapTile[] row = new MapTile[this.regions[0].length];
			for(int x = 0; x < this.regions[0].length; x++ ) {
				//MapTile tile = new ColoredTile(0xff81aa81, 0xff000000, 0, RegionWidth, RegionHeight);
				//tile.setPosition(x*RegionWidth, y*RegionHeight);
				MapTile tile = new ImageTile(texTile, 0, RegionWidth, RegionHeight); //tileWidth, tileHeight);
				//tile.setPosition(x*tileWidth, y*tileHeight);
				tile.setPosition(x*RegionWidth, y*RegionHeight);
				tile.setIndexPosition(x, y);
				row[x] = tile;
			}
			
			background.addRow(y, row);
		}
		
		this.map =// new OrthoMap(true); 
				new IsometricMap(true);
		this.map.init(scene);
		
		this.graph = this.map.createMapGraph(new GraphNodeFactory<Void>() {
			@Override
			public Void createEdgeData(Map map, GraphNode<MapTile, Void> left, GraphNode<MapTile, Void> right) {
				return null;
			}
		});
		
		this.camera.setWorldBounds(new Vector2f(this.map.getMapWidth(), this.map.getMapHeight()));
	}

	public int getRegionWidth() {
		return RegionWidth;
	}
	
	public int getRegionHeight() {
		return RegionHeight;
	}
	
	/**
	 * @return the map
	 */
	public IsometricMap getMap() {
		return map;
	}
	
	/**
	 * @return the graph
	 */
	public MapGraph<Void> getGraph() {
		return graph;
	}
	
	
	public void foreachRegion(Consumer<Region> f) {
		for(int y = 0; y < this.regions.length; y++) {
			for(int x = 0; x < this.regions[y].length; x++) {
				f.accept(this.regions[y][x]);
			}
		}
	}
	
	public Region getRegion(int x, int y) {
		return this.regions[y][x];
	}
	
	public MapTile getMapTileByWorldPos(Vector2f pos) {
		MapTile tile = this.map.getWorldTile(0, pos.x, pos.y);
		return tile;
	}
	
	public Vector2f snapMapTilePos(Vector2f pos) {
		MapTile tile = this.map.getWorldTile(0, pos.x, pos.y);
		if(tile != null) {
			return new Vector2f(tile.getX() /*+ tile.getWidth()/2f*/, tile.getY() /*+ tile.getHeight()/2f*/);
		}
		return null;
	}
	
	
	public Vector2f screenToWorldCoordinates(Vector2f pos) {
		return screenToWorldCoordinates(pos.x, pos.y);
	}
	public Vector2f screenToWorldCoordinates(Vector2f pos, Vector2f out) {
		return screenToWorldCoordinates(pos.x, pos.y, out);
	}
	
	/**
	 * @param x - screen x position
	 * @param y - screen y position
	 * @return the x and y converted to world coordinates
	 */
	public Vector2f screenToWorldCoordinates(float x, float y) {
		return screenToWorldCoordinates(x, y, new Vector2f());
	}
	
	/**
	 * @param x - screen x position
	 * @param y - screen y position
	 * @param out
	 * @return the x and y converted to world coordinates
	 */
	public Vector2f screenToWorldCoordinates(float x, float y, Vector2f out) {
		Vector2f pos = camera.getPosition();
		out.set(x + pos.x, y + pos.y);
		return out;
	}
	
	public Vector2f worldToScreen(float worldX, float worldY, Vector2f out) {
		Vector2f pos = camera.getPosition();
		out.set(worldX - pos.x, worldY - pos.y);
		return out;
	}
	
	public Vector2f worldToIso(float worldX, float worldY, Vector2f out) {
		//worldToScreen(worldX, worldY, out);
		this.map.screenToIsoPosition(worldX, worldY, out);
		//this.map.worldToIsoIndex(worldX, worldY, out);
		//Vector2f.Vector2fSubtract(out, camera.getPosition(), out);
		return out;
	}
	
	public Vector2f isoIndexToWorld(int isoX, int isoY, Vector2f out) {
		this.map.isoIndexToWorld(isoX, isoY, out); 
		return out;
	}
	
	/* (non-Javadoc)
	 * @see newera.gfx.Renderable#update(newera.util.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		this.map.update(timeStep);
		
//		for(int y = 0; y < this.regions.length; y++) {
//			for(int x = 0; x < this.regions[y].length; x++) {
//				this.regions[y][x].update(timeStep);
//			}
//		}
	}
	
	/* (non-Javadoc)
	 * @see newera.gfx.Renderable#render(newera.gfx.Canvas, newera.gfx.Camera, float)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {
		this.map.render(canvas, camera, alpha);
		
		Vector2f pos = cursor.getCenterPos();
		pos = screenToWorldCoordinates(pos.x, pos.y);		
		MapTile tile = this.map.getWorldTile(0, pos.x, pos.y);
		
		canvas.drawString( "Screen: " + (int)cursor.getCenterPos().x+","+ (int)cursor.getCenterPos().y, cursor.getX()-50, cursor.getY()+40, 0xffffffff);
		canvas.drawString( "World:  " + (int)pos.x+","+ (int)pos.y, cursor.getX()-50, cursor.getY()+60, 0xffffffff);
		
		if(tile != null) {
			canvas.drawString( "IsoPos:  " + (int)tile.getIsoX()+","+ (int)tile.getIsoY(), cursor.getX()-50, cursor.getY()+80, 0xffffffff);
			canvas.drawString( "TileIndex:  " + (int)tile.getXIndex()+","+ (int)tile.getYIndex(), cursor.getX()-50, cursor.getY()+100, 0xffffffff);
			canvas.drawString( "TilePos:  " + (int)tile.getX()+","+ (int)tile.getY(), cursor.getX()-50, cursor.getY()+120, 0xffffffff);
			
			//canvas.drawRect(tile.getRenderX(), tile.getRenderY(), tile.getWidth(), tile.getHeight(), 0xffffffff);
			Vector2f c = camera.getRenderPosition(alpha);
			map.renderIsoRect(canvas, tile.getIsoX()-c.x, tile.getIsoY()-c.y, tile.getWidth(), tile.getHeight(), 0xffffffff);
			//canvas.drawString( tile.getRenderX()+","+tile.getRenderY(), 10, 70, 0xffffffff);
		}
		
		canvas.drawImage(tileImg, 10, canvas.getHeight() - 100, null);
		//canvas.drawCircle(2, 10, canvas.getHeight() - 100, 0xffffffff);
		canvas.drawRect(10, canvas.getHeight()-100, tileImg.getRegionWidth(), tileImg.getRegionHeight(), 0xffffffff);
		//canvas.drawCircle(2, cursor.getCenterPos().x, cursor.getCenterPos().y, 0xffffffff);
//		for(int y = 0; y < this.regions.length; y++) {
//			for(int x = 0; x < this.regions[y].length; x++) {
//				this.regions[y][x].render(canvas, camera, alpha);
//			}
//		}
	}
}

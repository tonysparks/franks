/*
 * see license.txt 
 */
package newera.game;

import java.util.function.Consumer;

import newera.gfx.Camera;
import newera.gfx.Canvas;
import newera.gfx.Cursor;
import newera.gfx.Renderable;
import newera.graph.GraphNode;
import newera.map.ColoredTile;
import newera.map.GraphNodeFactory;
import newera.map.Layer;
import newera.map.Map;
import newera.map.MapGraph;
import newera.map.Map.SceneDef;
import newera.map.MapTile;
import newera.map.OrthoMap;
import newera.math.Vector2f;
import newera.util.TimeStep;

/**
 * @author Tony
 *
 */
public class World implements Renderable {
	public static final int RegionWidth = 32;
	public static final int RegionHeight = 32;
	
	private Region[][] regions;
	private Map map;
	private Cursor cursor;
	private Camera camera;
	
	private MapGraph<Void> graph;
	
	/**
	 * 
	 */
	public World(Camera camera, Cursor cursor) {
		this.camera = camera;
		this.cursor = cursor;
		
		this.regions = new Region[16][16];
		for(int y = 0; y < this.regions.length; y++) {
			for(int x = 0; x < this.regions[y].length; x++) {
				this.regions[y][x] = new Region(x,y, RegionWidth, RegionHeight);
			}
		}
		
		SceneDef scene = new SceneDef();
		scene.setTileWidth(RegionWidth);
		scene.setTileHeight(RegionHeight);
		Layer background = new Layer("ground", false, false, false, false, true, 0, 0, this.regions.length);
		scene.setDimensionY(this.regions.length);
		scene.setDimensionX(this.regions[0].length);
		scene.setBackgroundLayers(new Layer[] { background });
		scene.setForegroundLayers(new Layer[] {});
		
		for(int y = 0; y < this.regions.length; y++) {
			MapTile[] row = new MapTile[this.regions[0].length];
			for(int x = 0; x < this.regions[0].length; x++ ) {
				MapTile tile = new ColoredTile(0xff81aa81, 0xff000000, 0, RegionWidth, RegionHeight);
				tile.setPosition(x*RegionWidth, y*RegionHeight);
				tile.setIndexPosition(x, y);
				row[x] = tile;
			}
			
			background.addRow(y, row);
		}
		
		this.map = new OrthoMap(true);
		this.map.init(scene);
		
		this.graph = this.map.createMapGraph(new GraphNodeFactory<Void>() {
			@Override
			public Void createEdgeData(Map map, GraphNode<MapTile, Void> left, GraphNode<MapTile, Void> right) {
				return null;
			}
		});
	}

	public int getRegionWidth() {
		return RegionWidth;
	}
	
	public int getRegionHeight() {
		return RegionHeight;
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
		MapTile tile = this.map.getWorldTile(0, (int)pos.x, (int)pos.y);
		return tile;
	}
	
	public Vector2f snapMapTilePos(Vector2f pos) {
		MapTile tile = this.map.getWorldTile(0, (int)pos.x, (int)pos.y);
		if(tile != null) {
			return new Vector2f(tile.getX() + tile.getWidth()/2f, tile.getY() + tile.getHeight()/2f);
		}
		return null;
	}
	
	
	public Vector2f screenToWorldCoordinates(Vector2f pos) {
		return screenToWorldCoordinates((int)pos.x, (int)pos.y);
	}
	public Vector2f screenToWorldCoordinates(Vector2f pos, Vector2f out) {
		return screenToWorldCoordinates((int)pos.x, (int)pos.y, out);
	}
	
	/**
	 * @param x - screen x position
	 * @param y - screen y position
	 * @return the x and y converted to world coordinates
	 */
	public Vector2f screenToWorldCoordinates(int x, int y) {
		return screenToWorldCoordinates(x, y, new Vector2f());
	}
	
	/**
	 * @param x - screen x position
	 * @param y - screen y position
	 * @param out
	 * @return the x and y converted to world coordinates
	 */
	public Vector2f screenToWorldCoordinates(int x, int y, Vector2f out) {
		Vector2f pos = camera.getPosition();
		out.set(x + pos.x, y + pos.y);
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
		
		Vector2f pos = cursor.getCursorPos();
		pos = screenToWorldCoordinates((int)pos.x, (int)pos.y);		
		MapTile tile = this.map.getWorldTile(0, (int)pos.x, (int)pos.y);
		if(tile != null) {
			canvas.drawRect(tile.getRenderX(), tile.getRenderY(), tile.getWidth(), tile.getHeight(), 0xffffffff);
		}
		
//		for(int y = 0; y < this.regions.length; y++) {
//			for(int x = 0; x < this.regions[y].length; x++) {
//				this.regions[y][x].render(canvas, camera, alpha);
//			}
//		}
	}
}

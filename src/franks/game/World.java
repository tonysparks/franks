/*
 * see license.txt 
 */
package franks.game;

import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.gfx.Renderable;
import franks.graph.GraphNode;
import franks.map.GraphNodeFactory;
import franks.map.IsometricMap;
import franks.map.Map;
import franks.map.MapGraph;
import franks.map.MapLoader;
import franks.map.MapLoader.TiledData;
import franks.map.MapTile;
import franks.map.TiledMapLoader;
import franks.math.Vector2f;
import franks.util.Cons;
import franks.util.TimeStep;

/**
 * @author Tony
 *
 */
public class World implements Renderable {
	public static final int TileWidth = 32;
	public static final int TileHeight = 32;

	public static final int CellTileWidth = 2;
	public static final int CellTileHeight = 2;
	
	public static final int CellWidth = TileWidth * CellTileWidth;
	public static final int CellHeight = TileHeight * CellTileHeight;
	
	private IsometricMap map;
	private Camera camera;
	
	private MapGraph<Void> graph;
	
	private Vector2f cacheVector;
	
	/**
	 * 
	 */
	public World(Game game) {
		this.camera = game.getCamera();				
		this.cacheVector = new Vector2f();
		
		TerrainData terrainData = game.loadData("assets/maps/frank_map01-terrain.json", TerrainData.class);
		TiledData tiledData = game.loadData("assets/maps/frank_map01.json", TiledData.class);
		
		MapLoader loader = new TiledMapLoader();
		try {
			this.map = (IsometricMap)loader.loadMap(tiledData, true);
		} 
		catch (Exception e) {
			Cons.println("Unable to load map: " + e);
		}
		
		int numberOfTilesX = this.map.getTileWorldWidth();
		int numberOfTilesY = this.map.getTileWorldWidth();
		
//		int numberOfTilesX = 12;
//		int numberOfTilesY = 12;
				
//		int tileWidth = TileWidth*2;
//		int tileHeight = TileHeight;
		
//		SceneDef scene = new SceneDef();
//		scene.setTileWidth(tileWidth);
//		scene.setTileHeight(tileHeight);
//		
//		Layer background = new Layer("ground", false, false, false, false, true, 0, 0, numberOfTilesY);
//		scene.setDimensionX(numberOfTilesX);
//		scene.setDimensionY(numberOfTilesY);
//		scene.setBackgroundLayers(new Layer[] { background });
//		scene.setForegroundLayers(new Layer[] {});
		
//		int xrow = 1024 / tileWidth;
//		int xcol = 1024 / tileHeight;
//		
//		TextureRegion tex = TextureUtil.loadImage("./assets/gfx/tiles.png", xrow, xcol);
//		TextureRegion grassTile = new TextureRegion(tex.getTexture(), 0, 0, tileWidth, tileHeight);
				
		for(int y = 0; y < numberOfTilesY; y++) {
			//MapTile[] row = new MapTile[numberOfTilesX];
		
			for(int x = 0; x < numberOfTilesX; x++ ) {
				//MapTile tile = new ImageTile(grassTile, 0, TileWidth, TileHeight);
				MapTile tile = this.map.getTile(0, x, y);
				tile.setSize(TileWidth, TileHeight);
				tile.setPosition(x*TileWidth, y*TileHeight);
				tile.setIndexPosition(x, y);
				tile.setTerrainTileData(terrainData.getTileTerrainData(x, y));
				//row[x] = tile;
			}
			
			
//			background.addRow(y, row);
		}

		
//		this.map = new IsometricMap(true);
//		this.map.init(scene);
		
		this.graph = this.map.createMapGraph(new GraphNodeFactory<Void>() {
			@Override
			public Void createEdgeData(Map map, GraphNode<MapTile, Void> left, GraphNode<MapTile, Void> right) {
				return null;
			}
		});
		
		this.camera.setWorldBounds(new Vector2f(this.map.getMapWidth(), this.map.getMapHeight()));

	}

	public int getRegionWidth() {
		return TileWidth;
	}
	
	public int getRegionHeight() {
		return TileHeight;
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
	
	public MapTile getMapTile(Vector2f tilePos) {		
		MapTile tile = this.map.getWorldTile(0, tilePos.x, tilePos.y);
		return tile;
	}
		
	/**
	 * This will internally adjust the supplied screen coordinates by the current 
	 * camera position and return the {@link MapTile} underneath the position
	 * 
	 * @param screenPos the screen position 
	 * @return the {@link MapTile} underneath the supplied screen position, or null
	 * if no {@link MapTile}
	 */
	public MapTile getMapTileByScreenPos(Vector2f screenPos) {
		screenRelativeToCamera(screenPos, cacheVector);
		MapTile tile = this.map.getWorldTile(0, cacheVector.x, cacheVector.y);
		return tile;
	}
	
	/**
	 * This will internally adjust the supplied screen coordinates by the current 
	 * camera position and return the {@link MapTile} tile coordinates underneath the position
	 * 
	 * @param screenPos the screen position 
	 * @return the {@link MapTile} tile coordinates underneath the supplied screen position, or null
	 * if no {@link MapTile}
	 */
	public Vector2f getMapTilePosByScreenPos(Vector2f screenPos) {
		screenRelativeToCamera(screenPos, cacheVector);
		MapTile tile = this.map.getWorldTile(0, cacheVector.x, cacheVector.y);
		if(tile != null) {
			//return new Vector2f(tile.getX() /*+ tile.getWidth()/2f*/, tile.getY() /*+ tile.getHeight()/2f*/);
			cacheVector.set(tile.getX(), tile.getY());
			return cacheVector;
		}
		return null;
	}
	
	/**
	 * Get the screen position given the supplied tile coordinates
	 * 
	 * @param tilePos the tile position
	 * @param out the screen coordinates for the supplied tile coordinates
	 * @return the screen coordinates for the supplied tile coordinates
	 */
	public Vector2f getScreenPosByMapTileIndex(Vector2f tilePos, Vector2f out) {
		return map.isoIndexToScreen(tilePos.x, tilePos.y, out);
	}
	
	/**
	 * Get the screen position given the supplied tile coordinates
	 * 
	 * @param tileX the tile position x
	 * @param tileY the tile position y
	 * @param out the screen coordinates for the supplied tile coordinates
	 * @return the screen coordinates for the supplied tile coordinates
	 */
	public Vector2f getScreenPosByMapTileIndex(float tileX, float tileY, Vector2f out) {
		return map.isoIndexToScreen(tileX, tileY, out);
	}
	
	
	public Vector2f screenRelativeToCamera(Vector2f pos) {
		return screenRelativeToCamera(pos.x, pos.y);
	}
	public Vector2f screenRelativeToCamera(Vector2f pos, Vector2f out) {
		return screenRelativeToCamera(pos.x, pos.y, out);
	}
	
	/**
	 * @param x - screen x position
	 * @param y - screen y position
	 * @return the x and y relative to the current camera position
	 */
	public Vector2f screenRelativeToCamera(float x, float y) {
		return screenRelativeToCamera(x, y, cacheVector);
	}
	
	/**
	 * @param x - screen x position
	 * @param y - screen y position
	 * @param out
	 * @return the x and y relative to the current camera position
	 */
	public Vector2f screenRelativeToCamera(float x, float y, Vector2f out) {
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
	}
	
	/* (non-Javadoc)
	 * @see newera.gfx.Renderable#render(newera.gfx.Canvas, newera.gfx.Camera, float)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {
		this.map.render(canvas, camera, alpha);		
	}
}

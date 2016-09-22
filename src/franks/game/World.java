/*
 * see license.txt 
 */
package franks.game;

import java.util.ArrayList;
import java.util.List;

import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.gfx.Renderable;
import franks.graph.GraphNode;
import franks.map.GraphNodeFactory;
import franks.map.IsometricMap;
import franks.map.Layer;
import franks.map.Map;
import franks.map.MapGraph;
import franks.map.MapLoader;
import franks.map.MapLoader.TiledData;
import franks.map.MapObject;
import franks.map.MapObjectData;
import franks.map.MapTile;
import franks.map.MapTile.Visibility;
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
	
	private IsometricMap map;
	private Camera camera;
	
	private MapGraph<Void> graph;
	
	private Vector2f cacheVector;
	
	private List<MapObject> mapObjects;
	
	private int regionWidth;
	private int regionHeight;
	
	/**
	 * 
	 */
	public World(ResourceLoader resourceLoader, Camera camera, String map) {
		this.camera = camera;		
		this.cacheVector = new Vector2f();
		this.mapObjects = new ArrayList<>();
	
		String path = "assets/maps/%s.json";
		String terrainFile = String.format(path, map + "-terrain");
		String mapFile = String.format(path, map);
		
		TerrainData terrainData = resourceLoader.loadData(terrainFile, TerrainData.class);
		TiledData tiledData = resourceLoader.loadData(mapFile, TiledData.class);
		
		MapLoader loader = new TiledMapLoader();
		try {
			this.map = (IsometricMap)loader.loadMap(tiledData, true);
		} 
		catch (Exception e) {
			Cons.println("Unable to load map: " + e);
		}
		
		int numberOfTilesX = this.map.getTileWorldWidth();
		int numberOfTilesY = this.map.getTileWorldWidth();
		
		this.regionWidth = this.map.getTileWidth();
		this.regionHeight = this.map.getTileHeight();
		
		for(int y = 0; y < numberOfTilesY; y++) {
			//MapTile[] row = new MapTile[numberOfTilesX];
		
			for(int x = 0; x < numberOfTilesX; x++ ) {
				//MapTile tile = new ImageTile(grassTile, 0, TileWidth, TileHeight);
				MapTile tile = this.map.getTile(0, x, y);
				if(tile!=null) {
					tile.setSize(regionWidth, regionHeight);
					tile.setPosition(x*regionWidth, y*regionHeight);
					tile.setIndexPosition(x, y);
					if(terrainData!=null) {
						tile.setTerrainTileData(terrainData.getTileTerrainData(x, y));
					}
				}
				//row[x] = tile;
			}
			
			
//			background.addRow(y, row);
		}
		
		TextureCache textureCache = resourceLoader.getTextureCache();
		if(terrainData != null && terrainData.mapObjects != null) { 
			for(MapObjectData data : terrainData.mapObjects) {
				if(data.locations != null) {
					for(Vector2f pos : data.locations) {
						this.mapObjects.add(new MapObject(this, textureCache, pos, data));
					}
				}
			}
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
	
	public void setVisibility(Visibility visibility) {
		Layer[] bkLayers = map.getBackgroundLayers();
		Layer[] fgLayers = map.getForegroundLayers();
		for(int y = 0; y < this.map.getTileWorldHeight(); y++) {
			for(int x = 0; x < this.map.getTileWorldWidth(); x++) {
				for(Layer layer : bkLayers) {
					MapTile tile = layer.getRow(y)[x];
					if(tile!=null) tile.setVisibility(visibility);
				}
				for(Layer layer : fgLayers) {
					MapTile tile = layer.getRow(y)[x];
					if(tile!=null) tile.setVisibility(visibility);
				}
			}
		}
	}
	
	List<MapTile> visibilityTiles = new ArrayList<>();
	public void updateVisibility() {
		
		map.getTilesInRect(camera.getWorldViewPort(), visibilityTiles);
		for(MapTile tile : visibilityTiles) {
			Visibility visibility = tile.getVisibility();
			switch(visibility) {
				case VISIBLE: tile.setVisibility(Visibility.VISITED);
				default : 
			}
		}
		
		/*
		Layer[] bkLayers = map.getBackgroundLayers();
		Layer[] fgLayers = map.getForegroundLayers();
		for(int y = 0; y < this.map.getTileWorldHeight(); y++) {
			for(int x = 0; x < this.map.getTileWorldWidth(); x++) {
				for(Layer layer : bkLayers) {
					MapTile tile = layer.getRow(y)[x]; 
					Visibility visibility = tile.getVisibility();
					switch(visibility) {
						case VISIBLE: tile.setVisibility(Visibility.VISITED);
						default : 
					}
				}
				for(Layer layer : fgLayers) {
					MapTile tile = layer.getRow(y)[x]; 
					Visibility visibility = tile.getVisibility();
					switch(visibility) {
						case VISIBLE: tile.setVisibility(Visibility.VISITED);
						default : 
					}
				}
			}
		}*/
	}

	public int getRegionWidth() {
		return regionWidth;
	}
	
	public int getRegionHeight() {
		return regionHeight;
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
		for(int i = 0; i< this.mapObjects.size(); i++) {
			this.mapObjects.get(i).update(timeStep);
		}
	}
	
	public void renderOverEntities(Canvas canvas, Camera camera, float alpha) {
		for(int i = 0; i< this.mapObjects.size(); i++) {
			MapObject object = this.mapObjects.get(i);
			if(object.renderOverEntities()) {
				this.mapObjects.get(i).render(canvas, camera, alpha);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see newera.gfx.Renderable#render(newera.gfx.Canvas, newera.gfx.Camera, float)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {
		this.map.render(canvas, camera, alpha);	
		
		for(int i = 0; i< this.mapObjects.size(); i++) {
			MapObject object = this.mapObjects.get(i);
			if(!object.renderOverEntities()) {
				this.mapObjects.get(i).render(canvas, camera, alpha);
			}
		}
	}
}

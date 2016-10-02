/*
 * see license.txt 
 */
package franks.map;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TmxMapLoader.Parameters;
import com.google.gson.JsonPrimitive;

import franks.game.GameState;
import franks.game.ResourceLoader;
import franks.game.TerrainData;
import franks.map.MapLoader.TiledData;
import franks.map.MapLoader.TiledLayer;
import franks.map.MapLoader.TiledTilesetData;
import franks.util.Cons;

/**
 * @author Tony
 *
 */
public class GdxTmxMapLoader  {

	public TiledData loadTiledData(TiledMap tiledMap) {
		MapLayers layers = tiledMap.getLayers();
		MapProperties mapProps = tiledMap.getProperties();
		TiledData tiledData = new TiledData();
		tiledData.orientation = mapProps.get("orientation", String.class);
		tiledData.nextobjectid = mapProps.get("nextobjectid", 1, Integer.class);
		tiledData.renderorder = mapProps.get("renderorder", String.class);
		tiledData.width = mapProps.get("width", Integer.class);
		tiledData.height = mapProps.get("height", Integer.class);
		tiledData.tilewidth = mapProps.get("tilewidth", Integer.class);
		tiledData.tileheight = mapProps.get("tileheight", Integer.class);
				
		tiledData.layers = new ArrayList<>(layers.getCount());
		for(MapLayer mapLayer : layers) {
			TiledMapTileLayer layer = (TiledMapTileLayer)mapLayer;
			TiledLayer tiledLayer = new TiledLayer();
			//tiledLayer.data = layer.
			tiledLayer.name = layer.getName();
			tiledLayer.opacity = layer.getOpacity();
			tiledLayer.width = layer.getWidth();
			tiledLayer.height = layer.getHeight();
			tiledLayer.properties = new HashMap<>();
			
			MapProperties layerProps = layer.getProperties();
			layerProps.getKeys().forEachRemaining( 
					key -> tiledLayer.properties.put(key, new JsonPrimitive(layerProps.get(key, String.class))));
			
			tiledLayer.data = new int[tiledLayer.width * tiledLayer.height];
			
			for(int y = 0; y < tiledLayer.height; y++) {
				for(int x = 0; x < tiledLayer.width; x++) {					
					Cell cell = layer.getCell(x, tiledLayer.height - 1 - y );
					if(cell != null) {
						TiledMapTile tile = cell.getTile();
						if(tile!=null) {
							tiledLayer.data[tiledLayer.width * y + x] = tile.getId();
						}
					}
				}
			}
			
			tiledData.layers.add(tiledLayer);
		}
		
		tiledData.tilesets = new ArrayList<>();
		tiledMap.getTileSets().forEach( tileSet -> {
			TiledTilesetData tilesetData = new TiledTilesetData();
			MapProperties props = tileSet.getProperties();
			tilesetData.firstgid = props.get("firstgid", Integer.class);
			tilesetData.name = tileSet.getName();
			tilesetData.tilewidth = props.get("tilewidth", Integer.class);
			tilesetData.tileheight = props.get("tileheight", Integer.class);
			tilesetData.tilecount = tileSet.size();
			//tilesetData.columns = props.get("columns", Integer.class);			
			tilesetData.image = props.get("imagesource", String.class);
			if(tilesetData.image.startsWith("..")) {
				tilesetData.image = "./assets" + tilesetData.image.replace("..", "");
			}
			
			tilesetData.imagewidth = props.get("imagewidth", 0, Integer.class);
			tilesetData.imageheight = props.get("imageheight", 0, Integer.class);
			tilesetData.margin = props.get("margin", 0, Integer.class);
			tilesetData.spacing = props.get("spacing", 0, Integer.class);
			tiledData.tilesets.add(tilesetData);
		});
		
		return tiledData;
	}
	
	public IsometricMap loadMap(GameState state, ResourceLoader resourceLoader, String mapName) throws Exception {
		String path = "assets/maps/%s.json";
		String tmxpath = "assets/maps/%s.tmx";		
		String terrainFile = String.format(path, mapName + "-terrain");
		String mapFile = String.format(tmxpath, mapName);
		
		IsometricMap map = null;
		TerrainData terrainData = resourceLoader.loadData(terrainFile, TerrainData.class);
		
		try {
			
			TmxMapLoader tmxLoader = new TmxMapLoader();
			Parameters params = new TmxMapLoader.Parameters();
			params.flipY = true;
			
			TiledMap tiledMap = tmxLoader.load(mapFile, params);
			map = (IsometricMap) new TiledMapLoader().loadMap(loadTiledData(tiledMap), true);		
			map.initTiledMap(tiledMap, state);
		} 
		catch (Exception e) {
			Cons.println("Unable to load map: " + e);
			throw e;
		}
		
		int numberOfTilesX = map.getTileWorldWidth();
		int numberOfTilesY = map.getTileWorldWidth();
		
		int regionWidth = map.getTileWidth();
		int regionHeight = map.getTileHeight();
		
		for(int y = 0; y < numberOfTilesY; y++) {
			for(int x = 0; x < numberOfTilesX; x++ ) {
				MapTile tile = map.getTile(0, x, y);
				if(tile!=null) {
					tile.setSize(regionWidth, regionHeight);
					tile.setPosition(x*regionWidth, y*regionHeight);
					tile.setIndexPosition(x, y);
					if(terrainData!=null) {
						tile.setTerrainTileData(terrainData.getTileTerrainData(x, y));
					}
				}
			}
		}
		
		return map;
	}

}

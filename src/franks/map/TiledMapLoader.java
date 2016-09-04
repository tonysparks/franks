/*
 *	leola-live 
 *  see license.txt
 */
package franks.map;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import franks.gfx.TextureUtil;
import franks.map.Map.SceneDef;
import franks.map.MapTile.SurfaceType;

/**
 * @author Tony
 *
 */
public class TiledMapLoader implements MapLoader {


	
	private static final int FLIPPED_HORIZONTALLY_FLAG = 0x80000000;
	private static final int FLIPPED_VERTICALLY_FLAG   = 0x40000000;
	private static final int FLIPPED_DIAGONALLY_FLAG   = 0x20000000;
	
	/**
	 * Loads an {@link OrthoMap} created from the "Tiled" program.
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public Map loadMap(TiledData map, boolean loadAssets) throws Exception {
		SceneDef def = new SceneDef();
		
		int width = map.width;
		int height = map.height;
		
		int tileWidth = map.tilewidth;
		int tileHeight = map.tileheight;
		
		def.setDimensionX(width);
		def.setDimensionY(height);
		
		def.setTileWidth(tileWidth);
		def.setTileHeight(tileHeight);
		
		TilesetAtlas atlas = null;
		SurfaceType[][] surfaces = new SurfaceType[height][width];
		def.setSurfaces(surfaces);
		
		List<TiledTilesetData> tilesets = map.tilesets;
		atlas = parseTilesets(tilesets, loadAssets);
		
		
		List<TiledLayer> layers = map.layers;
		List<Layer> mapLayers = parseLayers(layers, atlas, loadAssets, tileWidth, tileHeight, surfaces);
		
		List<Layer> backgroundLayers = new ArrayList<Layer>();
		List<Layer> foregroundLayers = new ArrayList<Layer>();
		
		for(Layer layer : mapLayers) {
			if(layer.isForeground()) {
				foregroundLayers.add(layer);
			}
			else {
				backgroundLayers.add(layer);
			}
		}
		
		def.setBackgroundLayers(backgroundLayers.toArray(new Layer[backgroundLayers.size()]));
		def.setForegroundLayers(foregroundLayers.toArray(new Layer[foregroundLayers.size()]));
		
		String orientation = map.orientation;
		
		Map theMap = orientation.equals("isometric") ? new IsometricMap(loadAssets) : new OrthoMap(loadAssets);
		theMap.init(def);
		
		return theMap;
	}
	
	/**
	 * Parses the {@link SurfaceType}s layer
	 * @param surfaces
	 * @param data
	 * @param width
	 * @param tileWidth
	 * @param tileHeight
	 */
	private void parseSurfaces(SurfaceType[][] surfaces, TilesetAtlas atlas, int[] data, int width, int tileWidth, int tileHeight) {
		int y = -1; // account for zero
		for(int x = 0; x < data.length; x++) {
						
			if(x % width == 0) {									
				y++;
			}
			
			int tileId = data[x];
			int surfaceId = atlas.getTileId(tileId)-1; /* minus one to get back to zero based */
			surfaces[y][x % width] = SurfaceType.fromId(surfaceId);
		}
	}		
	
	private List<Layer> parseLayers(List<TiledLayer> layers,TilesetAtlas atlas, boolean loadImages, int tileWidth, int tileHeight, SurfaceType[][] surfaces) throws Exception {
		
		List<Layer> mapLayers = new ArrayList<Layer>(layers.size());
		
		int index = 0;
		for(TiledLayer layer : layers) {			
			
			
			int[] data = layer.data;
			int width = layer.width;	
			int height = layer.height;
			
			boolean isCollidable = false;
			boolean isForeground = false;
			boolean isProperty = false;
			boolean isSurfaceTypes = false;
			boolean isDestructable = false;
			boolean isVisible = layer.visible;
			
			int heightMask = 0;
			if ( layer.properties != null) {
				java.util.Map<String, JsonElement> properties = layer.properties;			
				isCollidable = properties.getOrDefault("collidable", new JsonPrimitive("false")).getAsString().equals("true");
				isForeground = properties.getOrDefault("foreground", new JsonPrimitive("false")).getAsString().equals("true");
				isDestructable = properties.getOrDefault("destructable", new JsonPrimitive("false")).getAsString().equals("true");
				
				if(properties.containsKey("heightMask")) {
					String strMask = properties.get("heightMask").getAsString();
					heightMask = Integer.parseInt(strMask);
				}
				
				if(properties.containsKey("lights")) {
					isProperty = true;
				}
				
				if(properties.containsKey("surfaces")) {
					isSurfaceTypes = true;
				}
								
			}
			
			if(isSurfaceTypes) {
				parseSurfaces(surfaces, atlas, data, width, tileWidth, tileHeight);
				continue;
			}
			
			Layer mapLayer = new Layer(layer.name,
									   isCollidable, 
					                   isForeground, 
					                   isDestructable, 
					                   isProperty, 
					                   isVisible,
					                   index, 
					                   heightMask,
					                   height);
			mapLayers.add(mapLayer);
						
			ImageTile[] row = null; //new Tile[width];
			
			int y = -tileHeight; // account for zero
			int rowIndex = 0;
			for(int x = 0; x < data.length; x++) {
				int tileId = data[x];
				boolean flippedHorizontally = (tileId & FLIPPED_HORIZONTALLY_FLAG) != 0;
				boolean flippedVertically = (tileId & FLIPPED_VERTICALLY_FLAG) != 0; 
				boolean flippedDiagonally = (tileId & FLIPPED_DIAGONALLY_FLAG) != 0;
				
				tileId &= ~(FLIPPED_HORIZONTALLY_FLAG |
                            FLIPPED_VERTICALLY_FLAG |
                            FLIPPED_DIAGONALLY_FLAG);
				
				if(x % width == 0) {						
					row = new ImageTile[width];
					mapLayer.addRow(rowIndex++, row);
					y+=tileHeight;
				}
				
				if(loadImages) {
					TextureRegion image = atlas.getTile(tileId);
					if(image != null) {
						ImageTile tile = null;
						if( atlas.isAnimatedTile(tileId) ) {
							tile = new AnimatedTile(atlas.getAnimatedTile(tileId), index, tileWidth, tileHeight); 
							mapLayer.setContainsAnimations(true);
						}
						else {
							tile = new ImageTile(image, index, tileWidth, tileHeight);
						}
												
						tile.setPosition( (x%width) * tileWidth, y);
//						tile.setSurfaceType(atlas.getTileSurfaceType(tileId));
						tile.setFlips(flippedHorizontally, flippedVertically, flippedDiagonally);
						
						if(isCollidable) {
							int collisionId = atlas.getTileId(tileId);
							tile.setCollisionMaskById(collisionId);
						}
						row[x%width] = tile;
					}
					else {
						row[x%width] = null;
					}
				}
				// if we are headless...
				else {
					if(tileId != 0) {
						ImageTile tile = new ImageTile(null, index, tileWidth,tileHeight);
						tile.setPosition( (x%width) * tileWidth, y);
//						tile.setSurfaceType(atlas.getTileSurfaceType(tileId));
						
						if(isCollidable) {
							int collisionId = atlas.getTileId(tileId);
							tile.setCollisionMaskById(collisionId);
						}
						row[x%width] = tile;
					}
					else {
						row[x%width] = null;
					}
				}
			}
			
			
			if(!isForeground) {
				index++;
			}	
			
			mapLayer.applyHeightMask();
		}
		
		return mapLayers;
	}
	
	private TilesetAtlas parseTilesets(List<TiledTilesetData> tilesets, boolean loadImages) throws Exception {
		if(tilesets ==null || tilesets.isEmpty()) {
			throw new IllegalArgumentException("There must be at least 1 tileset");
		}
		
		TilesetAtlas atlas = new  TilesetAtlas();
		for(TiledTilesetData tileset : tilesets) {									
			int firstgid = tileset.firstgid;			
			int margin = tileset.margin;
			int spacing = tileset.spacing;
			int tilewidth = tileset.tilewidth;
			int tileheight = tileset.tileheight;
			
			java.util.Map<String, JsonElement> tilesetprops = tileset.tileproperties;
			

			TextureRegion image = null;
			TextureRegion[] images = null;
												
			if(loadImages) {
				final String imagePath = tileset.image;
				image = TextureUtil.loadImage(imagePath);
				image.flip(false, true);
				images = TextureUtil.toTileSet(image, tilewidth, tileheight, margin, spacing);			
			}
			atlas.addTileset(new Tileset(firstgid, images, tilesetprops));
		}
		
		return atlas;
	}
}

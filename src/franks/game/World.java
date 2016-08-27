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
import franks.map.Map.SceneDef;
import franks.map.MapGraph;
import franks.map.MapTile;
import franks.math.Vector2f;
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
	
	
	private Region[][] regions;
	private IsometricMap map;
	private Cursor cursor;
	private Camera camera;
	
	private Cell[][] cells;
	
	private MapGraph<Void> graph;
	
	private TextureRegion tileImg;
	private Vector2f cacheVector;
	/**
	 * 
	 */
	public World(Game game) {
		this.camera = game.getCamera();
		this.cursor = game.getCursor();
				
		this.cacheVector = new Vector2f();
		this.regions = new Region[12][12];
		for(int y = 0; y < this.regions.length; y++) {
			for(int x = 0; x < this.regions[y].length; x++) {
				this.regions[y][x] = new Region(x,y, TileWidth, TileHeight);
			}
		}
		
		int tileWidth = TileWidth*2;
		int tileHeight = TileHeight;
		
		SceneDef scene = new SceneDef();
		//scene.setTileWidth(tileWidth);
		//scene.setTileHeight(tileHeight);
		scene.setTileWidth(TileWidth*2);
		scene.setTileHeight(TileHeight);
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
		
		
		
		
		int numberOfXCells = scene.getDimensionX() / CellTileWidth;
		int numberOfYCells = scene.getDimensionY() / CellTileHeight;
		this.cells = new Cell[numberOfYCells][];
		
		for(int y = 0; y < numberOfYCells; y++) {
			this.cells[y] = new Cell[numberOfXCells];
			for(int x = 0; x < numberOfXCells; x++) {		
				this.cells[y][x] = new Cell(game, x*CellTileWidth, y*CellTileHeight, CellTileWidth, CellTileHeight); 				
			}
		}
		
		int cellY = 0;
		int cellX = 0;
		
		for(int y = 0; y < this.regions.length; y++) {
			MapTile[] row = new MapTile[this.regions[0].length];
			for(int x = 0; x < this.regions[0].length; x++ ) {
				//MapTile tile = new ColoredTile(0xff81aa81, 0xff000000, 0, RegionWidth, RegionHeight);
				//tile.setPosition(x*RegionWidth, y*RegionHeight);
				MapTile tile = new ImageTile(texTile, 0, TileWidth, TileHeight); //tileWidth, tileHeight);
				//tile.setPosition(x*tileWidth, y*tileHeight);
				tile.setPosition(x*TileWidth, y*TileHeight);
				tile.setIndexPosition(x, y);
				
				tile.setCell(this.cells[cellY][cellX]);
				row[x] = tile;
				
				
				if(x>0 && x%CellTileWidth==0) {
					cellX++;
				}				
			}
			
			if(y>0 && y%CellTileHeight==0) {
				cellY++;
			}
			cellX = 0;
			
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
	
	public Cell getCell(Vector2f worldPos) {
		int x = (int) worldPos.x / World.TileWidth;
		int y = (int) worldPos.y / World.TileHeight;
		
		if(map.checkTileBounds(x, y)) {
		
			MapTile tile = map.getTile(0, x, y);
			if(tile!=null) {
				return tile.getCell();
			}
		}
		
		return null;
	}
	
	public MapTile getMapTileByScreenPos(Vector2f pos) {
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
		return screenToWorldCoordinates(x, y, cacheVector);
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
	
//	public Vector2f worldToScreen(float worldX, float worldY, Vector2f out) {
//		Vector2f pos = camera.getPosition();
//		out.set(worldX - pos.x, worldY - pos.y);
//		return out;
//	}
	
//	public Vector2f worldToIso(float worldX, float worldY, Vector2f out) {
//		//worldToScreen(worldX, worldY, out);
//		this.map.screenToIsoPosition(worldX, worldY, out);
//		//this.map.worldToIsoIndex(worldX, worldY, out);
//		//Vector2f.Vector2fSubtract(out, camera.getPosition(), out);
//		return out;
//	}
	
//	public Vector2f isoIndexToWorld(int isoX, int isoY, Vector2f out) {
//		this.map.isoIndexToWorld(isoX, isoY, out); 
//		return out;
//	}
	
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
		
		for(int y = 0; y < cells.length; y++) {			
			for(int x = 0; x < cells[y].length; x++) {		
				cells[y][x].render(canvas, camera, alpha); 				
			}
		}
		

		
		//canvas.drawImage(tileImg, 10, canvas.getHeight() - 100, null);
		//canvas.drawCircle(2, 10, canvas.getHeight() - 100, 0xffffffff);
		//canvas.drawRect(10, canvas.getHeight()-100, tileImg.getRegionWidth(), tileImg.getRegionHeight(), 0xffffffff);
		//canvas.drawCircle(2, cursor.getCenterPos().x, cursor.getCenterPos().y, 0xffffffff);
//		for(int y = 0; y < this.regions.length; y++) {
//			for(int x = 0; x < this.regions[y].length; x++) {
//				this.regions[y][x].render(canvas, camera, alpha);
//			}
//		}
	}
}

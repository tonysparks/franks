/*
 * see license.txt 
 */
package franks.game;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.hjson.JsonValue;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import franks.FranksGame;
import franks.game.CommandAction.CompletionState;
import franks.game.CommandQueue.CommandRequest;
import franks.game.entity.Entity;
import franks.game.entity.Entity.Direction;
import franks.game.entity.EntityData;
import franks.gfx.Art;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.gfx.Cursor;
import franks.gfx.RenderFont;
import franks.gfx.Renderable;
import franks.gfx.Terminal;
import franks.map.IsometricMap;
import franks.map.MapTile;
import franks.math.Rectangle;
import franks.math.Vector2f;
import franks.util.AssetWatcher;
import franks.util.Command;
import franks.util.Cons;
import franks.util.Console;
import franks.util.FileSystemAssetWatcher;
import franks.util.PassThruAssetWatcher;
import franks.util.TimeStep;
import leola.vm.types.LeoMap;
import leola.vm.types.LeoObject;

/**
 * @author Tony
 *
 */
public class Game implements Renderable {

	private FranksGame app;
	private World world;
	
//	private MovementMeter moveMeter;
	private Resources resources;
	
	private List<Entity> entities;
	private List<Entity> aliveEntities;
	
	private List<Entity> selectedEntities;
	
	private Optional<Entity> selectedEntity;
	private Optional<Entity> hoveredOverEntity;
	
	private List<CommandAction> actions, inprogressActions;
	
	private Vector2f mouseWorldPos;
	private Cursor cursor;
	
	private Terminal terminal;
	
	private Randomizer randomizer;
	private Turn currentTurn;
	
	private HealthMeter healthMeter;
	private CommandQueue commandQueue;
	
	private Camera camera;
	private CameraController cameraController;
	
	private ResourceCache textureCache;
	private Gson gson;
	
	private AssetWatcher watcher;
	
	private Team redTeam, greenTeam;
	
	private Comparator<Entity> renderOrder = new Comparator<Entity>() {
		
		@Override
		public int compare(Entity left, Entity right) {
			return left.getZOrder() - right.getZOrder();
		}
	};
	
	/**
	 * 
	 */
	public Game(FranksGame app, Camera camera) {
		this.camera = camera;
		this.terminal = app.getTerminal();
		this.randomizer = new Randomizer();
		this.cursor = app.getUiManager().getCursor();
		this.world = new World(this); 
		
		this.healthMeter = new HealthMeter();
		this.resources = new Resources();
		
		this.textureCache = new ResourceCache();
		
		this.entities = new ArrayList<>();
		this.aliveEntities = new ArrayList<>();
		this.selectedEntities = new ArrayList<>();
		
		this.selectedEntity = Optional.empty();
		this.hoveredOverEntity = Optional.empty();
		
		this.actions = new ArrayList<>();
		this.inprogressActions = new ArrayList<>();
		
		this.mouseWorldPos = new Vector2f();

		this.currentTurn = new Turn(0);
		
		this.commandQueue = new CommandQueue(this);
		
		this.cameraController = new CameraController(world.getMap(), camera);
	
		this.redTeam = new Team("Red");
		this.greenTeam = new Team("Green");
		
		this.gson = new GsonBuilder().create();
		
		try {
			this.watcher = new FileSystemAssetWatcher(new File("/assets"));
			this.watcher.startWatching();
		}
		catch(IOException e) {
			Cons.println("*** Unable to start asset watcher - " + e);
			this.watcher = new PassThruAssetWatcher();
		}
		
		// temp


		
		//newBuilding(8, 3, 2, 2);
		
		app.getConsole().addCommand(new Command("reload") {
			
			@Override
			public void execute(Console console, String... args) {
				entities.clear();
				
				EntityData dwarf = loadEntity("assets/entities/dark_dwarf.json");
				
				EntityData greenArcher = loadEntity("assets/entities/green_archer.json");
				EntityData greenKnight = loadEntity("assets/entities/green_knight.json");
				
				for(int i = 0; i < 8; i++) {					
					Entity dataEnt = new Entity(Game.this, greenTeam, i%2==0 ? greenArcher : greenKnight);
					dataEnt.moveToRegion(0, i);
					dataEnt.setCurrentDirection(Direction.SOUTH_EAST);
					dataEnt.setDesiredDirection(Direction.SOUTH_EAST);
					addEntity(dataEnt);
				}								
				
				EntityData redArcher = loadEntity("assets/entities/red_archer.json");
				EntityData redKnight = loadEntity("assets/entities/red_knight.json");
				
				for(int i = 0; i < 8; i++) {
					Entity dataEnt = new Entity(Game.this, redTeam, i%2==0 ? redArcher: redKnight);
					dataEnt.moveToRegion(11, i);
					dataEnt.setCurrentDirection(Direction.NORTH_WEST);
					dataEnt.setDesiredDirection(Direction.NORTH_WEST);
					addEntity(dataEnt);
				}
				

				Entity dwarfEnt = new Entity(Game.this, greenTeam, dwarf);
				dwarfEnt.moveToRegion(0, 9);
				dwarfEnt.setCurrentDirection(Direction.SOUTH_EAST);
				dwarfEnt.setDesiredDirection(Direction.SOUTH_EAST);
				addEntity(dwarfEnt);
				
				
				dwarfEnt = new Entity(Game.this, redTeam, dwarf);
				dwarfEnt.moveToRegion(11, 9);
				dwarfEnt.setCurrentDirection(Direction.NORTH_WEST);
				dwarfEnt.setDesiredDirection(Direction.NORTH_WEST);
				addEntity(dwarfEnt);
				
//				EntityData knight = loadEntity("assets/entities/green_knight.json");
//				Entity dataKnight= new Entity(Game.this, redTeam, knight);
//				dataKnight.moveToRegion(0, 0);
//				dataKnight.setCurrentDirection(Direction.SOUTH_EAST);
//				addEntity(dataKnight);
//				
//				dataKnight= new Entity(Game.this, redTeam, knight);
//				dataKnight.moveToRegion(5, 0);
//				dataKnight.setCurrentDirection(Direction.SOUTH_EAST);
//				addEntity(dataKnight);
//				
//				dataKnight= new Entity(Game.this, redTeam, knight);
//				dataKnight.moveToRegion(10, 0);
//				dataKnight.setCurrentDirection(Direction.SOUTH_EAST);
//				addEntity(dataKnight);
//				
//				dataKnight= new Entity(Game.this, redTeam, knight);
//				dataKnight.moveToRegion(0, 5);
//				dataKnight.setCurrentDirection(Direction.SOUTH_EAST);
//				addEntity(dataKnight);
//				
//				dataKnight= new Entity(Game.this, redTeam, knight);
//				dataKnight.moveToRegion(0, 10);
//				dataKnight.setCurrentDirection(Direction.SOUTH_EAST);
//				addEntity(dataKnight);
//				
//				
//				dataKnight= new Entity(Game.this, redTeam, knight);
//				dataKnight.moveToRegion(5, 5);
//				dataKnight.setCurrentDirection(Direction.SOUTH_EAST);
//				addEntity(dataKnight);
//				
//				dataKnight= new Entity(Game.this, redTeam, knight);
//				dataKnight.moveToRegion(10, 10);
//				dataKnight.setCurrentDirection(Direction.SOUTH_EAST);
//				addEntity(dataKnight);
				

			}
		});
		app.getConsole().execute("reload");
	}
	
	public EntityData loadEntity(String file) {
		try {
			return gson.fromJson(JsonValue.readHjson(Gdx.files.internal(file).reader(1024)).toString(), EntityData.class);
		}
		catch(IOException e) {
			Cons.println("*** Unable to load '" + file + "' : " + e);
		}
		
		return null;
	}
	
	/**
	 * @return the app
	 */
	public FranksGame getApp() {
		return app;
	}
	
	/**
	 * @return the camera
	 */
	public Camera getCamera() {
		return camera;
	}
	
	/**
	 * @return the cursor
	 */
	public Cursor getCursor() {
		return cursor;
	}
	
	/**
	 * @return the textureCache
	 */
	public ResourceCache getTextureCache() {
		return textureCache;
	}
	
	
	public Game addEntity(Entity ent) {
		this.entities.add(ent);
		this.entities.sort(renderOrder);
		return this;
	}
	
	public void foreachEntity(Consumer<Entity> c) {
		this.entities.forEach(c);
	}
	
	public Optional<Entity> getEntityOnTile(MapTile tile) {
		Optional<Entity> entity = Optional.empty();
		for(Entity ent : entities) {
			if(ent.getBounds().intersects(tile.getBounds())) {
				entity = Optional.of(ent);
				break;
			}
		}
		return entity;
	}
	
	/**
	 * @return the currentTurn
	 */
	public Turn getCurrentTurn() {
		return currentTurn;
	}
	
	public Turn nextTurn() {
		this.currentTurn.endTurn(this);
				
		Turn next = new Turn(this.currentTurn.getNumber()+1);
		this.healthMeter.calculate(this);
		this.currentTurn = next;
		return next;
	}
	
	/**
	 * @return the selectedEntity
	 */
	public Optional<Entity> getSelectedEntity() {
		return selectedEntity;
	}
	
	public boolean hasSelectedEntities() {
		return !this.selectedEntities.isEmpty();
	}
	
	public boolean isSelected(Entity entity) {
		return this.selectedEntities.contains(entity);
	}
	
	/**
	 * @return the selectedEntities
	 */
	public List<Entity> getSelectedEntities() {
		return selectedEntities;
	}
	
	public void deselectEntities() {
		for(int i = 0; i < this.selectedEntities.size();i++) {
			this.selectedEntities.get(i).isSelected(false);
		}
		this.selectedEntities.clear();
	}
	
	public boolean selectEntity() {
		deselectEntities();
		
		Entity selectedEntity = getEntityOverMouse();
		if(selectedEntity != null) {
			selectedEntity.isSelected(true);
			this.selectedEntities.add(selectedEntity);
			return true;
		}
		return false;
	}
	
	public boolean addSelectEntity() {		
		Entity selectedEntity = getEntityOverMouse();
		if(selectedEntity != null) {
			if(selectedEntity.isSelected()) {
				selectedEntity.isSelected(false);
				selectedEntities.remove(selectedEntity);
			}
			else {
				selectedEntity.isSelected(true);
				selectedEntities.add(selectedEntity);
				return true;
			}
		}
		return false;
	}
	
	public boolean selectRegion(Vector2f startPos, Vector2f endPos) {
		Vector2f worldStart = world.screenToWorldCoordinates(startPos);
		Vector2f worldEnd = world.screenToWorldCoordinates(endPos);
		
		IsometricMap map = world.getMap();
		map.screenToIsoIndex(worldStart, worldStart);
		map.screenToIsoIndex(worldEnd, worldEnd);
		
		// convert to world
		worldStart.x *= World.TileWidth;
		worldStart.y *= World.TileHeight;
		
		worldEnd.x *= World.TileWidth;
		worldEnd.y *= World.TileHeight;
		
		float deltaX = worldEnd.x - worldStart.x;
		float deltaY = worldEnd.y - worldStart.y;
		int width = (int)Math.abs(deltaX);
		int height = (int)Math.abs(deltaY);
		Rectangle region = new Rectangle(width, height);
		if(deltaX < 0f) {
			worldStart.x = worldEnd.x;
		}
		if(deltaY < 0f) {
			worldStart.y = worldEnd.y;
		}
		
		region.setLocation(worldStart);
		System.out.println(region);

		this.selectedEntities.clear();
		for(Entity ent : this.entities) {
			if(ent.isAlive()) {
				System.out.print("checking: " + ent.getBounds() + ":" + ent.getTilePos() + " vs." + (int)worldStart.x+","+(int)worldStart.y + " intersects: ");
				if(ent.getBounds().intersects(region)) {
					this.selectedEntities.add(ent);
					System.out.println("true");
				}
				else System.out.println("false");
			}
		}
		
		return !this.selectedEntities.isEmpty();
	}
	
	public boolean hoveringOverEntity() {
		hoveredOverEntity = Optional.ofNullable(getEntityOverMouse());		
		return hoveredOverEntity.isPresent();
	}
	
	public Entity getEntityOverMouse() {				
		Vector2f worldPos = getMouseWorldPos();
		MapTile tile = world.getMapTileByScreenPos(worldPos);
		if(tile!=null) {
			Rectangle bounds = tile.getBounds();
			for(Entity ent : this.entities) {
				if(bounds.intersects(ent.getBounds()) ||
				   bounds.contains(ent.getCenterPos())) {
					return ent;
				}
			}
		}
		
		return null;
	}
	
	public Cell getCellOverMouse() {				
		Vector2f worldPos = getMouseWorldPos();
		MapTile tile = world.getMapTileByScreenPos(worldPos);
		if(tile!=null) {
			return tile.getCell();
		}
		
		return null;
	}
	
	public MapTile getTileOverMouse() {
		Vector2f worldPos = getMouseWorldPos();
		MapTile tile = world.getMapTileByScreenPos(worldPos);
		return tile;
	}
	
	/**
	 * @return the entities
	 */
	public List<Entity> getEntities() {
		return entities;
	}
	
	/**
	 * @return the randomizer
	 */
	public Randomizer getRandomizer() {
		return randomizer;
	}
	
	public Vector2f getMouseWorldPos() {
		return world.screenToWorldCoordinates(cursor.getCursorPos(), mouseWorldPos);		
	}
			
	private void dispatchCommands(String action) {
		for(int i = 0; i < this.selectedEntities.size();i++) {
			Entity entity = this.selectedEntities.get(i);
			entity.queueAction(new CommandRequest(this, action, entity));
		}
	}
	
	public void queueCommand() {
		
		if(!hasSelectedEntities()) {
			return;
		}
		
		Entity target = getEntityOverMouse();
		if(target!=null && !isSelected(target)) {
						
			// TODO make generic!!
			switch(target.getType()) {				
				case HUMAN:					
					dispatchCommands("attack");
					break;
				default: // throw new IllegalArgumentException("Unsupported type: " + resource.getType());
					Cons.println("Unsupported type: " + target.getType());
			}
			
		}
		else {
			//this.commandQueue.add(makeRequest("moveTo"));						
			//this.commandQueue.add(makeRequest("die"));
			dispatchCommands("moveTo");
		}
	}
	
	// TODO create a menu over target entity, to display dispatch
	// command to do
//	public void dispatchCommand() {
//		Entity resource = getEntityOverMouse();
//		if(resource!=null) {
//			dispatchCommand("collectWood");
//		}
//		else {
//			dispatchCommand("moveTo");
//		}
//	}
	
//	public void dispatchCommand(final String cmdName) {
//		final CommandRequest request = makeRequest(cmdName);
//		executeCommandRequest(request);
//	}
		
	/**
	 * @return the resources
	 */
	public Resources getResources() {
		return resources;
	}
	
	/**
	 * @return the world
	 */
	public World getWorld() {
		return world;
	}
	
	public IsometricMap getMap() {
		return world.getMap();
	}

	/* (non-Javadoc)
	 * @see newera.gfx.Renderable#update(newera.util.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {		
		Vector2f mousePos = cursor.getCursorPos();
		if(!terminal.isActive()) {
			this.cameraController.applyPlayerMouseInput(mousePos.x, mousePos.y);
			this.cameraController.update(timeStep);
		}
		this.world.update(timeStep);
		
		this.aliveEntities.clear();
		this.entities.forEach(ent -> {
			ent.update(timeStep);
			if(!ent.isDeleted()) {
				aliveEntities.add(ent);
			}
		});
		
		this.entities.clear();
		this.entities.addAll(aliveEntities);
		
		
		this.commandQueue.update(timeStep);
		
		this.inprogressActions.clear();
		this.actions.forEach(action -> {
			if(action.getCurrentState() == CompletionState.InProgress) {
				action.update(timeStep);
				inprogressActions.add(action);
			}
		});
		
		this.actions.clear();
		this.actions.addAll(inprogressActions);
		
		this.camera.update(timeStep);
	}
	
	private void renderEntityAttributes(Canvas canvas, Entity entity, float x, float y, int textColor) {
		LeoMap attributes = entity.getAttributes();
		if(!attributes.isEmpty()) {
			//RenderFont.drawShadedString(canvas, "Attributes: ", x, y, textColor);
			
			for(int i = 0; i < attributes.bucketLength(); i++) {
				LeoObject key = attributes.getKey(i);
				if(key!=null) {
					LeoObject value = attributes.getValue(i);
					RenderFont.drawShadedString(canvas, key + ": " + value, x, y, textColor);
					y+=15;
				}
			}
		}
				
		float healthPer = ((float)entity.getHealth() / (float)entity.getMaxHealth()) * 100f;
		RenderFont.drawShadedString(canvas, "Health: " + (int) healthPer + "%" , x, y, textColor);
		RenderFont.drawShadedString(canvas, "Moves: " + entity.getMeter().getMovementAmount(), x, y+15, textColor);
	}
	
	/* (non-Javadoc)
	 * @see newera.gfx.Renderable#render(newera.gfx.Canvas, newera.gfx.Camera, float)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {
		this.world.render(canvas, camera, alpha);
		this.actions.forEach(action -> action.render(canvas, camera, alpha));
		
		drawMouseHover(canvas, camera, alpha);
		
		Vector2f c = camera.getRenderPosition(alpha);
		IsometricMap map = world.getMap();
		this.selectedEntity.ifPresent(ent -> {

			Vector2f tilePos = ent.getTilePos();
			MapTile tile = map.getTile(0, (int)tilePos.x, (int)tilePos.y);
			if(tile!=null) {
				map.renderIsoRect(canvas, tile.getIsoX()-c.x, tile.getIsoY()-c.y, tile.getWidth(), tile.getHeight(), 0xab34baff);
			}
		});
		
		for(Entity ent: this.selectedEntities) {
			Vector2f tilePos = ent.getTilePos();
			MapTile tile = map.getTile(0, (int)tilePos.x, (int)tilePos.y);
			if(tile!=null) {
				map.renderIsoRect(canvas, tile.getIsoX()-c.x, tile.getIsoY()-c.y, tile.getWidth(), tile.getHeight(), 0xab34baff);
			}
		}

		this.entities.sort(renderOrder);
		this.entities.forEach(ent -> ent.render(canvas, camera, alpha));
		//this.entities.forEach(ent -> RenderFont.drawShadedString(canvas, "" + ent.getZOrder(), ent.getRenderPosition(camera, alpha).x+42, ent.getRenderPosition(camera, alpha).y+64, 0xffffffff));
		
		int textColor = 0xff00ff00;
		canvas.setFont("Consola", 12);
		
		this.resources.render(canvas, camera, alpha);
		
		float y = 600;
		float x = 10;
		//RenderFont.drawShadedString(canvas, "Movements: " + moveMeter.getMovementAmount(), x, y, textColor);
		RenderFont.drawShadedString(canvas, "Health: " + healthMeter.getHealth(), x, y+=15f, textColor);
		RenderFont.drawShadedString(canvas, "Happiness: " + healthMeter.getHappiness(), x, y+=15f, textColor);
		RenderFont.drawShadedString(canvas, "Actions: " + actions.size(), x, y+=15f, textColor);
		
		
		this.selectedEntity.ifPresent(ent -> {
			float sx = 20;
			float sy = 20;
			RenderFont.drawShadedString(canvas, "Selected: " + ent.getName(), sx, sy, textColor);
			renderEntityAttributes(canvas, ent, sx, sy + 15, textColor);
			
			
		});
		
		for(Entity ent: this.selectedEntities) {
			float sx = 20;
			float sy = 20;
			RenderFont.drawShadedString(canvas, "Selected: " + ent.getName(), sx, sy, textColor);
			renderEntityAttributes(canvas, ent, sx, sy + 15, textColor);
		}
		
		this.hoveredOverEntity.ifPresent(ent -> {
			Vector2f pos = cursor.getCursorPos();
			RenderFont.drawShadedString(canvas, "" + ent.getName(), pos.x + 30, pos.y, textColor);
			renderEntityAttributes(canvas, ent, pos.x + 30, pos.y + 15, textColor);
		});
		
		
	}
	
	private void drawMouseHover(Canvas canvas, Camera camera, float alpha) {
		Vector2f pos = cursor.getCenterPos();
		pos = world.screenToWorldCoordinates(pos.x, pos.y);
		
		IsometricMap map = world.getMap();
		MapTile tile = map.getWorldTile(0, pos.x, pos.y);
		
		//this.cells.forEach(cell -> cell.render(canvas, camera, alpha));
		//this.cells.get(0).render(canvas, camera, alpha);
		
		//canvas.drawString( "Screen: " + (int)cursor.getCenterPos().x+","+ (int)cursor.getCenterPos().y, cursor.getX()-50, cursor.getY()+40, 0xffffffff);
		//canvas.drawString( "World:  " + (int)pos.x+","+ (int)pos.y, cursor.getX()-50, cursor.getY()+60, 0xffffffff);
		
		if(tile != null) {
			//canvas.drawString( "IsoPos:  " + (int)tile.getIsoX()+","+ (int)tile.getIsoY(), cursor.getX()-50, cursor.getY()+80, 0xffffffff);
			//canvas.drawString( "TileIndex:  " + (int)tile.getXIndex()+","+ (int)tile.getYIndex(), cursor.getX()-50, cursor.getY()+100, 0xffffffff);
			//canvas.drawString( "TilePos:  " + (int)tile.getX()+","+ (int)tile.getY(), cursor.getX()-50, cursor.getY()+120, 0xffffffff);
			
			//canvas.drawRect(tile.getRenderX(), tile.getRenderY(), tile.getWidth(), tile.getHeight(), 0xffffffff);
			Vector2f c = camera.getRenderPosition(alpha);
			
			int color = 0x5fffffff;
			
			cursor.setCursorImg(Art.normalCursorImg);
			
			if(selectedEntities.size() == 1) {
				Entity ent = selectedEntities.get(0);
				int moves = ent.getMeter().getMovementAmount();				
				
				
				
				int distance = ent.distanceFrom(tile);
				if(distance <= moves && moves > 0) {
					color = 0x5f0000ff;
					
					Entity enemy = getEntityOverMouse();
					if(enemy!=null && !enemy.getTeam().equals(ent.getTeam())) {
						if( (moves - ((distance-1) + ent.attackCost())) > 0 ) {
							cursor.setCursorImg(Art.attackCursorImg);
						}
					}
					
				}
				else {
					color = 0x5fff0000;
				}
				
			}
			
			map.renderIsoRect(canvas, tile.getIsoX()-c.x, tile.getIsoY()-c.y, tile.getWidth(), tile.getHeight(), color);
			//canvas.drawString( tile.getRenderX()+","+tile.getRenderY(), 10, 70, 0xffffffff);
		}
	}
}

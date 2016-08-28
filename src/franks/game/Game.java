/*
 * see license.txt 
 */
package franks.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.gfx.Cursor;
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
import franks.util.PassThruAssetWatcher;
import franks.util.TimeStep;

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
	
//	private List<Entity> selectedEntities;
	
	private Entity selectedEntity;
	private Entity hoveredOverEntity;
	
	private List<CommandAction> actions, inprogressActions;
	
	private Vector2f mouseWorldPos;
	private Cursor cursor;
	
	private Terminal terminal;
	
	private Randomizer randomizer;
	private Turn currentTurn;
	
	private Hud hud;
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
//		this.selectedEntities = new ArrayList<>();
				
		this.actions = new ArrayList<>();
		this.inprogressActions = new ArrayList<>();
		
		this.mouseWorldPos = new Vector2f();

		this.currentTurn = new Turn(0);
		
		this.commandQueue = new CommandQueue(this);
		
		this.cameraController = new CameraController(world.getMap(), camera);
	
		this.redTeam = new Team("Red");
		this.greenTeam = new Team("Green");
		
		this.hud = new Hud(this);
		
		this.gson = new GsonBuilder().create();
		
		try {
			this.watcher = // new FileSystemAssetWatcher(new File("/assets"));
					new PassThruAssetWatcher();
			this.watcher.startWatching();
		}
		catch(Exception e) {
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
				
				final int rightSize = world.getMap().getTileWorldHeight()-1;
				
				for(int i = 0; i < 8; i++) {
					Entity dataEnt = new Entity(Game.this, redTeam, i%2==0 ? redArcher: redKnight);
					dataEnt.moveToRegion(rightSize, i);
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
				dwarfEnt.moveToRegion(rightSize, 9);
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
	
	public Entity getEntityOnTile(MapTile tile) {
		
		for(Entity ent : entities) {
			if(ent.getBounds().intersects(tile.getBounds())) {
				return ent;
			}
		}
		return null;
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
	public Entity getSelectedEntity() {
		return selectedEntity;
	}
	
//	public boolean hasSelectedEntities() {
//		return !this.selectedEntities.isEmpty();
//	}
	
//	public boolean isSelected(Entity entity) {
//		return this.selectedEntities.contains(entity);
//	}
	
	/**
	 * @return the selectedEntities
	 */
//	public List<Entity> getSelectedEntities() {
//		return selectedEntities;
//	}
	
//	public void deselectEntities() {
//		for(int i = 0; i < this.selectedEntities.size();i++) {
//			this.selectedEntities.get(i).isSelected(false);
//		}
//		this.selectedEntities.clear();
//	}
	
	public boolean selectEntity() {
//		deselectEntities();
//		
//		Entity selectedEntity = getEntityOverMouse();
//		if(selectedEntity != null) {
//			selectedEntity.isSelected(true);
//			this.selectedEntities.add(selectedEntity);
//			return true;
//		}
//		return false;
		
		if(this.selectedEntity != null) {
			this.selectedEntity.isSelected(false);
		}
		
		Entity newSelectedEntity = getEntityOverMouse();
		if(newSelectedEntity != null) {
			newSelectedEntity.isSelected(true);
			this.selectedEntity = newSelectedEntity;
			return true;
		}
		return false;
	}
	
//	public boolean addSelectEntity() {		
//		Entity selectedEntity = getEntityOverMouse();
//		if(selectedEntity != null) {
//			if(selectedEntity.isSelected()) {
//				selectedEntity.isSelected(false);
//				selectedEntities.remove(selectedEntity);
//			}
//			else {
//				selectedEntity.isSelected(true);
//				selectedEntities.add(selectedEntity);
//				return true;
//			}
//		}
//		return false;
//	}
	
//	public boolean selectRegion(Vector2f startPos, Vector2f endPos) {
//		Vector2f worldStart = world.screenToWorldCoordinates(startPos);
//		Vector2f worldEnd = world.screenToWorldCoordinates(endPos);
//		
//		IsometricMap map = world.getMap();
//		map.screenToIsoIndex(worldStart, worldStart);
//		map.screenToIsoIndex(worldEnd, worldEnd);
//		
//		// convert to world
//		worldStart.x *= World.TileWidth;
//		worldStart.y *= World.TileHeight;
//		
//		worldEnd.x *= World.TileWidth;
//		worldEnd.y *= World.TileHeight;
//		
//		float deltaX = worldEnd.x - worldStart.x;
//		float deltaY = worldEnd.y - worldStart.y;
//		int width = (int)Math.abs(deltaX);
//		int height = (int)Math.abs(deltaY);
//		Rectangle region = new Rectangle(width, height);
//		if(deltaX < 0f) {
//			worldStart.x = worldEnd.x;
//		}
//		if(deltaY < 0f) {
//			worldStart.y = worldEnd.y;
//		}
//		
//		region.setLocation(worldStart);
//		System.out.println(region);
//
//		this.selectedEntities.clear();
//		for(Entity ent : this.entities) {
//			if(ent.isAlive()) {
//				System.out.print("checking: " + ent.getBounds() + ":" + ent.getTilePos() + " vs." + (int)worldStart.x+","+(int)worldStart.y + " intersects: ");
//				if(ent.getBounds().intersects(region)) {
//					this.selectedEntities.add(ent);
//					System.out.println("true");
//				}
//				else System.out.println("false");
//			}
//		}
//		
//		return !this.selectedEntities.isEmpty();
//	}
	
	public boolean hoveringOverEntity() {
		hoveredOverEntity = getEntityOverMouse();		
		return hoveredOverEntity!=null;
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
			
//	private void dispatchCommands(String action) {
//		for(int i = 0; i < this.selectedEntities.size();i++) {
//			Entity entity = this.selectedEntities.get(i);
//			entity.queueAction(new CommandRequest(this, action, entity));
//		}
//	}
	
	private void dispatchCommand(Entity entity, String action) {				
		entity.queueAction(new CommandRequest(this, action, entity));		
	}
	
	public void queueCommand() {
		
//		if(!hasSelectedEntities()) {
//			return;
//		}
//		
//		Entity target = getEntityOverMouse();
//		if(target!=null && !isSelected(target)) {
//						
//			// TODO make generic!!
//			switch(target.getType()) {				
//				case HUMAN:					
//					dispatchCommands("attack");
//					break;
//				default: 
//					Cons.println("Unsupported type: " + target.getType());
//			}
//			
//		}
//		else {
//			dispatchCommands("moveTo");
//		}
		
		if(this.selectedEntity==null) {
			return;
		}
		
		Entity target = getEntityOverMouse();
		if(target!=null && this.selectedEntity!=target) {
			switch(target.getType()) {				
				case HUMAN:					
					dispatchCommand(selectedEntity, "attack");
					break;
				default: 
					Cons.println("Unsupported type: " + target.getType());
			}
		}
		else {
			dispatchCommand(selectedEntity, "moveTo");
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
		this.hud.update(timeStep);
	}
	

	
	/* (non-Javadoc)
	 * @see newera.gfx.Renderable#render(newera.gfx.Canvas, newera.gfx.Camera, float)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {
		this.world.render(canvas, camera, alpha);
		this.actions.forEach(action -> action.render(canvas, camera, alpha));
		
		this.hud.renderUnderEntities(canvas, camera, alpha);
		
		this.entities.sort(renderOrder);
		this.entities.forEach(ent -> ent.render(canvas, camera, alpha));
		//this.entities.forEach(ent -> RenderFont.drawShadedString(canvas, "" + ent.getZOrder(), ent.getRenderPosition(camera, alpha).x+42, ent.getRenderPosition(camera, alpha).y+64, 0xffffffff));
		
		this.hud.render(canvas, camera, alpha);

		
		
	}		
}

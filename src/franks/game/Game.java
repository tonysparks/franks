/*
 * see license.txt 
 */
package franks.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import franks.FranksGame;
import franks.game.CommandAction.CompletionState;
import franks.game.CommandQueue.CommandRequest;
import franks.game.entity.Building;
import franks.game.entity.Entity;
import franks.game.entity.Human;
import franks.game.entity.ResourceEntity;
import franks.game.entity.Entity.Type;
import franks.gfx.Art;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.gfx.Cursor;
import franks.gfx.RenderFont;
import franks.gfx.Renderable;
import franks.gfx.Terminal;
import franks.map.MapTile;
import franks.math.Rectangle;
import franks.math.Vector2f;
import franks.util.Cons;
import franks.util.TimeStep;
import leola.vm.types.LeoMap;
import leola.vm.types.LeoObject;

/**
 * @author Tony
 *
 */
public class Game implements Renderable {

	private World world;
	
	private MovementMeter moveMeter;
	private Resources resources;
	
	private Human human;
	
	private List<Entity> entities;
	private List<Entity> aliveEntities;
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
	/**
	 * 
	 */
	public Game(FranksGame app, Camera camera) {
		this.camera = camera;
		this.terminal = app.getTerminal();
		this.randomizer = new Randomizer();
		this.world = new World(camera, app.getUiManager().getCursor());
		this.cursor = app.getUiManager().getCursor();
		
		this.moveMeter = new MovementMeter();
		this.healthMeter = new HealthMeter();
		this.resources = new Resources();
		
		this.entities = new ArrayList<>();
		this.aliveEntities = new ArrayList<>();
		
		this.selectedEntity = Optional.empty();
		this.hoveredOverEntity = Optional.empty();
		
		this.actions = new ArrayList<>();
		this.inprogressActions = new ArrayList<>();
		
		this.mouseWorldPos = new Vector2f();

		this.currentTurn = new Turn(0, getMoveMeter());
		
		this.commandQueue = new CommandQueue(this);
		
		this.cameraController = new CameraController(world.getMap(), camera);
		
		// temp

		this.human = new Human(this);
		this.human.moveToRegion(0, 0);
		addEntity(human);
		
		newTree(0, 3);
		newStone(1, 0);
		newBerryBush(2, 2);
		newBerryBush(3, 6);
		
		//newBuilding(8, 3, 2, 2);
	}
	
	public Entity newStone(int x, int y) {
		Entity ent = new ResourceEntity(this, Type.STONE, "Stone", Art.stone, 40 + randomizer.nextInt(10));
		ent.moveToRegion(x, y);
		addEntity(ent);
		return ent;
	}
	
	public Entity newTree(int x, int y) {
		Entity ent = new ResourceEntity(this, Type.TREE, "Wood", Art.tree, 120 + randomizer.nextInt(10));
		ent.moveToRegion(x, y);
		addEntity(ent);
		return ent;
	}
	
	public Entity newBerryBush(int x, int y) {
		Entity ent = new ResourceEntity(this, Type.BERRY_BUSH, "Food", Art.berryBush, 30 + randomizer.nextInt(10));
		ent.moveToRegion(x, y);
		addEntity(ent);
		return ent;
	}
	
	public Entity newBuilding(int x, int y, int tileWidth, int tileHeight) {
		Entity ent = new Building(this, tileWidth*World.RegionWidth, tileHeight*World.RegionHeight);
		ent.moveToRegion(x, y);
		addEntity(ent);
		return ent;
	}
	
	
	public Game addEntity(Entity ent) {
		this.entities.add(ent);
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
		
		this.moveMeter.reset(15 + randomizer.nextInt(5));
		Turn next = new Turn(this.currentTurn.getNumber()+1, this.moveMeter);
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
	
	public boolean selectEntity() {
		selectedEntity.ifPresent(ent -> ent.isSelected(false));
		selectedEntity = Optional.ofNullable(getEntityOverMouse());
		selectedEntity.ifPresent(ent -> ent.isSelected(true));
		return selectedEntity.isPresent();
	}
	
	public boolean hoveringOverEntity() {
		hoveredOverEntity = Optional.ofNullable(getEntityOverMouse());		
		return hoveredOverEntity.isPresent();
	}
	
	public Entity getEntityOverMouse() {				
		Vector2f worldPos = getMouseWorldPos();
		MapTile tile = world.getMapTileByWorldPos(worldPos);
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
	
	public CommandRequest makeRequest(String action) {
		return new CommandRequest(this, action);
	}
	
	public void executeCommandRequest(CommandRequest request) {
		request.selectedEntity.flatMap(ent -> ent.getCommand(request.action))
		  					  .filter(cmd -> cmd.checkPreconditions(this, request).isMet())
		  					  .ifPresent(cmd -> actions.add(cmd.doAction(this, request)));
	}
	
	public void queueCommand() {
		Entity resource = getEntityOverMouse();
		
		if(resource!=null) {
			if(getSelectedEntity().isPresent()) {
				if(getSelectedEntity().get() == resource) {
					return;
				}
			}
			
			// TODO make generic!!
			switch(resource.getType()) {
				case BERRY_BUSH:
					this.commandQueue.add(makeRequest("collectFood"));
					break;
				case STONE:
					this.commandQueue.add(makeRequest("collectStone"));
					break;
				case TREE:
					this.commandQueue.add(makeRequest("collectWood"));
					break;
				default: // throw new IllegalArgumentException("Unsupported type: " + resource.getType());
					Cons.println("Unsupported type: " + resource.getType());
			}
			
		}
		else {
			this.commandQueue.add(makeRequest("moveTo"));			
		}
	}
	
	// TODO create a menu over target entity, to display dispatch
	// command to do
	public void dispatchCommand() {
		Entity resource = getEntityOverMouse();
		if(resource!=null) {
			dispatchCommand("collectWood");
		}
		else {
			dispatchCommand("moveTo");
		}
	}
	
	public void dispatchCommand(final String cmdName) {
		final CommandRequest request = makeRequest(cmdName);
		executeCommandRequest(request);
//		selectedEntity.flatMap(ent -> ent.getCommand(cmdName))
//					  .filter(cmd -> cmd.checkPreconditions(this, request).isMet())
//					  .ifPresent(cmd -> actions.add(cmd.doAction(this, request)));
	}
	
	
	/**
	 * @return the moveMeter
	 */
	public MovementMeter getMoveMeter() {
		return moveMeter;
	}
	
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
			if(ent.isAlive()) {
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
	}
	
	/* (non-Javadoc)
	 * @see newera.gfx.Renderable#render(newera.gfx.Canvas, newera.gfx.Camera, float)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {
		this.world.render(canvas, camera, alpha);
		this.actions.forEach(action -> action.render(canvas, camera, alpha));
		
		this.entities.forEach(ent -> ent.render(canvas, camera, alpha));
		
		
		int textColor = 0xff00ff00;
		canvas.setFont("Consola", 12);
		
		this.resources.render(canvas, camera, alpha);
		
		float y = 600;
		float x = 10;
		RenderFont.drawShadedString(canvas, "Movements: " + moveMeter.getMovementAmount(), x, y, textColor);
		RenderFont.drawShadedString(canvas, "Health: " + healthMeter.getHealth(), x, y+=15f, textColor);
		RenderFont.drawShadedString(canvas, "Happiness: " + healthMeter.getHappiness(), x, y+=15f, textColor);
		
		this.selectedEntity.ifPresent(ent -> {
			float sx = 20;
			float sy = 20;
			RenderFont.drawShadedString(canvas, "Selected: " + ent.getType(), sx, sy, textColor);
			renderEntityAttributes(canvas, ent, sx, sy + 15, textColor);
		});
		
		
		this.hoveredOverEntity.ifPresent(ent -> {
			Vector2f pos = cursor.getCursorPos();
			RenderFont.drawShadedString(canvas, "" + ent.getType(), pos.x + 30, pos.y, textColor);
			renderEntityAttributes(canvas, ent, pos.x + 30, pos.y + 15, textColor);
		});
		
		
	}
}

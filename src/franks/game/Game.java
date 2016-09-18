/*
 * see license.txt 
 */
package franks.game;

import franks.FranksGame;
import franks.game.commands.Command.CommandType;
import franks.game.commands.CommandQueue.CommandRequest;
import franks.game.entity.Entity;
import franks.game.entity.Entity.Type;
import franks.game.entity.EntityData;
import franks.game.entity.EntityGroupData;
import franks.game.entity.EntityGroupData.EntityInstanceData;
import franks.game.entity.EntityList;
import franks.game.net.NetEntity;
import franks.game.net.NetMessage;
import franks.gfx.Camera;
import franks.gfx.Cursor;
import franks.gfx.Renderable;
import franks.gfx.Terminal;
import franks.map.IsometricMap;
import franks.map.Map;
import franks.map.MapTile;
import franks.math.Vector2f;
import franks.util.Cons;
import franks.util.TimeStep;

/**
 * @author Tony
 *
 */
public abstract class Game implements Renderable, ResourceLoader { 
	
	private GameState state;
	private FranksGame app;
	protected World world;
	
	protected EntityList entities;
	
	protected Entity selectedEntity;
	protected Entity hoveredOverEntity;
		
	private Vector2f cursorPos;
	private Cursor cursor;
	
	private Terminal terminal;
	
	protected Turn currentTurn;
		
	protected Camera camera;
	private CameraController cameraController;
		
	protected Army redTeam, greenTeam;
	protected Player redPlayer;
	protected Player greenPlayer;
	
	protected Player localPlayer;
	
	protected Ids entitityIds;
		
	/**
	 * 
	 */
	public Game(FranksGame app, GameState state, Camera camera) {
		this.app = app;
		this.camera = camera;
		this.state = state;				
		
		this.entitityIds = app.getEntityIds();
		
		this.greenPlayer = state.getGreenPlayer();
		this.greenTeam = this.greenPlayer.getTeam();
		
		this.redPlayer = state.getRedPlayer();
		this.redTeam = this.redPlayer.getTeam();
		
		this.localPlayer = state.getLocalPlayer();
		
		this.entities = new EntityList(getEntitityIds());
		this.cursorPos = new Vector2f();
		
		this.terminal = app.getTerminal();
		this.cursor = app.getUiManager().getCursor();
		
		this.world = createWorld(state);		
		this.cameraController = new CameraController(world.getMap(), camera);
	}
	
	public void enter() {
		Map map = getWorld().getMap();
		this.camera.setWorldBounds(new Vector2f(map.getMapWidth(), map.getMapHeight()));
		Army localTeam = this.localPlayer.getTeam();
		if(localTeam.armySize() > 0) {
			this.camera.centerAroundNow(localTeam.getLeaders().get(0).getScreenPosition());
		}
		else {
			this.camera.centerAround(new Vector2f(map.getMapWidth()/2f, map.getMapHeight()/2f));
		}
		
		this.cameraController.resetToCameraPos();
	}
	
	public void exit() {}
	
	/**
	 * Creates the {@link World} for this {@link Game} instance
	 * 
	 * @param state
	 * @return the {@link World}
	 */
	protected abstract World createWorld(GameState state);
	
	
	public EntityData loadEntity(String file) {
		EntityData data = loadData(file, EntityData.class);
		data.dataFile = file;
		return data;
	}
	
	public EntityGroupData loadGroupData(String file) {
		return loadData(file, EntityGroupData.class);
	}
	
	@Override
	public <T> T loadData(String file, Class<T> type) {
		return state.loadData(file, type);
	}
	
	/**
	 * @return the state
	 */
	public GameState getState() {
		return state;
	}
	
	/**
	 * @return the app
	 */
	public FranksGame getApp() {
		return app;
	}
	
	/**
	 * @return the entitityIds
	 */
	public Ids getEntitityIds() {
		return entitityIds;
	}
	
	/**
	 * @return the camera
	 */
	public Camera getCamera() {
		return camera;
	}
	
	
	/**
	 * @return the localPlayer
	 */
	public Player getLocalPlayer() {
		return localPlayer;
	}
	
	public Player getAIPlayer() {
		return state.getAIPlayer();
	}
	
	/**
	 * @return the greenPlayer
	 */
	public Player getGreenPlayer() {
		return greenPlayer;
	}
	
	/**
	 * @return the redPlayer
	 */
	public Player getRedPlayer() {
		return redPlayer;
	}
	
	/**
	 * @param player
	 * @return the opposite player of the supplied one
	 */
	public Player getOtherPlayer(Player player) {
		if(player==greenPlayer) {
			return redPlayer;
		}
		return greenPlayer;
	}
	
	/**
	 * @param army
	 * @return the opposite team of the supplied one
	 */
	public Army getOtherTeam(Army army) {
		if(army == greenTeam) {
			return redTeam;
		}
		return greenTeam;
	}
	
	public boolean isSinglePlayer() {
		return state.isSinglePlayer();
	}
	
	/**
	 * @return the cursor
	 */
	public Cursor getCursor() {
		return cursor;
	}
	
	
	@Override
	public TextureCache getTextureCache() {
		return this.state.getTextureCache();
	}
	
	/**
	 * Handles a remote message
	 * 
	 * @param msg
	 */
	public void handleNetMessage(NetMessage msg) {		
	}
	
	/**
	 * Builds the {@link Entity} from the {@link EntityInstanceData}
	 * 
	 * @param army
	 * @param ref
	 * @return the {@link Entity}
	 */
	public Entity buildEntity(Army army, EntityInstanceData ref) {
		return buildEntity(getEntities(), army, ref);
	}
	
	/**
	 * Builds the {@link Entity} from the {@link EntityInstanceData}
	 * 
	 * @param army
	 * @param ref
	 * @return the {@link Entity}
	 */
	public Entity buildEntity(EntityList entities, Army army, EntityInstanceData ref) {
		EntityData data = loadEntity("assets/entities/" + ref.dataFile);
		Entity dataEnt = entities.buildEntity(this, army, data);
		dataEnt.moveToRegion(ref.x, ref.y);
		dataEnt.setCurrentDirection(ref.direction);
		dataEnt.setDesiredDirection(ref.direction);
		
		dataEnt.visitTiles(getMap());
		
		return dataEnt;
	}
	
	public Entity buildEntity(EntityList entities, Army army, NetEntity net) {
		EntityData data = loadEntity(net.dataFile);
		Entity dataEnt = entities.buildEntity(net.id, this, army, data);
		dataEnt.syncFrom(net);
		return dataEnt;
	}
		
	public Entity getEntityOnTile(MapTile tile) {
		return entities.getEntityOnTile(tile);
	}
	
	/**
	 * @return the currentTurn
	 */
	public Turn getCurrentTurn() {
		return currentTurn;
	}
	
	public void endCurrentTurn() {
		if(this.currentTurn.isPlayersTurn(this.localPlayer)) {
			if(entities.commandsCompleted()) {
				this.currentTurn.markTurnCompleted();
			}
		}
	}
	
	public void endCurrentTurnAI() {
		if(isSinglePlayer()) {
			if(!this.currentTurn.isPlayersTurn(this.localPlayer)) {
				if(entities.commandsCompleted()) {
					this.currentTurn.markTurnCompleted();
				}
			}
		}
	}
	
	
	/**
	 * @return true if there is a selected entity
	 */
	public boolean hasSelectedEntity() {
		return this.selectedEntity != null;
	}
	
	public boolean hasHoveredOverEntity() {
		return getEntityOverMouse() != null;
	}
	
	/**
	 * @return the selectedEntity
	 */
	public Entity getSelectedEntity() {
		return selectedEntity;
	}
		
	public boolean selectEntity() {
		if(this.selectedEntity != null) {
			this.selectedEntity.isSelected(false);
			this.selectedEntity = null;
		}
		
		Entity newSelectedEntity = getEntityOverMouse();
		if(newSelectedEntity != null) {
			newSelectedEntity.isSelected(true);
			this.selectedEntity = newSelectedEntity;
			return true;
		}
		return false;
	}
		
	public boolean hoveringOverEntity() {
		hoveredOverEntity = getEntityOverMouse();		
		return hoveredOverEntity!=null;
	}
	
	public Entity getEntityOverMouse() {				
		Vector2f screenPos = getCursorPos();
		return getEntityOverPos(screenPos);
	}
	
	public Entity getEntityOverPos(Vector2f screenPos) {						
		MapTile tile = world.getMapTileByScreenPos(screenPos);
		if(tile!=null) {
			return entities.getEntityByBounds(tile.getBounds());
		}
		
		return null;
	}
	
	public MapTile getTileOverMouse() {
		Vector2f screenPos = getCursorPos();
		return getTileOverPos(screenPos);
	}
	
	public MapTile getTileOverPos(Vector2f screenPos) {		
		MapTile tile = world.getMapTileByScreenPos(screenPos);
		return tile;
	}
	
	public MapTile getTile(Vector2f tilePos) {
		MapTile tile = world.getMapTile(tilePos);
		return tile;
	}
	
	/**
	 * @return the entities
	 */
	public EntityList getEntities() {
		return entities;
	}
	
	public Entity getEntityById(int id) {
		return entities.getEntity(id);
	}
	
	/**
	 * @return the randomizer
	 */
	public Randomizer getRandomizer() {
		return this.state.getRandom();
	}
	
	/**
	 * Get the position of the cursor in screen coordinates
	 * @return the current screen coordinates of the mouse cursor
	 */
	public Vector2f getCursorPos() {
		this.cursorPos.set(cursor.getCursorPos());
		return this.cursorPos;
	}
	
	/**
	 * Get the cursor position relative to tile position
	 * @return the cursor position relative to tile position
	 */
	public Vector2f getCursorTilePos() {
		return world.getMapTilePosByScreenPos(getCursorPos());
	}
	
	
	protected void dispatchCommand(Entity entity, CommandType type) {				
		entity.queueAction(new CommandRequest(this, type, entity));		
	}
	
	public void queueCommand() {
		if(!currentTurn.isPlayersTurn(localPlayer)) {
			return;
		}
		
		if(this.selectedEntity==null) {
			return;
		}
		
		if(!localPlayer.owns(selectedEntity)) {
			return;
		}
		
		Entity target = getEntityOverMouse();
		if(target!=null && this.selectedEntity!=target) {
			Type type = target.getType();
			if(type.isAttackable()) {
				dispatchCommand(selectedEntity, CommandType.Attack);
			}
			else {
				Cons.println("Unsupported type: " + target.getType());
			}			
		}
		else {
			dispatchCommand(selectedEntity, CommandType.Move);
		}
		
	}
	
	
	/**
	 * Adds the completed command's {@link CommandRequest} to this turns
	 * history buffer
	 * 
	 * @param request
	 */
	public void addCommandRequestToHistory(CommandRequest request) {
//		if(!isSinglePlayer()) {
//			this.currentTurn.addCommandRequest(request);
//		}
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

	
	@Override
	public void update(TimeStep timeStep) {				
		Vector2f mousePos = cursor.getCursorPos();
		if(!terminal.isActive()) {
			this.cameraController.applyPlayerMouseInput(mousePos.x, mousePos.y);
			this.cameraController.update(timeStep);
		}
		this.world.update(timeStep);
		
		this.entities.update(timeStep);
		this.camera.update(timeStep);
				
		this.currentTurn = this.currentTurn.checkTurnState();		
	}			
}

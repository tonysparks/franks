/*
 * see license.txt 
 */
package franks.game;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.hjson.JsonValue;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import franks.FranksGame;
import franks.game.Team.TeamName;
import franks.game.commands.Command.CommandType;
import franks.game.commands.CommandQueue.CommandRequest;
import franks.game.entity.Entity;
import franks.game.entity.EntityData;
import franks.game.entity.EntityGroupData;
import franks.game.entity.EntityGroupData.EntityInstanceData;
import franks.game.entity.EntityList;
import franks.game.net.NetEntity;
import franks.game.net.NetGameFullState;
import franks.game.net.NetGamePartialState;
import franks.game.net.NetMessage;
import franks.game.net.PeerConnection;
import franks.game.net.websocket.WebSocketClient;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.gfx.Cursor;
import franks.gfx.Renderable;
import franks.gfx.Terminal;
import franks.map.IsometricMap;
import franks.map.MapTile;
import franks.math.Vector2f;
import franks.util.Command;
import franks.util.Cons;
import franks.util.Console;
import franks.util.TimeStep;

/**
 * @author Tony
 *
 */
public class Game implements Renderable {

	public static final int MAX_ENTITIES = 256;
	
	private FranksGame app;
	private World world;
	
	private EntityList entities;
	
	private Entity selectedEntity;
	private Entity hoveredOverEntity;
		
	private Vector2f cursorPos;
	private Cursor cursor;
	
	private Terminal terminal;
	
	private Randomizer randomizer;
	private Turn currentTurn;
	
	private Hud hud;
	
	private Camera camera;
	private CameraController cameraController;
	
	private TextureCache textureCache;
	private Gson gson;
	
	private Team redTeam, greenTeam;
	private Player redPlayer;
	private Player greenPlayer;
	
	private Player localPlayer;
	private boolean isSinglePlayer;
	
	private PeerConnection connection;
	
	
	/**
	 * 
	 */
	public Game(FranksGame app, Camera camera, boolean isSinglePlayer) {
		this.camera = camera;
		this.isSinglePlayer = isSinglePlayer;
		this.gson = new GsonBuilder().create();
		
		this.terminal = app.getTerminal();
		this.randomizer = new Randomizer();
		this.cursor = app.getUiManager().getCursor();
		this.world = new World(this); 
		
		this.textureCache = new TextureCache();
		
		this.entities = new EntityList(this);
		
		this.cursorPos = new Vector2f();
		
		this.cameraController = new CameraController(world.getMap(), camera);
	
		this.redTeam = new Team("Red");
		this.greenTeam = new Team("Green");
		
		this.hud = new Hud(this);
		
		
		this.redPlayer = new Player("Red Player", redTeam);
		this.greenPlayer = new Player("Green Player", greenTeam);
		this.localPlayer = this.greenPlayer;
		
		this.currentTurn = new Turn(this, this.localPlayer, 0);
		
		// temp
		app.getConsole().addCommand(new Command("reload") {
			
			@Override
			public void execute(Console console, String... args) {
				entities.clear();
				
				EntityGroupData redGroupData = loadGroupData("assets/red.json");
				redPlayer.setEntities(redGroupData.buildEntities(redTeam, Game.this));
				
				EntityGroupData greenGroupData = loadGroupData("assets/green.json");
				greenPlayer.setEntities(greenGroupData.buildEntities(greenTeam, Game.this));
			}
		});
		app.getConsole().execute("reload");
	}
	
	public PeerConnection peerConnection(Session session) {
		if(this.connection!=null) {
			this.connection.close();
		}
		
		this.connection = new PeerConnection(this, session);
		return this.connection;
	}
	
	public boolean connectToPeer(String uri) {
		try {
			WebSocketContainer container = ContainerProvider.getWebSocketContainer();
			Session session = container.connectToServer(WebSocketClient.class, URI.create(uri));
			if(session!=null && session.isOpen()) {
				return true;
			}
		}
		catch(Exception e) {
			Cons.println("*** Unable to connect to remote peer: " + e);
		}
		
		return false;
	}
	
	
	public EntityData loadEntity(String file) {
		EntityData data = loadData(file, EntityData.class);
		data.dataFile = file;
		return data;
	}
	
	public EntityGroupData loadGroupData(String file) {
		return loadData(file, EntityGroupData.class);
	}
	
	public <T> T loadData(String file, Class<T> type) {
		try {
			String data = JsonValue.readHjson(Gdx.files.internal(file).reader(1024)).toString();
			return gson.fromJson(data, type);
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
	
	public boolean isPeerConnected() {
		return this.connection != null && this.connection.isConnected();
	}
	
	/**
	 * @return the isSinglePlayer
	 */
	public boolean isSinglePlayer() {
		return isSinglePlayer;
	}
	
	/**
	 * @return the connection
	 */
	public PeerConnection getConnection() {
		return connection;
	}
	
	/**
	 * @return the localPlayer
	 */
	public Player getLocalPlayer() {
		return localPlayer;
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
	 * @return the cursor
	 */
	public Cursor getCursor() {
		return cursor;
	}
	
	/**
	 * @return the textureCache
	 */
	public TextureCache getTextureCache() {
		return textureCache;
	}
	
	
	/**
	 * Handles a remote message
	 * 
	 * @param msg
	 */
	public void handleNetMessage(NetMessage msg) {
		switch(msg.type) {
			case FullState:
				this.entities.clear();
				
				NetGameFullState state = msg.asNetGameFullState();
				this.randomizer = new Randomizer(state.seed, state.generation);
				this.greenPlayer.syncFrom(state.greenPlayer);
				for(NetEntity ent : state.greenPlayer.entities) {
					greenPlayer.addEntity(buildEntity(greenTeam, ent));
				}
				
				this.redPlayer.syncFrom(state.redPlayer);
				for(NetEntity ent : state.redPlayer.entities) {
					redPlayer.addEntity(buildEntity(redTeam, ent));
				}
				
				Player activePlayer = greenPlayer;
				if(state.currentPlayersTurn==TeamName.Red) {
					activePlayer = redPlayer;
				}
				
				this.currentTurn = new Turn(this, activePlayer, state.turnNumber);
				this.localPlayer = redPlayer;
				
				break;
			case PartialState:
				break;
			case Turn: {
				this.currentTurn.handleNetTurnMessage(msg.asNetTurn());
				break;
			}
		}
	}
	
	
	/**
	 * Builds the {@link Entity} from the {@link EntityInstanceData}
	 * 
	 * @param team
	 * @param ref
	 * @return the {@link Entity}
	 */
	public Entity buildEntity(Team team, EntityInstanceData ref) {
		EntityData data = loadEntity("assets/entities/" + ref.dataFile);
		Entity dataEnt = entities.buildEntity(team, data);
		dataEnt.moveToRegion(ref.x, ref.y);
		dataEnt.setCurrentDirection(ref.direction);
		dataEnt.setDesiredDirection(ref.direction);
				
		return dataEnt;
	}
	
	public Entity buildEntity(Team team, NetEntity net) {
		EntityData data = loadEntity(net.dataFile);
		Entity dataEnt = entities.buildEntity(net.id, team, data);
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
	
	
	/**
	 * @return the selectedEntity
	 */
	public Entity getSelectedEntity() {
		return selectedEntity;
	}
		
	public boolean selectEntity() {
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
		return randomizer;
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
	
	
	private void dispatchCommand(Entity entity, CommandType type) {				
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
			switch(target.getType()) {				
				case HUMAN:					
					dispatchCommand(selectedEntity, CommandType.Attack);
					break;
				default: 
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
		if(!isSinglePlayer()) {
			this.currentTurn.addCommandRequest(request);
		}
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
		this.hud.update(timeStep);
		
		if(isPeerConnected()) {
			this.connection.update(timeStep);
		}
				
		this.currentTurn = this.currentTurn.checkTurnState();
		
		if(this.isSinglePlayer) {
			this.localPlayer = this.currentTurn.getActivePlayer();
		}
		
	}

	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {
		this.world.render(canvas, camera, alpha);
		
		this.hud.renderUnderEntities(canvas, camera, alpha);
		this.entities.render(canvas, camera, alpha);
		
		this.hud.render(canvas, camera, alpha);
	}
	
	
	public NetGameFullState getNetGameFullState() {
		NetGameFullState net = new NetGameFullState();
		net.greenPlayer = greenPlayer.getNetPlayer();
		net.redPlayer = redPlayer.getNetPlayer();
		net.turnNumber = currentTurn.getNumber();
		
		net.seed = this.randomizer.getStartingSeed();
		net.generation = this.randomizer.getIteration();
		
		net.currentPlayersTurn = currentTurn.getActivePlayer()==greenPlayer 
				? TeamName.Green : TeamName.Red;
		
		return net;
	}
	
	public NetGamePartialState getNetGamePartialState() {
		NetGamePartialState net = new NetGamePartialState();
		net.greenEntities = greenPlayer.getNetPlayer().entities;
		net.redEntities = redPlayer.getNetPlayer().entities;
		net.turnNumber = currentTurn.getNumber();
		return net;
	}
	
	public void syncFrom(NetGameFullState net) {
		this.greenPlayer.syncFrom(net.greenPlayer);
		
		List<Entity> greens = new ArrayList<>();
		for(NetEntity ent : net.greenPlayer.entities) {
			greens.add(buildEntity(greenTeam, ent));
		}
		
		
		this.redPlayer.syncFrom(net.redPlayer);
		
		List<Entity> reds = new ArrayList<>();
		for(NetEntity ent : net.redPlayer.entities) {
			reds.add(buildEntity(redTeam, ent));
		}				
	}
	
	public void synFrom(NetGamePartialState net) {
		syncFrom(greenTeam, net.greenEntities);
		syncFrom(redTeam, net.redEntities);		
	}
	
	
	private void syncFrom(Team team, List<NetEntity> netEntities) {
		for(int i = 0; i < netEntities.size();i++) {
			NetEntity netEnt = netEntities.get(i);
			Entity ent = entities.getEntity(netEnt.id);
			if(ent==null) {
				buildEntity(greenTeam, netEnt);
			}
			else {
				ent.syncFrom(netEnt);
			}
		}
	}
}

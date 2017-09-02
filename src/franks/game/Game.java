/*
 * see license.txt 
 */
package franks.game;

import franks.FranksGame;
import franks.game.actions.Action.ActionType;
import franks.game.actions.Command;
import franks.game.entity.Entity;
import franks.game.entity.Entity.Type;
import franks.game.entity.EntityData;
import franks.game.entity.EntityGroupData;
import franks.game.entity.EntityGroupData.EntityInstanceData;
import franks.game.entity.EntityList;
import franks.game.entity.meta.LeaderEntity;
import franks.game.net.NetEntity;
import franks.game.net.NetLeaderEntity;
import franks.game.net.PeerConnection;
import franks.gfx.Camera;
import franks.gfx.Cursor;
import franks.gfx.GameController;
import franks.gfx.Renderable;
import franks.gfx.Terminal;
import franks.map.IsometricMap;
import franks.map.Map;
import franks.map.MapTile;
import franks.math.Vector2f;
import franks.util.Cons;
import franks.util.TimeStep;
import leola.frontend.listener.Event;

/**
 * @author Tony
 *
 */
public abstract class Game implements Renderable, ResourceLoader { 
    
    private FranksGame app;
    protected GameState gameState;
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
    private int inputKeys;
        
    protected Army redTeam, greenTeam;
    protected Player redPlayer;
    protected Player greenPlayer;
        
    protected OtherPlayer otherPlayer;
    
    protected Ids entitityIds;
    
    
    /**
     * 
     */
    public Game(FranksGame app, GameState state, Camera camera) {
        this.app = app;
        this.camera = camera;
        this.gameState = state;                
        
        this.entitityIds = app.getEntityIds();
                
        this.greenPlayer = state.getGreenPlayer();
        this.greenTeam = this.greenPlayer.getTeam();
        
        this.redPlayer = state.getRedPlayer();
        this.redTeam = this.redPlayer.getTeam();
                
        this.otherPlayer = state.getOtherPlayer();
        
        this.entities = new EntityList(getEntitityIds());
        this.cursorPos = new Vector2f();
        
        this.terminal = app.getTerminal();
        this.cursor = app.getUiManager().getCursor();
        
        this.world = createWorld(state);        
        this.cameraController = new CameraController(this.entities, world.getMap(), camera);
        
    }
    
    public void setTurn(Player active, int turnNumber) {
        this.currentTurn = new Turn(this, active, turnNumber);
    }
    
    public void enter() {
        this.gameState.setActiveGame(this);
        
        Map map = getWorld().getMap();
        this.camera.setWorldBounds(new Vector2f(map.getMapWidth(), map.getMapHeight()));
        Army localTeam = getLocalPlayer().getTeam();
        if(localTeam.armySize() > 0) {
            centerCameraAround(localTeam.getLeaders().get(0).getScreenPosition());
        }
        else {
            centerCameraAround(new Vector2f(map.getMapWidth()/2f, map.getMapHeight()/2f));
        }                
    }
    
    public void exit() {}
    
    
    public boolean isConnected() {
        return this.gameState.isConnected();
    }
    
    public PeerConnection getConnection() {
        return this.gameState.getConnection();
    }

    
    /**
     * Centers the camera around the supplied screen position
     * @param screenPos
     */
    public void centerCameraAround(Vector2f screenPos) {
        this.camera.centerAroundNow(screenPos);
        this.cameraController.resetToCameraPos();
    }
    
    /**
     * Creates the {@link World} for this {@link Game} instance
     * 
     * @param gameState
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
        return gameState.loadData(file, type);
    }
    
    /**
     * Dispatches an event
     * @param event
     */
    public void dispatchEvent(Event event) {
        this.gameState.getDispatcher().queueEvent(event);
    }
    
    /**
     * @return the gameState
     */
    public GameState getState() {
        return gameState;
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
        return gameState.getLocalPlayer();
    }
    
    public Player getAIPlayer() {
        return gameState.getAIPlayer();
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
        return gameState.isSinglePlayer();
    }
    
    /**
     * @return the cursor
     */
    public Cursor getCursor() {
        return cursor;
    }
    
    
    @Override
    public TextureCache getTextureCache() {
        return this.gameState.getTextureCache();
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
        if(ref.direction!=null) {
            dataEnt.setCurrentDirection(ref.direction);
            dataEnt.setDesiredDirection(ref.direction);
        }
                
        return dataEnt;
    }
    
    public LeaderEntity buildLeaderEntity(Army army, NetLeaderEntity net, Game battle) {
        LeaderEntity leader = (LeaderEntity)buildEntity(army, net);        
        for(NetEntity netEntity : net.entities) {
            Entity ent = buildEntity(leader.getEntities(), army, netEntity);
            leader.addEntity(ent);
        }
        return leader;
    }
    
    public Entity buildEntity(Army army, NetEntity net) {
        return buildEntity(getEntities(), army, net);
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
        if(this.currentTurn.isPlayersTurn(getLocalPlayer())) {
            if(entities.commandsCompleted()) {
                this.currentTurn.requestForTurnCompletion();
            }
        }
    }
    
    public void endCurrentTurnAI() {
        if(isSinglePlayer()) {
            if(!this.currentTurn.isPlayersTurn(getLocalPlayer())) {
                if(entities.commandsCompleted()) {
                    this.currentTurn.requestForTurnCompletion();
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
        return this.gameState.getRandom();
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
        
    
    /**
     * Dispatches the supplied {@link Command}
     * 
     * @param command
     * @return true if the command was dispatched, false otherwise
     */
    public boolean dispatchCommand(Command command) {
        if(command.selectedEntity==null) {
            return false;
        }
        
        command.selectedEntity.queueAction(command);
        return true;
    }
    
    /**
     * Dispatches the most appropriate command based on the players
     * selection and mouse position
     * 
     * @
     * @return true if the command was dispatched, false otherwise
     */
    public boolean dispatchCommand(ActionType actionType) {
        if(!currentTurn.isPlayersTurn(getLocalPlayer())) {
            return false;
        }
        
        if(this.selectedEntity==null) {
            return false;
        }
        
        if(!getLocalPlayer().owns(selectedEntity)) {
            return false;
        }
        
        Entity target = getEntityOverMouse();
        if(target!=null && this.selectedEntity!=target) {
            Type type = target.getType();
            if(!type.isAttackable()) {
                Cons.println("Unsupported type: " + target.getType());
                return false;
            }
            else {
                dispatchCommand(new Command(this, ActionType.Attack, selectedEntity));                    
            }    
            
        }
        else {
            switch(actionType) {
                case Move: 
                    dispatchCommand(new Command(this, ActionType.Move, selectedEntity));
                    break;
                case Build:
                    dispatchCommand(new Command(this, ActionType.Build, selectedEntity));
                    break;
                case Die:
                    dispatchCommand(new Command(this, ActionType.Die, selectedEntity));
                    break;
                default: {
                      Cons.println("Unsupported action type: " + actionType);
                    return false;
                }
            }
        }
        
        return true;
        
    }
    
    
    /**
     * Records the fact that the supplied {@link Command} was executed
     * this turn.
     * 
     * @param request
     */
    public void recordCommand(Command command) {        
        this.currentTurn.recordCommand(command);        
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
            GameController controller = (GameController)this.app.getActiveInputs();
            inputKeys = controller.pollInputs(timeStep, app.getKeyMap(), cursor, inputKeys);
//            if(inputKeys!=0) {
//                System.out.println(inputKeys);
//            }
            
            this.cameraController.applyPlayerInput(mousePos.x, mousePos.y, inputKeys);
            this.cameraController.update(timeStep);
            inputKeys = 0;
        }
        
        this.gameState.update(timeStep);        
        this.otherPlayer.update(timeStep);        
        this.world.update(timeStep);
        
        this.entities.update(timeStep);
        this.camera.update(timeStep);
                
        this.currentTurn = this.currentTurn.checkTurnState();        
    }            
    
    
    
}

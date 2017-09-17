/*
 * see license.txt 
 */
package franks.game;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;

import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.hjson.JsonValue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import franks.FranksGame;
import franks.game.Army.ArmyName;
import franks.game.actions.ActionType;
import franks.game.ai.AIPlayer;
import franks.game.battle.BattleGame;
import franks.game.entity.Entity;
import franks.game.events.BattleEvent;
import franks.game.events.TurnCompletedEvent;
import franks.game.meta.MetaGame;
import franks.game.net.GameNetworkProtocol;
import franks.game.net.NetBattle;
import franks.game.net.NetBattleFinished;
import franks.game.net.NetEntity;
import franks.game.net.NetGameFullState;
import franks.game.net.NetTurn;
import franks.game.net.NetworkProtocol;
import franks.game.net.PeerConnection;
import franks.game.net.RemotePlayer;
import franks.game.net.websocket.GameServer;
import franks.game.net.websocket.WebSocketClient;
import franks.gfx.Camera;
import franks.gfx.Camera2d;
import franks.math.Rectangle;
import franks.math.Vector2f;
import franks.util.Cons;
import franks.util.TimeStep;
import franks.util.Updatable;
import leola.frontend.listener.EventDispatcher;

/**
 * Persistent game gameState
 * 
 * @author Tony
 *
 */
public class GameState implements ResourceLoader, NetworkProtocol, Updatable {
    public static final int MAX_ENTITIES = 256;
    
    private FranksGame app;
    private Player greenPlayer;
    private Player redPlayer;        
    private Player localPlayer;
    private OtherPlayer otherPlayer;

    private TextureCache textureCache;
    private Randomizer random;
    private Gson gson;
    
    private Camera camera;
    private com.badlogic.gdx.graphics.OrthographicCamera mapCamera;
    private SpriteBatch spriteBatch;
    
    private Game activeGame;
    private EventDispatcher dispatcher;
    
    private boolean isSinglePlayer;
    private boolean isHost;
    
    private GameServer server;
    private PeerConnection connection;
    private NetworkProtocol protocol;
    
    private MetaGame metaGame;
    
    public GameState(FranksGame app, boolean isHost, boolean isSinglePlayer) {
        this.app = app;
        
        this.redPlayer = new Player("Red Player");
        this.greenPlayer = new Player("Green Player");
        
        this.redPlayer.setTeam(new Army("Red", new Color(0.98f, 0.67f, 0.67f, 0.78f)));
        this.greenPlayer.setTeam(new Army("Green", new Color(0.67f, 0.98f, 0.67f, 0.78f)));
        
        this.localPlayer = this.greenPlayer;
        
        this.isHost = isHost;
        this.isSinglePlayer = isSinglePlayer;
        
        init();
    }
    
    /**
     * @param redPlayer
     * @param greenPlayer
     * @param localPlayer
     * @param isSinglePlayer
     * @param app
     */
    public GameState(FranksGame app, Player greenPlayer, Player redPlayer, boolean isHost, boolean isSinglePlayer) {
        this.app = app;        
        this.greenPlayer = greenPlayer;
        this.redPlayer = redPlayer;
        
        this.localPlayer = greenPlayer;                
        this.isHost = isHost;
        this.isSinglePlayer = isSinglePlayer;
        
        init();
    }
    
    
    /**
     * Creates a new {@link Camera}
     * @param map
     * @return
     */
    private Camera newCamera() {
        Camera camera = new Camera2d();                        
        camera.setViewPort(new Rectangle(app.getScreenWidth(), app.getScreenHeight()));
//        camera.setMovementSpeed(new Vector2f(4000, 4000));
        camera.setMovementSpeed(new Vector2f(130, 130));
                
        return camera;
    }
    
    private void init() {
        this.dispatcher = new EventDispatcher();
        this.random = new Randomizer();    
        this.textureCache = new TextureCache();
        this.camera = newCamera();
        this.gson = new Gson();
//        this.gson = new GsonBuilder().registerTypeAdapter(ActionType.class, new JsonDeserializer<ActionType>() {
//            
//            @Override
//            public ActionType deserialize(JsonElement e, Type type, JsonDeserializationContext context) throws JsonParseException {
//                return ActionType.valueOf(e.getAsString());
//            }
//        }).create();
//        
        int screenWidth = camera.getViewPort().width;
        int screenHeight = camera.getViewPort().height;
        this.mapCamera = new OrthographicCamera(screenWidth, screenHeight);
        this.mapCamera.setToOrtho(false, screenWidth, screenHeight);
        
        this.spriteBatch = new SpriteBatch();
        
        this.protocol = new GameNetworkProtocol(this);
        
        if(isHost()) {
            this.server = new GameServer(this);
            this.server.start(8121);
        }
        
        if(isSinglePlayer()) {
            this.otherPlayer = new AIPlayer(this);
        }
        else {
            this.otherPlayer = new RemotePlayer(this);                    
        }
        
        this.metaGame = new MetaGame(getApp(), this, this.camera);
        
        this.dispatcher.addEventListener(TurnCompletedEvent.class, this.otherPlayer);
        this.dispatcher.addEventListener(BattleEvent.class, otherPlayer);
    }
    
    @Override
    public void update(TimeStep timeStep) {
        this.dispatcher.processQueue();
        if(this.isConnected()) {
            this.connection.update(timeStep);
        }
    }
    
    
    public boolean isConnected() {
        return this.connection != null && this.connection.isConnected();
    }
    
    /**
     * @return the connection
     */
    public PeerConnection getConnection() {
        return connection;
    }
    
    public PeerConnection peerConnection(Session session) {
        if(this.connection!=null) {
            this.connection.close();
        }
        
        this.connection = new PeerConnection(this.protocol, session);
        return this.connection;
    }
    
    public boolean connectToPeer(String uri) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            Session session = container.connectToServer(new WebSocketClient(this), URI.create(uri));
            if(session!=null && session.isOpen()) {
                return true;
            }
        }
        catch(Exception e) {
            Cons.println("*** Unable to connect to remote peer: " + e);
        }
        
        return false;
    }
    
    /**
     * @return the dispatcher
     */
    public EventDispatcher getDispatcher() {
        return dispatcher;
    }
    
    /**
     * @return the mapCamera
     */
    public com.badlogic.gdx.graphics.OrthographicCamera getMapCamera() {
        return mapCamera;
    }
    
    /**
     * @return the spriteBatch
     */
    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }
    
    /**
     * @return the camera
     */
    public Camera getCamera() {
        return camera;
    }
    
    /**
     * @return the isSinglePlayer
     */
    public boolean isSinglePlayer() {
        return isSinglePlayer;
    }
    
    /**
     * @return the isHost
     */
    public boolean isHost() {
        return isHost;
    }
    
    /**
     * @return the random
     */
    public Randomizer getRandom() {
        return random;
    }
        
    
    /**
     * @return the activeGame
     */
    public Game getActiveGame() {
        return activeGame;
    }
    
    /**
     * @return the metaGame
     */
    public MetaGame getMetaGame() {
        return metaGame;
    }
    
    /**
     * @param activeGame the activeGame to set
     */
    public void setActiveGame(Game activeGame) {
        this.activeGame = activeGame;
        if(activeGame instanceof MetaGame) {
            this.otherPlayer.enterMetaGame((MetaGame)activeGame);
        }
        else if(activeGame instanceof BattleGame) {
            this.otherPlayer.enterBattleGame((BattleGame)activeGame);
        }
    }
    
    /**
     * @return the textureCache
     */
    @Override
    public TextureCache getTextureCache() {
        return textureCache;
    }
    
    @Override
    public <T> T loadData(String file, Class<T> type) {
        try {
            String data = JsonValue.readHjson(Gdx.files.internal(file).reader(1024)).toString();
            return gson.fromJson(data, type);
        } catch (IOException e) {
            Cons.println("*** Unable to load '" + file + "' : " + e);
        }

        return null;
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
     * @return the localPlayer
     */
    public Player getLocalPlayer() {
        return localPlayer;
    }

    public Player getAIPlayer() {
        return this.redPlayer;
    }

    /**
     * @return the otherPlayer
     */
    public OtherPlayer getOtherPlayer() {
        return otherPlayer;
    }
    
    /**
     * @return the app
     */
    public FranksGame getApp() {
        return app;
    }
    
    
    public NetGameFullState getNetGameFullState() {
        NetGameFullState net = new NetGameFullState();
        net.greenPlayer = greenPlayer.getNetPlayer();
        net.redPlayer = redPlayer.getNetPlayer();
        
        Turn currentTurn = getActiveGame().getCurrentTurn();
        net.turnNumber = currentTurn.getNumber();
        
        net.seed = this.random.getStartingSeed();
        net.generation = this.random.getIteration();
        
        net.currentPlayersTurn = currentTurn.getActivePlayer()==greenPlayer 
                                    ? ArmyName.Green : ArmyName.Red;
        
        return net;
    }

    @Override
    public void onGameFullState(NetGameFullState state) {        
        this.random = new Randomizer(state.seed, state.generation);
        
        MetaGame game = getMetaGame();
        
        this.greenPlayer.syncFrom(state.greenPlayer);
        for(NetEntity ent : state.greenPlayer.entities) {
            Entity leader = game.buildLeaderEntity(this.greenPlayer.getTeam(), ent);
            greenPlayer.addEntity(leader);
        }
        
        this.redPlayer.syncFrom(state.redPlayer);
        for(NetEntity ent : state.redPlayer.entities) {
            Entity leader = game.buildLeaderEntity(this.redPlayer.getTeam(), ent);
            redPlayer.addEntity(leader);
        }
        
        Player activePlayer = greenPlayer;
        if(state.currentPlayersTurn==ArmyName.Red) {
            activePlayer = redPlayer;
        }
        
        getActiveGame().setTurn(activePlayer, state.turnNumber);
        this.localPlayer = redPlayer;
    }
    
    /* (non-Javadoc)
     * @see franks.game.net.NetworkProtocol#onBattle(franks.game.net.NetBattle)
     */
    @Override
    public void onBattle(NetBattle battle) {
        if(this.otherPlayer instanceof RemotePlayer) {
            ((RemotePlayer)this.otherPlayer).onRemoteBattleMessage(battle);
        }
    }
    
    /* (non-Javadoc)
     * @see franks.game.net.NetworkProtocol#onBattleFinished(franks.game.net.NetBattleFinished)
     */
    @Override
    public void onBattleFinished(NetBattleFinished battle) {
        // TODO Auto-generated method stub
        
    }
    
    
    /* (non-Javadoc)
     * @see franks.game.net.NetworkProtocol#onTurnEnd(franks.game.net.NetTurn)
     */
    @Override
    public void onTurnEnd(NetTurn turn) {
        if(this.otherPlayer instanceof RemotePlayer) {
            ((RemotePlayer)this.otherPlayer).onRemoteTurnEndMessage(turn);
        }
    }
}

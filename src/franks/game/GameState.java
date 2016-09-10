/*
 * see license.txt 
 */
package franks.game;

import java.io.IOException;

import org.hjson.JsonValue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.google.gson.Gson;

import franks.FranksGame;
import franks.util.Cons;

/**
 * Persistent game state
 * 
 * @author Tony
 *
 */
public class GameState implements ResourceLoader {
	public static final int MAX_ENTITIES = 256;
	
	private FranksGame app;
	private Player greenPlayer;
	private Player redPlayer;		
	private Player localPlayer;

	private TextureCache textureCache;
	private Randomizer random;
	private Gson gson;
	
	private boolean isSinglePlayer;
	
	public GameState(FranksGame app) {
		this.app = app;
		
				
		this.redPlayer = new Player("Red Player");
		this.greenPlayer = new Player("Green Player");
		
		this.redPlayer.setTeam(new Team("Red", new Color(0.98f, 0.67f, 0.67f, 0.78f)));
		this.greenPlayer.setTeam(new Team("Green", new Color(0.67f, 0.98f, 0.67f, 0.78f)));
		
		this.localPlayer = this.greenPlayer;
		
		this.isSinglePlayer = true;
		
		init();
	}
	
	/**
	 * @param redPlayer
	 * @param greenPlayer
	 * @param localPlayer
	 * @param isSinglePlayer
	 * @param app
	 */
	public GameState(FranksGame app, Player greenPlayer, Player redPlayer, boolean isSinglePlayer) {
		this.app = app;		
		this.greenPlayer = greenPlayer;
		this.redPlayer = redPlayer;
		
		this.localPlayer = greenPlayer;				
		this.isSinglePlayer = isSinglePlayer;
		
		init();
	}
	
	private void init() {
		this.random = new Randomizer();	
		this.textureCache = new TextureCache();
		this.gson = new Gson();
	}
	
	/**
	 * @return the isSinglePlayer
	 */
	public boolean isSinglePlayer() {
		return isSinglePlayer;
	}
	
	/**
	 * @return the random
	 */
	public Randomizer getRandom() {
		return random;
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
	 * @return the app
	 */
	public FranksGame getApp() {
		return app;
	}
	
	
	

}

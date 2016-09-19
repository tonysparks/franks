/*
 * see license.txt 
 */
package franks.game.meta;

import franks.game.Game;
import franks.game.Player;
import franks.game.entity.Entity;
import franks.util.TimeStep;
import franks.util.Timer;
import franks.util.Updatable;

/**
 * The Fog of War hides unvisited tiles from the player
 * 
 * @author Tony
 *
 */
public class FogOfWar implements Updatable {

	private Timer visibilityCheckTimer;
	private Game game;
	
	/**
	 * 
	 */
	public FogOfWar(Game game) {
		this.game = game;
		this.visibilityCheckTimer = new Timer(true, 200);
	}
	
	@Override
	public void update(TimeStep timeStep) {
		visibilityCheckTimer.update(timeStep);
		
		if(this.visibilityCheckTimer.isOnFirstTime()) {
			game.getWorld().updateVisibility();
			Player localPlayer = game.getLocalPlayer();
			
			for(Entity ent : game.getEntities()) {
				if(localPlayer.owns(ent)) {
					ent.visitTiles(game.getMap());
				}
			}
		}
		
		
	}

}

/*
 * see license.txt 
 */
package franks.game.meta;

import franks.FranksGame;
import franks.game.Game;
import franks.game.GameState;
import franks.game.Team;
import franks.game.World;
import franks.game.entity.Entity;
import franks.game.entity.EntityGroupData.EntityInstanceData;
import franks.gfx.Camera;
import franks.gfx.Canvas;

/**
 * @author Tony
 *
 */
public class MetaGame extends Game {

	/**
	 * 
	 */
	public MetaGame(FranksGame app, GameState state, Camera camera) {
		super(app, state, camera);
		
	}

	@Override
	protected World createWorld(GameState state) {	
		World world = new World(this, camera);
		
		
		return world;
	}
	
	
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {
		this.world.render(canvas, camera, alpha);
		
//		this.hud.renderUnderEntities(canvas, camera, alpha);
		this.entities.render(canvas, camera, alpha);
		this.world.renderOverEntities(canvas, camera, alpha);
		
//		this.hud.render(canvas, camera, alpha);
	}
}

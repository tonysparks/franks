/*
 * see license.txt 
 */
package franks.game.entity;

import franks.game.Game;
import franks.game.action.CollectResourceCommand2;
import franks.game.action.MovementCommand;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.math.Vector2f;
import franks.util.TimeStep;

/**
 * @author Tony
 *
 */
public class DataEntity extends Entity {

	private EntityModel model;
	
	/**
	 * 
	 */
	public DataEntity(Game game, EntityData data) {
		super(game, data.type, new Vector2f(), data.width, data.height);
		this.model = new EntityModel(game, this, data.graphics);
		
		if(data.availableActions!=null) {
			data.availableActions.forEach(action -> {
				switch(action.action) {
					case "movement": {
						addAvailableAction(new MovementCommand(game, this, ((Double)action.params.get("movementSpeed")).intValue() ));
						break;
					}
					case "collect": {
						addAvailableAction(new CollectResourceCommand2(action.params.get("resource").toString(), this));
						break;
					}
				}
			});
		}
		
	}
	
	@Override
	public void update(TimeStep timeStep) {	
		super.update(timeStep);
		model.update(timeStep);
	}
	
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {
		model.render(canvas, camera, alpha);
	}

}

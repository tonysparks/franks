/*
 * see license.txt 
 */
package franks.game.entity;

import franks.game.Game;
import franks.game.action.AttackCommand;
import franks.game.action.CollectResourceCommand2;
import franks.game.action.DieCommand;
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
		super(game, data.name, data.type, new Vector2f(), data.width, data.height);
		this.model = new EntityModel(game, this, data.graphics);
		
		this.health = data.getNumber("health", 5D).intValue();
		
		if(data.availableActions!=null) {
			data.availableActions.forEach(action -> {
				switch(action.action) {
					case "movement": {
						addAvailableAction(new MovementCommand(game, this, action.getNumber("movementSpeed", 50D).intValue() ));
						break;
					}
					case "collect": {
						addAvailableAction(new CollectResourceCommand2(action.getStr("resource", "wood"), this));
						break;
					}
					case "attack": {
						addAvailableAction(new AttackCommand(this, action.getNumber("cost", 1D).intValue(),
								action.getNumber("attackDistance", 1D).intValue(),
								action.getNumber("attackPoints", 50D).intValue()) );
						break;
					}
					case "die" : {
						addAvailableAction(new DieCommand(this));
						break;
					}
				}
			});
		}
		
	}
	
	
	
	/* (non-Javadoc)
	 * @see franks.game.entity.Entity#setCurrentState(franks.game.entity.Entity.State)
	 */
	@Override
	public void setCurrentState(State currentState) {	
		super.setCurrentState(currentState);
		this.model.resetAnimation();
	}
	
	@Override
	public void update(TimeStep timeStep) {	
		super.update(timeStep);
		model.update(timeStep);
	}
	
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {
		model.render(canvas, camera, alpha);
		
		if(getType().equals(Type.HUMAN)) {
			Vector2f pos = getRenderPosition(camera, alpha);
			int health = getHealth();
			for(int i = 0; i < health; i++) {
				canvas.drawString("*", pos.x+23 + (i*10), pos.y+68, 0xffffffff);
			}
		}
	}

}

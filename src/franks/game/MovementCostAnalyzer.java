/*
 * see license.txt 
 */
package franks.game;

import franks.game.entity.Entity;
import franks.util.TimeStep;
import franks.util.Timer;
import franks.util.Updatable;

/**
 * @author Tony
 *
 */
public class MovementCostAnalyzer implements Updatable {

	private Game game;
	private Timer calculateMovementCostTimer;
	private boolean movementAllowed;
	
	/**
	 * 
	 */
	public MovementCostAnalyzer(Game game) {
		this.game = game;
		this.calculateMovementCostTimer = new Timer(true, 150);
	}

	/* (non-Javadoc)
	 * @see franks.util.Updatable#update(franks.util.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		this.calculateMovementCostTimer.update(timeStep);
		
		Entity selectedEntity = game.getSelectedEntity();
		if(selectedEntity != null && this.calculateMovementCostTimer.isTime()) {			
			int moves = selectedEntity.getMeter().remaining();				

			this.movementAllowed = false;
			
			int distance = selectedEntity.calculateMovementCost(game.getMouseWorldPos());
			if(distance > 0 && distance <= moves && moves > 0) {
				this.movementAllowed = true;
			}
			
		}		
	}
	
	public boolean isMovementAllowed() {
		return this.movementAllowed;
	}
}

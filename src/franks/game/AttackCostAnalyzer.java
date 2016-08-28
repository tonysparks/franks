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
public class AttackCostAnalyzer implements Updatable {

	private Game game;
	private Timer calculateMovementCostTimer;
	private boolean validAttack;
	
	/**
	 * 
	 */
	public AttackCostAnalyzer(Game game) {
		this.game = game;
		this.calculateMovementCostTimer = new Timer(true, 150);
		
	}

	@Override
	public void update(TimeStep timeStep) {
		this.calculateMovementCostTimer.update(timeStep);
		
		Entity selectedEntity = game.getSelectedEntity();
		if(selectedEntity != null && this.calculateMovementCostTimer.isTime()) {			
			int moves = selectedEntity.getMeter().remaining();				

			this.validAttack = false;
			
			Entity enemy = game.getEntityOverMouse();
			if(enemy!=null && !selectedEntity.isTeammate(enemy)) {
				int attackCost = selectedEntity.calculateAttackCost(enemy);
				if(attackCost > 0 && moves > 0) {
					if( (moves - attackCost) > 0 ) {
						this.validAttack = true;
					}
				}
			}
			
		}		
	}
	
	public boolean isValidAttack() {
		return this.validAttack;
	}	
}

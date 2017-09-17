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
    private int cost;
    
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
        if(selectedEntity != null && this.calculateMovementCostTimer.isOnFirstTime()) {            
            int moves = selectedEntity.getMeter().remaining();                

            this.movementAllowed = false;
            
            if(!game.hoveringOverEntity()) {
                cost = selectedEntity.calculateMovementCost(game.getCursorTilePos());
                
                if(cost > 0 && cost <= moves && moves > 0) {
                    this.movementAllowed = true;
                }
            }
            
        }        
    }
    
    /**
     * @return the actionPoints
     */
    public int getCost() {
        return cost;
    }
    
    public boolean isMovementAllowed() {
        return this.movementAllowed;
    }
}

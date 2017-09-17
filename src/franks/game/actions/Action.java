/*
 * see license.txt 
 */
package franks.game.actions;

import franks.game.Game;
import franks.game.PreconditionResponse;
import franks.game.entity.Entity;

/**
 * @author Tony
 *
 */
public abstract class Action {

    private Entity entity;
    private ActionType type;
    private int actionCost;
    
    
    /**
     * 
     */
    public Action(ActionType type, int actionCost, Entity entity) {
        this.type = type;
        this.actionCost = actionCost;
        this.entity = entity;
                
    }
    
    /**
     * @param actionCost the movementCost to set
     */
    protected void setActionCost(int actionCost) {
        this.actionCost = actionCost;
    }
    
    
    /**
     * @return the entityType
     */
    public ActionType getType() {
        return type;
    }
    
    
    /**
     * @return the actionCost
     */
    public int getActionCost() {
        return actionCost;
    }
    
    /**
     * @return the entity
     */
    public Entity getEntity() {
        return entity;
    }
    
    /**
     * The display name of this {@link Action}
     * 
     * @return The display name of this {@link Action}
     */
    public String getDisplayName() {
        return this.type.name();
    }
    
    protected PreconditionResponse newResponse(Game game) {
        PreconditionResponse response = new PreconditionResponse();
        checkCost(response, game);
        return response;
    }
    
    protected void checkCost(PreconditionResponse response, Game game) {        
        if(!entity.getMeter().hasEnough(getActionCost())) {
            response.addFailure("Not enough movement points");
        }        
    }
    
    public abstract PreconditionResponse checkPreconditions(Game game, Command request);
    protected abstract ExecutedAction doActionImpl(Game game, Command request);
    
    public ExecutedAction doAction(Game game, Command command) {
        getEntity().getMeter().decrementBy(getActionCost());
        return doActionImpl(game, command);
    }

    
}

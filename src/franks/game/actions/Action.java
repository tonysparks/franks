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

	public static enum ActionType {
		Move,
		Attack,
		Die,
		
		;		
	}
	
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
	 * @return the type
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
	
	public ExecutedAction doAction(Game game, Command request) {
		getEntity().getMeter().decrementBy(getActionCost());
		return doActionImpl(game, request);
	}

	
}

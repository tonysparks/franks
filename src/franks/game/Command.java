/*
 * see license.txt 
 */
package franks.game;

import franks.game.CommandQueue.CommandRequest;
import franks.game.entity.Entity;

/**
 * @author Tony
 *
 */
public abstract class Command {

	private Entity entity;
	private String name;
	private int actionCost;
	
	
	/**
	 * 
	 */
	public Command(String name, int actionCost, Entity entity) {
		this.name = name;
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
	 * @return the name
	 */
	public String getName() {
		return name;
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
	
	public abstract PreconditionResponse checkPreconditions(Game game, CommandRequest request);
	protected abstract CommandAction doActionImpl(Game game, CommandRequest request);
	
	public CommandAction doAction(Game game, CommandRequest request) {
		getEntity().getMeter().decrementBy(getActionCost());
		return doActionImpl(game, request);
	}

}

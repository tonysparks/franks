/*
 * see license.txt 
 */
package franks.game.commands;

import franks.game.Game;
import franks.game.PreconditionResponse;
import franks.game.commands.CommandQueue.CommandRequest;
import franks.game.entity.Entity;

/**
 * @author Tony
 *
 */
public abstract class Command {

	public static enum CommandType {
		Move,
		Attack,
		Die,
		
		;		
	}
	
	private Entity entity;
	private CommandType type;
	private int actionCost;
	
	
	/**
	 * 
	 */
	public Command(CommandType type, int actionCost, Entity entity) {
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
	public CommandType getType() {
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
	
	public abstract PreconditionResponse checkPreconditions(Game game, CommandRequest request);
	protected abstract CommandAction doActionImpl(Game game, CommandRequest request);
	
	public CommandAction doAction(Game game, CommandRequest request) {
		getEntity().getMeter().decrementBy(getActionCost());
		return doActionImpl(game, request);
	}

	
}

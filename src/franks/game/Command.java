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
	private int movementCost;
	/**
	 * 
	 */
	public Command(String name, int movementCost, Entity entity) {
		this.name = name;
		this.movementCost = movementCost;
		this.entity = entity;
				
	}
	
	/**
	 * @param movementCost the movementCost to set
	 */
	protected void setMovementCost(int movementCost) {
		this.movementCost = movementCost;
	}
	
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the movementCost
	 */
	public int getMovementCost() {
		return movementCost;
	}
	
	/**
	 * @return the entity
	 */
	public Entity getEntity() {
		return entity;
	}
	
	protected PreconditionResponse newResponse(Game game) {
		PreconditionResponse response = new PreconditionResponse();
		checkMovement(response, game);
		return response;
	}
	
	protected void checkMovement(PreconditionResponse response, Game game) {		
		if(!entity.getMeter().hasEnough(getMovementCost())) {
			response.addFailure("Not enough movement points");
		}		
	}
	
	public abstract PreconditionResponse checkPreconditions(Game game, CommandRequest request);
	protected abstract CommandAction doActionImpl(Game game, CommandRequest request);
	
	public CommandAction doAction(Game game, CommandRequest request) {
		getEntity().getMeter().decrementMovement(getMovementCost());
		return doActionImpl(game, request);
	}

}

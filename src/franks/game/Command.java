/*
 * see license.txt 
 */
package franks.game;

import franks.game.CommandQueue.CommandRequest;

/**
 * @author Tony
 *
 */
public abstract class Command {

	private String name;
	private int movementCost;
	/**
	 * 
	 */
	public Command(String name, int movementCost) {
		this.name = name;
		this.movementCost = movementCost;
				
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
	
	protected PreconditionResponse newResponse(Game game) {
		PreconditionResponse response = new PreconditionResponse();
		checkMovement(response, game);
		return response;
	}
	
	protected void checkMovement(PreconditionResponse response, Game game) {		
		if(!game.getMoveMeter().hasEnough(getMovementCost())) {
			response.addFailure("Not enough movement points");
		}		
	}
	
	public abstract PreconditionResponse checkPreconditions(Game game, CommandRequest request);
	public abstract CommandAction doAction(Game game, CommandRequest request);

}

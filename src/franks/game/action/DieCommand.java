/*
 * see license.txt 
 */
package franks.game.action;

import franks.game.Game;
import franks.game.PreconditionResponse;
import franks.game.commands.Command;
import franks.game.commands.CommandAction;
import franks.game.commands.CommandQueue.CommandRequest;
import franks.game.entity.Entity;
import franks.game.entity.Entity.State;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.util.TimeStep;
import franks.util.Timer;

/**
 * @author Tony
 *
 */
public class DieCommand extends Command {

	public DieCommand(Entity entity) {
		super(CommandType.Die,  0, entity);		
	}

	/* (non-Javadoc)
	 * @see franks.game.Command#checkPreconditions(franks.game.Game, franks.game.CommandQueue.CommandRequest)
	 */
	@Override
	public PreconditionResponse checkPreconditions(Game game, CommandRequest request) {
		PreconditionResponse response = new PreconditionResponse();
		
		if(!getEntity().canDo(getType())) {
			response.addFailure("This entity can not die");
		}
		
		checkCost(response, game);
		return response;
	}

	
	@Override
	protected CommandAction doActionImpl(Game game, CommandRequest request) {			
		return new CommandAction() {
			
			Timer timer = new Timer(false, getEntity().getData().getAnimationTime(State.DEAD));
			
			@Override
			public CommandAction start() {
				timer.start();
				getEntity().setCurrentState(State.DEAD);
				return this;
			}
			
			@Override
			public CommandAction end() {
				getEntity().delete();
				// TODO: Once a 'map' is over, clean up the 
				// dead bodies???
				return this;
			}
			
			@Override
			public void update(TimeStep timeStep) {		
				timer.update(timeStep);				
			}
			
			@Override
			public void render(Canvas canvas, Camera camera, float alpha) {
			}
			
			@Override
			public CompletionState getCurrentState() {
				return timer.isTime() ? CompletionState.Success : CompletionState.InProgress;
			}
			
			@Override
			public void cancel() {
				timer.setEndTime(0);
			}
		};
	}

	
}

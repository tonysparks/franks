/*
 * see license.txt 
 */
package franks.game.action;

import franks.game.Command;
import franks.game.CommandAction;
import franks.game.CommandQueue.CommandRequest;
import franks.game.Game;
import franks.game.PreconditionResponse;
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


	private Entity entity;

	public DieCommand(Entity entity) {
		super("die",  0);
		this.entity = entity;
	}

	/* (non-Javadoc)
	 * @see franks.game.Command#checkPreconditions(franks.game.Game, franks.game.CommandQueue.CommandRequest)
	 */
	@Override
	public PreconditionResponse checkPreconditions(Game game, CommandRequest request) {
		PreconditionResponse response = new PreconditionResponse();
		
		if(!entity.canDo(getName())) {
			response.addFailure("This entity can not die");
		}
		
		checkMovement(response, game);
		return response;
	}

	/* (non-Javadoc)
	 * @see franks.game.Command#doAction(franks.game.Game, franks.game.CommandQueue.CommandRequest)
	 */
	@Override
	public CommandAction doAction(Game game, CommandRequest request) {			
		return new CommandAction() {
			
			Timer timer = new Timer(false, 13*120);
			
			@Override
			public CommandAction start() {
				timer.start();
				entity.setCurrentState(State.DEAD);
				return this;
			}
			
			@Override
			public CommandAction end() {
				//entity.delete();
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

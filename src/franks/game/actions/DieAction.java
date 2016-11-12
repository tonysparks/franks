/*
 * see license.txt 
 */
package franks.game.actions;

import franks.game.Game;
import franks.game.PreconditionResponse;
import franks.game.entity.Entity;
import franks.game.entity.Entity.State;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.sfx.Sounds;
import franks.util.TimeStep;
import franks.util.Timer;

/**
 * @author Tony
 *
 */
public class DieAction extends Action {

	public DieAction(Entity entity) {
		super(ActionType.Die,  0, entity);		
	}

	/* (non-Javadoc)
	 * @see franks.game.Command#checkPreconditions(franks.game.Game, franks.game.CommandQueue.CommandRequest)
	 */
	@Override
	public PreconditionResponse checkPreconditions(Game game, Command command) {
		PreconditionResponse response = new PreconditionResponse();
		
		if(!getEntity().canDo(getType())) {
			response.addFailure("This entity can not die");
		}
		
		checkCost(response, game);
		return response;
	}

	
	@Override
	protected ExecutedAction doActionImpl(Game game, Command command) {			
		return new ExecutedAction(command) {
			
			Timer timer = new Timer(false, getEntity().getData().getAnimationTime(State.DEAD));
			
			@Override
			public ExecutedAction start() {
				timer.start();
				getEntity().setCurrentState(State.DEAD);
				Sounds.playGlobalSound(Sounds.die);
				return this;
			}
			
			@Override
			public ExecutedAction end() {
				getEntity().delete();
				// TODO: Once a 'map' is over, clean up the 
				// dead bodies???
				return super.end();
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

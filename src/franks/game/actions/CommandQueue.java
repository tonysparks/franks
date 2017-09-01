/*
 * see license.txt 
 */
package franks.game.actions;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

import franks.game.Game;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.gfx.Renderable;
import franks.util.TimeStep;

/**
 * @author Tony
 *
 */
public class CommandQueue implements Renderable {

	private Queue<Command> queue;
	private Optional<ExecutedAction> currentAction;
	private Game game;
	
	/**
	 * 
	 */
	public CommandQueue(Game game) {
		this.game = game;
		this.queue = new LinkedList<>();
		this.currentAction = Optional.empty();
	}
	
	public CommandQueue add(Command cmd) {
		this.queue.add(cmd);
		return this;
	}
	
	public Optional<ExecutedAction> getCurrentAction() {
		return this.currentAction;
	}
	
	public CommandQueue clear() {
		this.queue.clear();
		return this;
	}
	
	public boolean isEmpty() {
		if(this.queue.isEmpty()) {
			if(currentAction.isPresent()) {
				ExecutedAction action = currentAction.get();
				return action.hasEnded() || action.spansTurns();
			}
			return true;
		}
		return false;
	}
	
	public CommandQueue cancel() {
		currentAction.ifPresent(action -> action.cancel());
		return clear();		
	}
	
	@Override
	public void update(TimeStep timeStep) {
		if(currentAction.isPresent()) {
			ExecutedAction action = currentAction.get();
			if(action.getCurrentState().isCompleted()) {
				action.end();
				 
				game.recordCommand(action.getCommand());
				
				currentAction = Optional.empty();
			}
			else {
				action.update(timeStep);
			}
		}
		
		
		if(!currentAction.isPresent()) {
			if(!queue.isEmpty()) {
				Command request = queue.poll();
				currentAction = request.executeRequest(game);				
			}
		}
	}	
	
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {
	    if(currentAction.isPresent()) {
	        currentAction.get().render(canvas, camera, alpha);
	    }
	}
}

/*
 * see license.txt 
 */
package newera.game;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

import newera.game.CommandAction.CompletionState;
import newera.game.entity.Entity;
import newera.gfx.Camera;
import newera.gfx.Canvas;
import newera.gfx.Renderable;
import newera.util.TimeStep;

/**
 * @author Tony
 *
 */
public class CommandQueue implements Renderable {

	public static class CommandRequest {
		public Optional<Entity> selectedEntity;
		public Optional<Entity> targetEntity;
		public String action;
		
		public CommandRequest(Game game, String action) {
			this.action = action;
			this.selectedEntity = Optional.ofNullable(game.getSelectedEntity().orElse(null));
			this.targetEntity = Optional.ofNullable(game.getEntityOverMouse());
		}
	}
	
	private Queue<CommandRequest> queue;
	private Optional<CommandAction> currentAction;
	private Game game;
	
	/**
	 * 
	 */
	public CommandQueue(Game game) {
		this.game = game;
		this.queue = new LinkedList<>();
		this.currentAction = Optional.empty();
	}
	
	public CommandQueue add(CommandRequest cmd) {
		this.queue.add(cmd);
		return this;
	}
	
	
	
	public CommandQueue clear() {
		this.queue.clear();
		return this;
	}
	
	public CommandQueue cancel() {
		currentAction.ifPresent(action -> action.cancel());
		return clear();		
	}
	
	@Override
	public void update(TimeStep timeStep) {
		if(currentAction.isPresent()) {
			CommandAction action = currentAction.get();
			if(action.getCurrentState() != CompletionState.InProgress) {
				currentAction = Optional.empty();
			}
			else {
				action.update(timeStep);
			}
		}
		
		
		if(!currentAction.isPresent()) {
			if(!queue.isEmpty()) {
				CommandRequest request = queue.poll();
				game.executeCommandRequest(request);
			}
		}
	}
	
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {
		
	}

}

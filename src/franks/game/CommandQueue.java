/*
 * see license.txt 
 */
package franks.game;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

import franks.game.CommandAction.CompletionState;
import franks.game.entity.Entity;
import franks.util.TimeStep;
import franks.util.Updatable;

/**
 * @author Tony
 *
 */
public class CommandQueue implements Updatable {

	public static class CommandRequest {
		public Optional<Entity> selectedEntity;
		public Optional<Entity> targetEntity;
		public String action;
		
		public CommandRequest(Game game, String action) {
			this(game, action, game.getSelectedEntity().orElse(null), game.getEntityOverMouse());
		}
		
		public CommandRequest(Game game, String action, Entity selectedEntity) {
			this(game, action, selectedEntity, game.getEntityOverMouse());
		}
		
		public CommandRequest(Game game, String action, Entity selectedEntity, Entity targetEntity) {
			this.action = action;
			this.selectedEntity = Optional.ofNullable(selectedEntity);
			this.targetEntity = Optional.ofNullable(targetEntity);
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
	
	public Optional<CommandAction> getCurrentAction() {
		return this.currentAction;
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
				action.end();
				
				currentAction = Optional.empty();
			}
			else {
				action.update(timeStep);
			}
		}
		
		
		if(!currentAction.isPresent()) {
			if(!queue.isEmpty()) {
				CommandRequest request = queue.poll();
				currentAction = game.executeCommandRequest(request);
				if(currentAction.isPresent())
					System.out.println("executing: " + request.action);
			}
		}
	}	
}

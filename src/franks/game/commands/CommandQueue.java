/*
 * see license.txt 
 */
package franks.game.commands;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

import franks.game.Game;
import franks.game.PreconditionResponse;
import franks.game.commands.Command.CommandType;
import franks.game.entity.Entity;
import franks.game.net.NetCommandRequest;
import franks.math.Vector2f;
import franks.util.Cons;
import franks.util.TimeStep;
import franks.util.Updatable;

/**
 * @author Tony
 *
 */
public class CommandQueue implements Updatable {

	public static class CommandRequest {
		public Entity selectedEntity;
		public Optional<Entity> targetEntity;
		public Vector2f cursorTilePos;
		public CommandType type;
		
		public CommandRequest(Game game, NetCommandRequest request) {
			this(game, request.type, game.getEntityById(request.selectedEntityId), game.getEntityById(request.targetEntityId), request.cursorTilePos);
		}
		
		public CommandRequest(Game game, CommandType type, Entity selectedEntity) {
			this(game, type, selectedEntity, game.getEntityOverMouse(), game.getCursorTilePos()!=null ? game.getCursorTilePos().createClone():new Vector2f());
		}
		
		public CommandRequest(Game game, CommandType type, Entity selectedEntity, Entity targetEntity, Vector2f cursorTilePos) {
			this.type = type;
			this.selectedEntity = selectedEntity;
			this.targetEntity = Optional.ofNullable(targetEntity);
			this.cursorTilePos = cursorTilePos;
		}
		
		/**
		 * Attempts to execute this {@link CommandRequest}
		 * @param game
		 * @return the built {@link CommandAction}
		 */
		public Optional<CommandAction> executeRequest(Game game) {
			return Optional.ofNullable(selectedEntity).flatMap(ent -> ent.getCommand(type))
					 								  .filter(cmd -> {
					 									 PreconditionResponse response = cmd.checkPreconditions(game, this);
					 									 for(String msg : response.getFailureReasons()) {
					 										 Cons.println(msg);
					 									 }
					 									 return response.isMet();
					 								  })
					 								  .map(cmd -> cmd.doAction(game, this).start());
		}
		
		public NetCommandRequest getNetCommandRequest() {
			NetCommandRequest cmd = new NetCommandRequest();
			cmd.type = type;
			cmd.cursorTilePos = cursorTilePos.createClone();
			cmd.selectedEntityId = selectedEntity.getId();
			if(targetEntity.isPresent()) {
				cmd.targetEntityId = targetEntity.get().getId();
			}
			else {
				cmd.targetEntityId = -1;
			}
			return cmd;
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
	
	public boolean isEmpty() {
		if(this.queue.isEmpty()) {
			if(currentAction.isPresent()) {
				CommandAction action = currentAction.get();
				return action.hasEnded();
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
			CommandAction action = currentAction.get();
			if(action.getCurrentState().isCompleted()) {
				action.end();
				
				game.addCommandRequestToHistory(action.getRequest());
				
				currentAction = Optional.empty();
			}
			else {
				action.update(timeStep);
			}
		}
		
		
		if(!currentAction.isPresent()) {
			if(!queue.isEmpty()) {
				CommandRequest request = queue.poll();
				currentAction = request.executeRequest(game);				
			}
		}
	}	
}

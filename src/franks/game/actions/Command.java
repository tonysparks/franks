/*
 * see license.txt 
 */
package franks.game.actions;

import java.util.Optional;

import franks.game.Game;
import franks.game.PreconditionResponse;
import franks.game.actions.Action.ActionType;
import franks.game.entity.Entity;
import franks.game.net.NetCommand;
import franks.math.Vector2f;
import franks.util.Cons;

/**
 * @author Tony
 *
 */
public class Command {
	public Entity selectedEntity;
	public Optional<Entity> targetEntity;
	public Vector2f cursorTilePos;
	public ActionType type;
	
	public Command(Game game, NetCommand request) {
		this(game, request.type, game.getEntityById(request.selectedEntityId), game.getEntityById(request.targetEntityId), request.cursorTilePos);
	}
	
	public Command(Game game, ActionType type, Entity selectedEntity) {
		this(game, type, selectedEntity, game.getEntityOverMouse(), game.getCursorTilePos()!=null ? game.getCursorTilePos().createClone():new Vector2f());
	}
	
	public Command(Game game, ActionType type, Entity selectedEntity, Entity targetEntity) {
		this(game, type, selectedEntity, targetEntity, new Vector2f());
	}
	
	public Command(Game game, ActionType type, Entity selectedEntity, Entity targetEntity, Vector2f cursorTilePos) {
		this.type = type;
		this.selectedEntity = selectedEntity;
		this.targetEntity = Optional.ofNullable(targetEntity);
		this.cursorTilePos = cursorTilePos;
	}
	
	/**
	 * Attempts to execute this {@link CommandRequest}
	 * @param game
	 * @return the built {@link ExecutedAction}
	 */
	public Optional<ExecutedAction> executeRequest(Game game) {
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
	
	public NetCommand asNetCommand() {
		NetCommand cmd = new NetCommand();
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

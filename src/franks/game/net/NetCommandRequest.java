/*
 * see license.txt 
 */
package franks.game.net;

import franks.game.Game;
import franks.game.commands.Command.CommandType;
import franks.game.commands.CommandQueue.CommandRequest;
import franks.game.entity.Entity;
import franks.game.entity.EntityList;
import franks.math.Vector2f;

/**
 * @author Tony
 *
 */
public class NetCommandRequest {

	public CommandType type;
	public int selectedEntityId;
	public int targetEntityId;
	public Vector2f cursorTilePos;
	
	
	public Entity dispatchRequest(Game game) {
		EntityList entities = game.getEntities();
		Entity ent = entities.getEntity(selectedEntityId);
		if(ent!=null) {
			ent.queueAction(new CommandRequest(game, this));
		}
		return ent;
	}
}

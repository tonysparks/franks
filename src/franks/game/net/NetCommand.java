/*
 * see license.txt 
 */
package franks.game.net;

import franks.game.Game;
import franks.game.actions.ActionType;
import franks.game.actions.Command;
import franks.game.entity.Entity;
import franks.game.entity.EntityList;
import franks.math.Vector2f;

/**
 * @author Tony
 *
 */
public class NetCommand {

    public ActionType type;
    public int selectedEntityId;
    public int targetEntityId;
    public Vector2f cursorTilePos;
    public Vector2f cursorPos;
    
    
    public Entity dispatchCommand(Game game) {
        EntityList entities = game.getEntities();
        Entity ent = entities.getEntity(selectedEntityId);
        if(ent!=null) {
            ent.queueAction(new Command(game, this));
        }
        return ent;
    }
}

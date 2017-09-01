/*
 * see license.txt 
 */
package franks.game.entity.meta;

import franks.game.Army;
import franks.game.Game;
import franks.game.entity.Entity;
import franks.game.entity.EntityData;

/**
 * Can build buildings
 * 
 * @author Tony
 *
 */
public class WorkerEntity extends Entity {

    /**
     * @param id
     * @param game
     * @param army
     * @param data
     */
    public WorkerEntity(int id, Game game, Army army, EntityData data) {
        super(id, game, army, data);        
    }

}

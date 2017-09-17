/*
 * see license.txt 
 */
package franks.game.entity;

import java.util.ArrayList;
import java.util.List;

import franks.game.Game;
import franks.game.Army;

/**
 * A group of {@link Entity}'s
 * 
 * @author Tony
 *
 */
public class EntityGroupData {

    /**
     * The entity instance specifies the entityType of entity to create and
     * position
     * 
     * @author Tony
     *
     */
    public static class EntityInstanceData {
        public String dataFile;
        public int x;
        public int y;
        
        public Direction direction=Direction.SOUTH_EAST;
    }
    
    public List<EntityInstanceData> entities;    
    
    
    /**
     * Build the entities from this {@link EntityGroupData}
     * 
     * @param army
     * @param game
     * @return the list of {@link Entity}s
     */
    public List<Entity> buildEntities(EntityList list, Army army, Game game) {
        List<Entity> result = new ArrayList<>();
        if(entities!=null) {
            for(EntityInstanceData ref : entities) {
                result.add(game.buildEntity(list, army, ref));
            }
        }
        
        return result;
    }
}

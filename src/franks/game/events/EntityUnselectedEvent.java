/*
 * see license.txt 
 */
package franks.game.events;

import franks.game.entity.Entity;
import leola.frontend.listener.Event;

/**
 * @author Tony
 *
 */
public class EntityUnselectedEvent extends Event {

    private Entity unselectedEntity;
    
    /**
     * 
     * @param source
     * @param selectedEntity
     */
    public EntityUnselectedEvent(Object source, Entity unselectedEntity) {
        super(source);
        this.unselectedEntity = unselectedEntity;
    }
    
    /**
     * @return the selectedEntity
     */
    public Entity getUnselectedEntity() {
        return unselectedEntity;
    }

}

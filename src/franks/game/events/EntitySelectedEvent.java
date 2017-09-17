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
public class EntitySelectedEvent extends Event {

    private Entity selectedEntity;
    
    /**
     * 
     * @param source
     * @param selectedEntity
     */
    public EntitySelectedEvent(Object source, Entity selectedEntity) {
        super(source);
        this.selectedEntity = selectedEntity;
    }
    
    /**
     * @return the selectedEntity
     */
    public Entity getSelectedEntity() {
        return selectedEntity;
    }

}

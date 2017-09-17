/*
 * see license.txt 
 */
package franks.game.events;

import franks.game.entity.Entity;
import leola.frontend.listener.EventListener;
import leola.frontend.listener.EventMethod;

/**
 * An {@link Entity} was unselected
 * 
 * @author Tony
 *
 */
@FunctionalInterface
public interface EntityUnselectedListener extends EventListener {

    @EventMethod
    public void onUnselected(EntityUnselectedEvent event);
}

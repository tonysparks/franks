/*
 * see license.txt 
 */
package franks.game.events;

import franks.game.entity.Entity;
import leola.frontend.listener.EventListener;
import leola.frontend.listener.EventMethod;

/**
 * An {@link Entity} was selected
 * 
 * @author Tony
 *
 */
@FunctionalInterface
public interface EntitySelectedListener extends EventListener {

    @EventMethod
    public void onSelected(EntitySelectedEvent event);
}

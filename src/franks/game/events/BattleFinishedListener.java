/*
 * see license.txt 
 */
package franks.game.events;

import leola.frontend.listener.EventListener;
import leola.frontend.listener.EventMethod;

/**
 * A Turn has completed
 * 
 * @author Tony
 *
 */
@FunctionalInterface
public interface BattleFinishedListener extends EventListener {

    @EventMethod
    public void onTurnCompleted(TurnCompletedEvent event);
}

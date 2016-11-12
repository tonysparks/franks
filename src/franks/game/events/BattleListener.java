/*
 * see license.txt 
 */
package franks.game.events;

import leola.frontend.listener.EventListener;
import leola.frontend.listener.EventMethod;

/**
 * A battle has started
 * 
 * @author Tony
 *
 */
public interface BattleListener extends EventListener {

	@EventMethod
	public void onBattle(BattleEvent event);
}

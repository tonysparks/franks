/*
 * see license.txt 
 */
package franks.game.events;

import franks.game.battle.Battle;
import leola.frontend.listener.Event;

/**
 * @author Tony
 *
 */
public class BattleEvent extends Event {

    private Battle battle;
    
    /**
     * 
     * @param source
     * @param battle
     */
    public BattleEvent(Object source, Battle battle) {
        super(source);
        this.battle = battle;
    }
    
    /**
     * @return the battle
     */
    public Battle getBattle() {
        return battle;
    }
}

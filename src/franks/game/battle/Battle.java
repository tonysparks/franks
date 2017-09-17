/*
 * see license.txt 
 */
package franks.game.battle;

import franks.game.entity.Entity;

/**
 * @author Tony
 *
 */
public class Battle {

    private Entity attacker;
    private Entity defender;
    /**
     * @param attacker
     * @param defender
     */
    public Battle(Entity attacker, Entity defender) {
        super();
        this.attacker = attacker;
        this.defender = defender;
    }        
    
    /**
     * @return the attacker
     */
    public Entity getAttacker() {
        return attacker;
    }
    
    /**
     * @return the defender
     */
    public Entity getDefender() {
        return defender;
    }

}

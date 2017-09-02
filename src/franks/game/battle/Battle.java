/*
 * see license.txt 
 */
package franks.game.battle;

import franks.game.entity.meta.LeaderEntity;

/**
 * @author Tony
 *
 */
public class Battle {

    private LeaderEntity attacker;
    private LeaderEntity defender;
    /**
     * @param attacker
     * @param defender
     */
    public Battle(LeaderEntity attacker, LeaderEntity defender) {
        super();
        this.attacker = attacker;
        this.defender = defender;
    }        
    
    /**
     * @return the attacker
     */
    public LeaderEntity getAttacker() {
        return attacker;
    }
    
    /**
     * @return the defender
     */
    public LeaderEntity getDefender() {
        return defender;
    }

}

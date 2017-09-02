/*
 * see license.txt 
 */
package franks.game.net;

import java.util.List;

/**
 * @author Tony
 *
 */
public class NetBattle {
    
    /**
     * Any commands executed before this battle
     * ensued
     */
    public List<NetCommand> commands;
    
    public int attackerLeaderId;
    public int defenderLeaderId;
    
    public NetBattle() {
    }

}

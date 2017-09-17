/*
 * see license.txt 
 */
package franks.game;

import franks.game.battle.BattleGame;
import franks.game.events.TurnCompletedListener;
import franks.game.meta.MetaGame;
import franks.util.Updatable;

/**
 * @author Tony
 *
 */
public abstract class OtherPlayer implements Updatable, TurnCompletedListener {
    private Player player;
    
    protected OtherPlayer(Player player) {
        this.player = player;
    }
    
    /**
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }
    
    
    public abstract void enterMetaGame(MetaGame game);        
    public abstract void enterBattleGame(BattleGame game);
    
}

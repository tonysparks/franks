/*
 * see license.txt 
 */
package franks.game.net;

import franks.game.GameState;

/**
 * @author Tony
 *
 */
public class GameNetworkProtocol implements NetworkProtocol {

    private GameState gameState;
    
    /**
     * 
     */
    public GameNetworkProtocol(GameState gameState) {
        this.gameState = gameState;
    }

    /* (non-Javadoc)
     * @see franks.game.net.NetworkProtocol#onGameFullState(franks.game.net.NetGameFullState)
     */
    @Override
    public void onGameFullState(NetGameFullState state) {
        this.gameState.onGameFullState(state);
    }
    
    /* (non-Javadoc)
     * @see franks.game.net.NetworkProtocol#onBattle(franks.game.net.NetBattle)
     */
    @Override
    public void onBattle(NetBattle battle) {
        this.gameState.onBattle(battle);
    }
    
    /* (non-Javadoc)
     * @see franks.game.net.NetworkProtocol#onBattleFinished(franks.game.net.NetBattleFinished)
     */
    @Override
    public void onBattleFinished(NetBattleFinished battle) {
        this.gameState.onBattleFinished(battle);        
    }

    /* (non-Javadoc)
     * @see franks.game.net.NetworkProtocol#onTurnEnd(franks.game.net.NetTurn)
     */
    @Override
    public void onTurnEnd(NetTurn turn) {
        this.gameState.onTurnEnd(turn);
    }

}

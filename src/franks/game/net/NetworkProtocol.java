/*
 * see license.txt 
 */
package franks.game.net;

/**
 * Network communication protocol
 * 
 * @author Tony
 *
 */
public interface NetworkProtocol {

	/**
	 * Send the full game gameState
	 * 
	 * @param gameState
	 */
	public void onGameFullState(NetGameFullState state);

	/**
	 * We have entered battle
	 * 
	 * @param battle
	 */
	public void onBattle(NetBattle battle);
	
	/**
	 * Battle has finished
	 * 
	 * @param battle
	 */
	public void onBattleFinished(NetBattleFinished battle);
	
	
	/**
	 * Mark an end of a turn for a remote player
	 * 
	 * @param turn
	 */
	public void onTurnEnd(NetTurn turn);
}

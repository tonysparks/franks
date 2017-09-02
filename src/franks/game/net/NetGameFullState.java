/*
 * see license.txt 
 */
package franks.game.net;

import franks.game.Army.ArmyName;

/**
 * @author Tony
 *
 */
public class NetGameFullState {

    public int turnNumber;
    public long seed;
    public long generation;
    public NetPlayer redPlayer;
    public NetPlayer greenPlayer;
    public ArmyName currentPlayersTurn;
}

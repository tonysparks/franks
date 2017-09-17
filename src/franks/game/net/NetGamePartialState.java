/*
 * see license.txt 
 */
package franks.game.net;

import java.util.List;

/**
 * @author Tony
 *
 */
public class NetGamePartialState {

    public List<NetEntity> redEntities;
    public List<NetEntity> greenEntities;
    
    public int turnNumber;
    
    public NetGamePartialState() {
    }

}

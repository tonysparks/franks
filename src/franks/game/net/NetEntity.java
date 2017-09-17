/*
 * see license.txt 
 */
package franks.game.net;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tony
 *
 */
public class NetEntity extends NetEntityPartial {
    public String dataFile;
    public List<NetEntity> entities;
    
    public NetEntity() {
        this.entities = new ArrayList<>();
    }
}

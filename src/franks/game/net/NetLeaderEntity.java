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
public class NetLeaderEntity extends NetEntity {

	public List<NetEntity> entities;
	
	public NetLeaderEntity() {
		this.entities = new ArrayList<>();
	}

}

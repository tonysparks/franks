/*
 * see license.txt 
 */
package franks.game.net;

import franks.game.commands.Command.CommandType;

/**
 * @author Tony
 *
 */
public class NetCommandRequest {

	public CommandType type;
	public int selectedEntityId;
	public int targetEntityId;
	
}

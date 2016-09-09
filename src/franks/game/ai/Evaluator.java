/*
 * see license.txt 
 */
package franks.game.ai;

import franks.game.Game;
import franks.game.commands.CommandQueue.CommandRequest;
import franks.game.entity.Entity;

/**
 * Evaluates a situation
 * 
 * @author Tony
 *
 */
public interface Evaluator {

	public double calculateScore(Entity entity, Game game);
	
	public CommandRequest getCommandRequest(Game game);
}

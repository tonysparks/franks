/*
 * see license.txt 
 */
package franks.game.ai;

import franks.game.Game;
import franks.game.commands.CommandQueue.CommandRequest;
import franks.game.entity.meta.LeaderEntity;
import franks.game.meta.MetaGame;

/**
 * Evaluates a situation
 * 
 * @author Tony
 *
 */
public interface MetaEvaluator {

	public double calculateScore(LeaderEntity entity, MetaGame game);	
	public CommandRequest getCommandRequest(Game game);
}

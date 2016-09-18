/*
 * see license.txt 
 */
package franks.game.ai.evaluators;

import franks.game.Game;
import franks.game.ai.MetaEvaluator;
import franks.game.commands.CommandQueue.CommandRequest;
import franks.game.entity.meta.LeaderEntity;
import franks.game.meta.MetaGame;

/**
 * @author Tony
 *
 */
public class DoNothingMetaEvaluator implements MetaEvaluator {

	@Override
	public double calculateScore(LeaderEntity entity, MetaGame game) {
		return game.getRandomizer().getRandomRange(0.001, 0.05);
	}

	@Override
	public CommandRequest getCommandRequest(Game game) {
		return null;
	}

}

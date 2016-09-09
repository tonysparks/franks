/*
 * see license.txt 
 */
package franks.game.ai.evaluators;

import franks.game.Game;
import franks.game.ai.Evaluator;
import franks.game.commands.CommandQueue.CommandRequest;
import franks.game.entity.Entity;

/**
 * @author Tony
 *
 */
public class DoNothingEvaluator implements Evaluator {

	@Override
	public double calculateScore(Entity entity, Game game) {
		return game.getRandomizer().getRandomRangeMax(0.1);
	}

	@Override
	public CommandRequest getCommandRequest(Game game) {
		return null;
	}

}

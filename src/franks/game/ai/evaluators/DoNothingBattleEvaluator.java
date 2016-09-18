/*
 * see license.txt 
 */
package franks.game.ai.evaluators;

import franks.game.Game;
import franks.game.ai.BattleEvaluator;
import franks.game.battle.BattleGame;
import franks.game.commands.CommandQueue.CommandRequest;
import franks.game.entity.Entity;

/**
 * @author Tony
 *
 */
public class DoNothingBattleEvaluator implements BattleEvaluator {

	@Override
	public double calculateScore(Entity entity, BattleGame game) {
		return game.getRandomizer().getRandomRange(0.001, 0.05);
	}

	@Override
	public CommandRequest getCommandRequest(Game game) {
		return null;
	}

}

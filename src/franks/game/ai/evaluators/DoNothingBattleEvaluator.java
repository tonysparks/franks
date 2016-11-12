/*
 * see license.txt 
 */
package franks.game.ai.evaluators;

import franks.game.Game;
import franks.game.actions.Command;
import franks.game.ai.BattleEvaluator;
import franks.game.battle.BattleGame;
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
	public Command getCommand(Game game) {
		return null;
	}

}

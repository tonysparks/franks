/*
 * see license.txt 
 */
package franks.game.ai;

import franks.game.Game;
import franks.game.actions.Command;
import franks.game.battle.BattleGame;
import franks.game.entity.Entity;

/**
 * Evaluates a situation
 * 
 * @author Tony
 *
 */
public interface BattleEvaluator {

	public double calculateScore(Entity entity, BattleGame game);
	
	public Command getCommand(Game game);
}

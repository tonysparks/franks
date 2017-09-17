/*
 * see license.txt 
 */
package franks.game.ai.evaluators;

import franks.game.Game;
import franks.game.actions.Command;
import franks.game.ai.MetaEvaluator;
import franks.game.entity.Entity;

/**
 * @author Tony
 *
 */
public class DoNothingMetaEvaluator implements MetaEvaluator {

    @Override
    public double calculateScore(Entity entity, Game game) {
        return game.getRandomizer().getRandomRange(0.001, 0.05);
    }

    @Override
    public Command getCommand(Game game) {
        return null;
    }

}

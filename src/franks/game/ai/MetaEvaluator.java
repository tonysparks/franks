/*
 * see license.txt 
 */
package franks.game.ai;

import franks.game.Game;
import franks.game.actions.Command;
import franks.game.entity.meta.LeaderEntity;

/**
 * Evaluates a situation
 * 
 * @author Tony
 *
 */
public interface MetaEvaluator {

    public double calculateScore(LeaderEntity entity, Game game);    
    public Command getCommand(Game game);
}

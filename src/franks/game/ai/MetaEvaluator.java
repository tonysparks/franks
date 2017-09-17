/*
 * see license.txt 
 */
package franks.game.ai;

import franks.game.Game;
import franks.game.actions.Command;
import franks.game.entity.Entity;

/**
 * Evaluates a situation
 * 
 * @author Tony
 *
 */
public interface MetaEvaluator {

    public double calculateScore(Entity entity, Game game);    
    public Command getCommand(Game game);
}

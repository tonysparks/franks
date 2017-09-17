/*
 * see license.txt 
 */
package franks.game.ai.evaluators;

import java.util.Arrays;
import java.util.List;

import franks.game.Game;
import franks.game.actions.Command;
import franks.game.ai.MetaEvaluator;
import franks.game.entity.Entity;

/**
 * @author Tony
 *
 */
public class UberMetaEvaluator implements MetaEvaluator {

    private List<MetaEvaluator> evaluators;
    private MetaEvaluator best;
    
    public UberMetaEvaluator() {
        this.evaluators = Arrays.asList(new AttackMetaEvaluator(), new MovementMetaEvaluator(), new DoNothingMetaEvaluator());
    }

    @Override
    public double calculateScore(Entity entity, Game game) {
        best = evaluators.get(0);
        double bestScore = 0;
        for(MetaEvaluator eval : evaluators) {
            double score = eval.calculateScore(entity, game);
            if(score > bestScore) {
                bestScore = score;
                best = eval;
            }
        }
        
        return bestScore;                
    }

    @Override
    public Command getCommand(Game game) {
        return best.getCommand(game);
    }

}

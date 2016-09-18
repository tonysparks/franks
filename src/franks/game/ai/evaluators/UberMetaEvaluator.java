/*
 * see license.txt 
 */
package franks.game.ai.evaluators;

import java.util.Arrays;
import java.util.List;

import franks.game.Game;
import franks.game.ai.MetaEvaluator;
import franks.game.commands.CommandQueue.CommandRequest;
import franks.game.entity.meta.LeaderEntity;
import franks.game.meta.MetaGame;

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
	public double calculateScore(LeaderEntity entity, MetaGame game) {
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
	public CommandRequest getCommandRequest(Game game) {
		return best.getCommandRequest(game);
	}

}

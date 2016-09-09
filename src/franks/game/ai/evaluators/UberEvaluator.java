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
public class UberEvaluator implements Evaluator {

	private AttackEvaluator attackEval;
	private MovementEvaluator moveEval;
	private DoNothingEvaluator doNothingEval;
	
	private double attackScore;
	private double movementScore;
	private double doNothingScore;
	
	public UberEvaluator() {
		attackEval = new AttackEvaluator();
		moveEval = new MovementEvaluator();
		doNothingEval = new DoNothingEvaluator();
	}

	/* (non-Javadoc)
	 * @see franks.game.ai.Evaluator#calculateScore(franks.game.entity.Entity, franks.game.Game)
	 */
	@Override
	public double calculateScore(Entity entity, Game game) {
		attackScore = attackEval.calculateScore(entity, game);
		movementScore = moveEval.calculateScore(entity, game);
		doNothingScore = doNothingEval.calculateScore(entity, game);
		
		return Math.max(Math.max(attackScore, movementScore), doNothingScore);
	}

	/* (non-Javadoc)
	 * @see franks.game.ai.Evaluator#getCommandRequest(franks.game.Game)
	 */
	@Override
	public CommandRequest getCommandRequest(Game game) {
		if(doNothingScore > attackScore && doNothingScore > movementScore) {
			return doNothingEval.getCommandRequest(game);
		}
		
		
		if(attackScore < movementScore) {			
			return moveEval.getCommandRequest(game);			
		}
		
		return attackEval.getCommandRequest(game);
	}

}

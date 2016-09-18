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
public class UberBattleEvaluator implements BattleEvaluator {

	private AttackBattleEvaluator attackEval;
	private MovementBattleEvaluator moveEval;
	private DoNothingBattleEvaluator doNothingEval;
	
	private double attackScore;
	private double movementScore;
	private double doNothingScore;
	
	public UberBattleEvaluator() {
		attackEval = new AttackBattleEvaluator();
		moveEval = new MovementBattleEvaluator();
		doNothingEval = new DoNothingBattleEvaluator();
	}

	/* (non-Javadoc)
	 * @see franks.game.ai.Evaluator#calculateScore(franks.game.entity.Entity, franks.game.Game)
	 */
	@Override
	public double calculateScore(Entity entity, BattleGame game) {
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

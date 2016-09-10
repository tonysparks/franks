/*
 * see license.txt 
 */
package franks.game.ai.evaluators;

import java.util.List;

import franks.game.Game;
import franks.game.Randomizer;
import franks.game.ai.BattleEvaluator;
import franks.game.battle.BattleGame;
import franks.game.commands.Command.CommandType;
import franks.game.commands.CommandQueue.CommandRequest;
import franks.game.entity.Entity;
import franks.math.Vector2f;

/**
 * Determines if it's optimal for a unit to attack
 * 
 * @author Tony
 *
 */
public class AttackEvaluator implements BattleEvaluator {

	private Entity selectedEntity;
	private Entity targetEntity;
	

	/* (non-Javadoc)
	 * @see franks.game.ai.Evaluator#calculateScore(franks.game.Game)
	 */
	@Override
	public double calculateScore(Entity entity, BattleGame game) {		
		Randomizer rand = game.getRandomizer();
		
		double bestScore = 0;
		
		this.selectedEntity = entity;
		this.targetEntity = null;
		
		int availablePoints = entity.getMeter().remaining();
		
		List<Entity> others = game.getOtherLeader(entity.getPlayer()).getEntities();
		for(int i = 0; i < others.size();i++) {
			Entity enemy = others.get(i);
			double score = 0;
			
			if(enemy.isAlive()) {
				int attackCost = entity.calculateAttackCost(enemy);
				if(attackCost > 0) {
					if(attackCost <= availablePoints) {
						int attackPercentage = entity.calculateStrictAttackPercentage();
						int defensePercentage = enemy.calculateStrictDefensePercentage();
						if(attackPercentage > defensePercentage) {
							score += rand.getRandomRange(0.3, 0.6);
						}					
						else {
							score += rand.getRandomRange(0.2, 0.3);
						}
					}
				}
			}
			
			if(score > bestScore) {
				bestScore = score;
				this.targetEntity = enemy;
			}
		}
		
		if(this.targetEntity==null) {
			bestScore = 0;
		}
		
		if(bestScore>0) {
			bestScore += rand.getRandomRange(0.2, 0.35);
		}
		
		return bestScore;
	}
	
	/* (non-Javadoc)
	 * @see franks.game.ai.Evaluator#getCommandRequest(franks.game.Game)
	 */
	@Override
	public CommandRequest getCommandRequest(Game game) {
		return new CommandRequest(game, CommandType.Attack, this.selectedEntity, this.targetEntity, new Vector2f());
	}
}

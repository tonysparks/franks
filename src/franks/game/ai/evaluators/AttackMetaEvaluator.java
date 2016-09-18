/*
 * see license.txt 
 */
package franks.game.ai.evaluators;

import java.util.List;

import franks.game.Game;
import franks.game.Randomizer;
import franks.game.ai.MetaEvaluator;
import franks.game.commands.Command.CommandType;
import franks.game.commands.CommandQueue.CommandRequest;
import franks.game.entity.Entity;
import franks.game.entity.meta.LeaderEntity;
import franks.game.meta.MetaGame;
import franks.math.Vector2f;

/**
 * Determines if it's optimal for a unit to attack
 * 
 * @author Tony
 *
 */
public class AttackMetaEvaluator implements MetaEvaluator {

	private LeaderEntity selectedEntity;
	private LeaderEntity targetEntity;

	@Override
	public double calculateScore(LeaderEntity entity, MetaGame game) {		
		Randomizer rand = game.getRandomizer();
		
		double bestScore = 0;
		
		this.selectedEntity = entity;
		this.targetEntity = null;
		
		int availablePoints = entity.getMeter().remaining();
		int aiSquadScore = calculateSquadScore(entity);
		
		List<LeaderEntity> enemies = game.getOtherTeam(entity.getTeam()).getLeaders();
		for(LeaderEntity enemyLeader : enemies) {						
			double score = 0;
			
			if(enemyLeader.isAlive()) {
				int attackCost = entity.calculateAttackCost(enemyLeader);
				if(attackCost > 0) {
					if(attackCost <= availablePoints) {
						
						int enemySquadScore = calculateSquadScore(enemyLeader);
						
						if(aiSquadScore >= enemySquadScore) {
							score += rand.getRandomRange(0.5, 0.9);
						}					
						else {
							score += rand.getRandomRange(0.3, 0.4);
						}
					}
				}
			}
			
			if(score > bestScore) {
				bestScore = score;
				this.targetEntity = enemyLeader;
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
	
	private int calculateSquadScore(LeaderEntity entity) {		
		int totalScore = 0;
		for(Entity ent : entity.getEntities()) {
			int healthScore = (int) (((double)ent.getHealth() / (double)ent.getMaxHealth()) * 100);
			int entityScore = ent.attackRange() + ent.attackBaseCost() + ent.startingActionPoints() + ent.defenseBaseScore();
			
			totalScore += (healthScore + entityScore);			
		}
		
		return totalScore;
	}
	
	/* (non-Javadoc)
	 * @see franks.game.ai.Evaluator#getCommandRequest(franks.game.Game)
	 */
	@Override
	public CommandRequest getCommandRequest(Game game) {
		return new CommandRequest(game, CommandType.Attack, this.selectedEntity, this.targetEntity, new Vector2f());
	}
}

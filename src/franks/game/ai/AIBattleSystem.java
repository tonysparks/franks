/*
 * see license.txt 
 */
package franks.game.ai;

import java.util.List;

import franks.game.Game;
import franks.game.Player;
import franks.game.Turn;
import franks.game.ai.evaluators.UberEvaluator;
import franks.game.battle.BattleGame;
import franks.game.commands.CommandQueue.CommandRequest;
import franks.game.commands.CommandRequestQueue;
import franks.game.commands.CommandRequestQueue.RequestDispatcher;
import franks.game.entity.Entity;
import franks.game.entity.meta.LeaderEntity;
import franks.util.TimeStep;
import franks.util.Updatable;

/**
 * @author Tony
 *
 */
public class AIBattleSystem implements Updatable {

	private BattleGame game;	
	private Player aiPlayer;
	private LeaderEntity aiEntityLeader;
	
	private CommandRequestQueue requestQueue;
	
	private UberEvaluator[] evaluators;
	private int numberOfThoughtCycles;
	
	
	/**
	 * 
	 */
	public AIBattleSystem(BattleGame game, Player aiPlayer) {
		this.game = game;
		this.aiPlayer = aiPlayer;
		if(game.getAttacker().getPlayer() == aiPlayer) {
			this.aiEntityLeader = game.getAttacker();
		}
		else {
			this.aiEntityLeader = game.getDefender();
		}
		
		this.requestQueue = new CommandRequestQueue(game);
		this.evaluators = new UberEvaluator[Game.MAX_ENTITIES];

	}
	
	
	@Override
	public void update(TimeStep timeStep) {
		Turn currentTurn = game.getCurrentTurn();
		
		if(currentTurn.isPlayersTurn(aiPlayer)) {
			if(!this.requestQueue.isReady()) {
				strategize();
				this.numberOfThoughtCycles++;
			}
			else {
				if(this.requestQueue.isCompleted()) {
					
					 if(this.numberOfThoughtCycles > this.aiEntityLeader.getEntities().size()*2) {					
						 game.endCurrentTurnAI();
						 this.numberOfThoughtCycles = 0;
					 }
					 else {
						 this.requestQueue.reset();
					 }
				}
			}
		}
	}

	/**
	 * Figure out what commands to start dispatching
	 */
	private void strategize() {
		List<Entity> team = aiEntityLeader.getEntities();
		
		double bestScore = 0;
		BattleEvaluator bestEval = null;
		
		for(Entity ent : team) {
			double score = 0;
			if(this.evaluators[ent.getId()] == null) {
				this.evaluators[ent.getId()] = new UberEvaluator();
			}
	
			score = this.evaluators[ent.getId()].calculateScore(ent, game);
			if(score > bestScore) {
				bestEval = this.evaluators[ent.getId()];
				bestScore = score;
			}			
		}
		
		if(bestEval!=null) {
			CommandRequest request = bestEval.getCommandRequest(game);
			if(request != null) {
				this.requestQueue.addRequest(new RequestDispatcher() {
					
					@Override
					public Entity dispatchRequest(Game game) {
						return request.getNetCommandRequest().dispatchRequest(game);
					}
				});
			}
			this.requestQueue.markReady();
		}
	}
}

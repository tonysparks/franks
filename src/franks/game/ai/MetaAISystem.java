/*
 * see license.txt 
 */
package franks.game.ai;

import java.util.List;

import franks.game.Game;
import franks.game.Player;
import franks.game.Turn;
import franks.game.ai.evaluators.UberMetaEvaluator;
import franks.game.commands.CommandQueue.CommandRequest;
import franks.game.commands.CommandRequestQueue;
import franks.game.commands.CommandRequestQueue.RequestDispatcher;
import franks.game.entity.Entity;
import franks.game.entity.EntityList;
import franks.game.entity.meta.LeaderEntity;
import franks.game.meta.MetaGame;
import franks.util.TimeStep;
import franks.util.Updatable;

/**
 * @author Tony
 *
 */
public class MetaAISystem implements Updatable {

	private MetaGame game;
	private Player aiPlayer;
	
	private CommandRequestQueue requestQueue;
	
	private UberMetaEvaluator[] evaluators;
	private int numberOfThoughtCycles;

	
	/**
	 * 
	 */
	public MetaAISystem(MetaGame game, Player aiPlayer) {
		this.game = game;
		this.aiPlayer = aiPlayer;
				
		this.requestQueue = new CommandRequestQueue(game);
		this.evaluators = new UberMetaEvaluator[EntityList.MAX_ENTITIES];
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
					
					 if(this.numberOfThoughtCycles > this.aiPlayer.getTeam().armySize()) {					
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
		List<LeaderEntity> team = aiPlayer.getTeam().getLeaders();
		
		double bestScore = 0;
		MetaEvaluator bestEval = null;
		
		for(LeaderEntity ent : team) {
			double score = 0;
			if(this.evaluators[ent.getId()] == null) {
				this.evaluators[ent.getId()] = new UberMetaEvaluator();
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

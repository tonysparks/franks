/*
 * see license.txt 
 */
package franks.game.ai;

import java.util.List;

import franks.game.Game;
import franks.game.GameState;
import franks.game.Player;
import franks.game.Turn;
import franks.game.actions.Command;
import franks.game.actions.CommandDispatcherQueue;
import franks.game.actions.CommandDispatcherQueue.CommandDispatcher;
import franks.game.ai.evaluators.UberMetaEvaluator;
import franks.game.entity.Entity;
import franks.game.entity.EntityList;
import franks.game.entity.meta.LeaderEntity;
import franks.util.TimeStep;
import franks.util.Updatable;

/**
 * @author Tony
 *
 */
public class MetaAISystem implements Updatable {

	private GameState gameState;
	private Player aiPlayer;
	
	private CommandDispatcherQueue dispatcherQueue;
	
	private UberMetaEvaluator[] evaluators;
	private int numberOfThoughtCycles;

	
	/**
	 * 
	 */
	public MetaAISystem(GameState gameState, Player aiPlayer) {
		this.gameState = gameState;
		this.aiPlayer = aiPlayer;
				
		this.dispatcherQueue = new CommandDispatcherQueue(gameState);
		this.evaluators = new UberMetaEvaluator[EntityList.MAX_ENTITIES];
	}

	@Override
	public void update(TimeStep timeStep) {
		Game game = gameState.getActiveGame();
		Turn currentTurn = game.getCurrentTurn();
		
		if(currentTurn.isPlayersTurn(aiPlayer)) {
			
			
			if(!this.dispatcherQueue.isReady()) {
				strategize();
				this.numberOfThoughtCycles++;
			}
			else {
				if(this.dispatcherQueue.isCompleted()) {
					
					 if(this.numberOfThoughtCycles > this.aiPlayer.getTeam().armySize()) {					
						 game.endCurrentTurnAI();
						 this.numberOfThoughtCycles = 0;
					 }
					 else {
						 this.dispatcherQueue.reset();
					 }
				}
			}
		}
	}

	/**
	 * Figure out what commands to start dispatching
	 */
	private void strategize() {
		Game game = gameState.getActiveGame();
		
		List<LeaderEntity> team = aiPlayer.getTeam().getLeaders();
		if(team.isEmpty()) {
			game.endCurrentTurnAI();
			return;
		}
		
		
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
			Command command = bestEval.getCommand(game);
			if(command != null) {
				this.dispatcherQueue.addDispatcher(new CommandDispatcher() {
					
					@Override
					public Entity dispatchCommand(Game game) {
						game.dispatchCommand(command);
						return command.selectedEntity;
						//return command.asNetCommandRequest().dispatchCommand(game);
					}
				});
			}
			this.dispatcherQueue.markReady();
		}
	}
}

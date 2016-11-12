/*
 * see license.txt 
 */
package franks.game.ai;

import franks.game.Game;
import franks.game.GameState;
import franks.game.Player;
import franks.game.Turn;
import franks.game.actions.Command;
import franks.game.actions.CommandDispatcherQueue;
import franks.game.actions.CommandDispatcherQueue.CommandDispatcher;
import franks.game.ai.evaluators.UberBattleEvaluator;
import franks.game.battle.BattleGame;
import franks.game.entity.Entity;
import franks.game.entity.EntityList;
import franks.game.entity.meta.LeaderEntity;
import franks.util.TimeStep;
import franks.util.Updatable;

/**
 * @author Tony
 *
 */
public class BattleAISystem implements Updatable {

	private GameState gameState;	
	private Player aiPlayer;
	private LeaderEntity aiEntityLeader;
	
	private CommandDispatcherQueue requestQueue;
	
	private UberBattleEvaluator[] evaluators;
	private int numberOfThoughtCycles;
	
	
	/**
	 * 
	 */
	public BattleAISystem(GameState gameState, Player aiPlayer) {
		this.gameState = gameState;
		this.aiPlayer = aiPlayer;
				
		this.requestQueue = new CommandDispatcherQueue(gameState);
		this.evaluators = new UberBattleEvaluator[EntityList.MAX_ENTITIES];

	}
	
	public void onEnterBattle(BattleGame game) {
		if(game.getAttacker().getPlayer() == aiPlayer) {
			this.aiEntityLeader = game.getAttacker();
		}
		else {
			this.aiEntityLeader = game.getDefender();
		}
	}
	
	
	@Override
	public void update(TimeStep timeStep) {
		Game game = this.gameState.getActiveGame();
		
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
		BattleGame game = (BattleGame)this.gameState.getActiveGame();
		
		EntityList team = aiEntityLeader.getEntities();
		
		double bestScore = 0;
		BattleEvaluator bestEval = null;
		
		for(Entity ent : team) {
			double score = 0;
			if(this.evaluators[ent.getId()] == null) {
				this.evaluators[ent.getId()] = new UberBattleEvaluator();
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
				this.requestQueue.addDispatcher(new CommandDispatcher() {
					
					@Override
					public Entity dispatchCommand(Game game) {
						game.dispatchCommand(command);
						return command.selectedEntity;
					}
				});
			}
			this.requestQueue.markReady();
		}
	}
}

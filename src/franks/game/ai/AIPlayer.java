/*
 * see license.txt 
 */
package franks.game.ai;

import franks.game.GameState;
import franks.game.OtherPlayer;
import franks.game.Player;
import franks.game.battle.BattleGame;
import franks.game.events.TurnCompletedEvent;
import franks.game.meta.MetaGame;
import franks.util.TimeStep;

/**
 * A Player controlled by the computer
 * 
 * @author Tony
 *
 */
public class AIPlayer extends OtherPlayer {

	private Player aiPlayer;
	private MetaAISystem metaAISystem;
	private BattleAISystem battleAISystem;
	
	private boolean inMetaGame;	
	
	/**
	 * 
	 */
	public AIPlayer(GameState state) {
		super(state.getAIPlayer());
		this.aiPlayer = state.getAIPlayer();
		
		this.metaAISystem = new MetaAISystem(state, this.aiPlayer);
		this.battleAISystem = new BattleAISystem(state, this.aiPlayer);
	}
	
	@Override
	public void enterMetaGame(MetaGame game) {	
		this.inMetaGame = true;		
	}
	

	@Override
	public void enterBattleGame(BattleGame game) {
		this.inMetaGame = false;
		this.battleAISystem.onEnterBattle(game);
	}

	@Override
	public void onTurnCompleted(TurnCompletedEvent event) {		
	}
	
	/* (non-Javadoc)
	 * @see franks.util.Updatable#update(franks.util.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		if(this.inMetaGame) {
			this.metaAISystem.update(timeStep);
		}
		else {
			this.battleAISystem.update(timeStep);
		}
	}

}

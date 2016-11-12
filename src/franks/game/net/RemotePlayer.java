/*
 * see license.txt 
 */
package franks.game.net;

import java.util.ArrayList;

import franks.game.Game;
import franks.game.GameState;
import franks.game.OtherPlayer;
import franks.game.Turn;
import franks.game.actions.Command;
import franks.game.actions.CommandDispatcherQueue;
import franks.game.actions.CommandDispatcherQueue.CommandDispatcher;
import franks.game.battle.BattleGame;
import franks.game.entity.Entity;
import franks.game.events.BattleEvent;
import franks.game.events.BattleListener;
import franks.game.events.TurnCompletedEvent;
import franks.game.events.TurnCompletedListener;
import franks.game.meta.MetaGame;
import franks.util.TimeStep;

/**
 * Represents a remote player.  We must send information to and receive from.
 * 
 * @author Tony
 *
 */
public class RemotePlayer extends OtherPlayer implements TurnCompletedListener, BattleListener {

	private GameState gameState;
	private CommandDispatcherQueue dispatcherQueue;
	
	/**
	 * @param player
	 */
	public RemotePlayer(GameState state) {
		super(state.getAIPlayer());
		
		this.gameState = state;
		this.dispatcherQueue = new CommandDispatcherQueue(state);
	}

	/* (non-Javadoc)
	 * @see franks.util.Updatable#update(franks.util.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		Game game = this.gameState.getActiveGame();
		Turn gameTurn = game.getCurrentTurn();
		
		/**
		 * If it's the remote players turn,
		 * wait until we receive an end turn message
		 * from them.  Once we do, we replay their Commands
		 * locally.  Once those Commands are completed, we
		 * can official end their turn.
		 */
		//if(gameTurn.isPlayersTurn(getPlayer())) 
		{	
			if(this.dispatcherQueue.isReady() && this.dispatcherQueue.isCompleted()) {
				if(!this.dispatcherQueue.isFromBattle()) {
					gameTurn.requestForTurnCompletion();
				}
				this.dispatcherQueue.reset();
			}
		}		
	}

	@Override
	public void enterMetaGame(MetaGame game) {
	}

	@Override
	public void enterBattleGame(BattleGame game) {
	}
	

	
	/**
	 * We've received a message from the remote player that they have 
	 * ended their turn.  We'll want to replay their {@link Command}s locally
	 * and after they are completed, we'll flag our local game as their turn 
	 * being completed.
	 */
	public void onRemoteTurnEndMessage(NetTurn turn) {
		this.dispatcherQueue.reset();
		for(NetCommand command : turn.commands) {
			this.dispatcherQueue.addDispatcher(new CommandDispatcher() {
				
				@Override
				public Entity dispatchCommand(Game game) {
					return command.dispatchCommand(game);						
				}
			});
		}
		this.dispatcherQueue.markReady();
	}

	
	/**
	 * We've received a message from the remote player that they are attacking
	 * us.  Enter battle now.
	 * 
	 * @param netBattle
	 */
	public void onRemoteBattleMessage(NetBattle netBattle) {
		
		this.dispatcherQueue.markFromBattle();
		for(NetCommand command : netBattle.commands) {
			this.dispatcherQueue.addDispatcher(new CommandDispatcher() {
				
				@Override
				public Entity dispatchCommand(Game game) {
					return command.dispatchCommand(game);						
				}
			});
		}
		
		
		this.dispatcherQueue.markReady();
		
		//MetaGame game = this.gameState.getMetaGame();
		
		//LeaderEntity attacker = (LeaderEntity)game.getEntityById(netBattle.attackerLeaderId);
		//LeaderEntity defender = (LeaderEntity)game.getEntityById(netBattle.defenderLeaderId);
		
		//BattleGame battleGame = game.getBattleGame();
		//Battle battle = new Battle(attacker, defender);
		//battleGame.enterBattle(battle);
		
		//game.getApp().pushScreen(new BattleScreen(game.getApp(), game.getState(), game.getBattleGame()));
	}
	
	/**
	 * We've locally heard of a turn being completed, decide if this needs to
	 * be distributed to the remote player
	 * 
	 * @param event
	 */
	@Override
	public void onTurnCompleted(TurnCompletedEvent event) {
		//System.out.println("Finished turn for: " + event.getPlayersTurn().getName());
		if(this.gameState.getLocalPlayer() == event.getPlayersTurn()) {
			
			if(this.gameState.isConnected()) {
				NetTurn net = new NetTurn();
				net.commands = new ArrayList<>();
				for(Command command : event.getExecuteCommands()) {
					net.commands.add(command.asNetCommand());
				}
				
				NetMessage msg = NetMessage.turnMessage(net);
				this.gameState.getConnection().sendMessage(msg);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see franks.game.events.BattleListener#onBattle(franks.game.events.BattleEvent)
	 */
	@Override
	public void onBattle(BattleEvent event) {
		if(this.gameState.getLocalPlayer() == event.getBattle().getAttacker().getPlayer()) {
			if(this.gameState.isConnected()) {
				MetaGame game = this.gameState.getMetaGame();
				Turn turn = game.getCurrentTurn();
				
				NetBattle battle = new NetBattle();
				battle.commands = new ArrayList<>();
				for(Command command : turn.getExecutedCommands()) {
					battle.commands.add(command.asNetCommand());
				}
				
				battle.attackerLeaderId = event.getBattle().getAttacker().getId();
				battle.defenderLeaderId = event.getBattle().getDefender().getId();
				
				NetMessage msg = NetMessage.battleMessage(battle);
				this.gameState.getConnection().sendMessage(msg);
			}
		}
	}
}

/*
 * see license.txt 
 */
package franks.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import franks.game.commands.CommandQueue.CommandRequest;
import franks.game.entity.Entity;
import franks.game.entity.EntityList;
import franks.game.net.NetCommandRequest;
import franks.game.net.NetTurn;

/**
 * @author Tony
 *
 */
public class Turn {

	private int number;
	private Game game;
	private Player activePlayer;
	private boolean markedForEndTurn;
	private boolean isRemotePlayersTurnCompleted;
	private Entity activeEntity;
	
	private Queue<NetCommandRequest> netRequests;
	private List<CommandRequest> executedRequests;
	
	/**
	 * @param game
	 * @param activePlayer - the player's who's turn it is
	 * @param number
	 */
	public Turn(Game game, Player activePlayer, int number) {
		this.game = game;
		this.activePlayer = activePlayer;
		this.number = number;
		this.markedForEndTurn = false;
		this.executedRequests = new ArrayList<>();
		this.netRequests = new ConcurrentLinkedQueue<>();
	}
	
	
	
	/**
	 * Handle a remote end turn message from a client.
	 * 
	 * If it isn't this local players turn, we want to queue up
	 * the remote CommandRequests so that we can run them one after
	 * each other (we must ensure they don't execute all at once so
	 * that they don't collide against each other).
	 * 
	 * 
	 * @param turn
	 */
	public void handleNetTurnMessage(NetTurn turn) {
		
		// queue the remote commands
		if(!isPlayersTurn(game.getLocalPlayer())) {			
			for(NetCommandRequest request : turn.requests) {						
				this.netRequests.add(request);
			}					
		}
		
		// mark that the remote turn has ended, but
		// the execution of the queued command's has
		// not yet completed, we must wait for this in
		// order to deem the turn as officially over
		markRemoteTurnCompleted();
	}
	
	public void addCommandRequest(CommandRequest request) {
		if(isPlayersTurn(game.getLocalPlayer())) {
			this.executedRequests.add(request);
		}
	}
	
	private void checkRemotePlayersTurnState() {
		if(this.isRemotePlayersTurnCompleted) {
			
			// if we have any more CommandRequests to execute,
			// go ahead and do that
			if(activeEntity==null||activeEntity.isCommandQueueEmpty()) {		
				if(!this.netRequests.isEmpty()) {
					NetCommandRequest request = this.netRequests.poll();
					activeEntity = request.dispatchRequest(game);								
				}				
			}
			
			
			// if there are no more command requests left to execute, we can 
			// officially close out this turn
			if((activeEntity==null||activeEntity.isCommandQueueEmpty())&&this.netRequests.isEmpty()) {
				markTurnCompleted();
//				this.isRemotePlayersTurnCompleted=false;
			}
		}
	}
	
	public Turn checkTurnState() {
		checkRemotePlayersTurnState();
		
		if(this.markedForEndTurn) {
			EntityList entities = game.getEntities();
			if(entities.commandsCompleted()) {
				game.getEntities().endTurn();
				
				Player greenPlayer = game.getGreenPlayer();
				Player redPlayer = game.getRedPlayer();
				
				
				Player nextPlayersTurn = greenPlayer;
				if(isPlayersTurn(greenPlayer)) {
					nextPlayersTurn = redPlayer;
				}
				
				handleEndTurn();
				
				return new Turn(game, nextPlayersTurn, this.number + 1);
			}
		}
		
		return this;
	}
	
	private void handleEndTurn() {
//		Player localPlayer = game.getLocalPlayer();
//		if(isPlayersTurn(localPlayer) && game.isPeerConnected()) {
//			NetTurn net = new NetTurn();
//			net.requests = new ArrayList<>();
//			for(CommandRequest request : this.executedRequests) {
//				net.requests.add(request.getNetCommandRequest());
//			}
//			
//			NetMessage msg = NetMessage.turnMessage(net);
//			game.getConnection().sendMessage(msg);
//		}		
	}
	
	public boolean isPlayersTurn(Player player) {
		return player == this.activePlayer;
	}
	
	/**
	 * @return the activePlayer
	 */
	public Player getActivePlayer() {
		return activePlayer;
	}
	
	/**
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Mark this turn as being eligible for completion
	 */
	public void markTurnCompleted() {
		this.markedForEndTurn = true;
	}
	
	private void markRemoteTurnCompleted() {
		this.isRemotePlayersTurnCompleted=true;
	}
}

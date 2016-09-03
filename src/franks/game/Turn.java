/*
 * see license.txt 
 */
package franks.game;

import java.util.ArrayList;
import java.util.List;

import franks.game.commands.CommandQueue.CommandRequest;
import franks.game.entity.EntityList;
import franks.game.net.NetMessage;
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
	
	private List<CommandRequest> executedRequests;
	
	/**
	 * 
	 */
	public Turn(Game game, Player activePlayer, int number) {
		this.game = game;
		this.activePlayer = activePlayer;
		this.number = number;
		this.markedForEndTurn = false;
		this.executedRequests = new ArrayList<>();
	}
	
	public void addCommandRequest(CommandRequest request) {
		if(isPlayersTurn(game.getLocalPlayer())) {
			this.executedRequests.add(request);
		}
	}
	
	public Turn checkTurnState() {
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
		Player localPlayer = game.getLocalPlayer();
		if(isPlayersTurn(localPlayer) && game.isPeerConnected()) {
			NetTurn net = new NetTurn();
			net.requests = new ArrayList<>();
			for(CommandRequest request : this.executedRequests) {
				net.requests.add(request.getNetCommandRequest());
			}
			
			NetMessage msg = NetMessage.turnMessage(net);
			game.getConnection().sendMessage(msg);
		}		
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
}

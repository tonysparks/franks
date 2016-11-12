/*
 * see license.txt 
 */
package franks.game;

import java.util.ArrayList;
import java.util.List;

import franks.game.actions.Command;
import franks.game.entity.EntityList;
import franks.game.events.TurnCompletedEvent;
import franks.sfx.Sounds;

/**
 * @author Tony
 *
 */
public class Turn {

	private int number;
	private Game game;
	private Player activePlayer;
	private boolean requestForTurnCompletion;
	
	private List<Command> executedCommands;
	
	/**
	 * @param game
	 * @param activePlayer - the player's who's turn it is
	 * @param number
	 */
	public Turn(Game game, Player activePlayer, int number) {
		this.game = game;
		this.activePlayer = activePlayer;
		this.number = number;
		this.requestForTurnCompletion = false;
		this.executedCommands = new ArrayList<>();	
	}
	
	/**
	 * @return the executedCommands
	 */
	public List<Command> getExecutedCommands() {
		return executedCommands;
	}

	/**
	 * Record a {@link Command} that was executed this
	 * {@link Turn}.
	 * 
	 * @param command
	 */
	public void recordCommand(Command command) {
		this.executedCommands.add(command);
	}
	
	
	public Turn checkTurnState() {		
		if(this.requestForTurnCompletion) {
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
				
				if(isPlayersTurn(game.getLocalPlayer())) {
					Sounds.playGlobalSound(Sounds.flagCaptured);
				}
				else {
					Sounds.playGlobalSound(Sounds.flagReturned);
				}
				
				return new Turn(game, nextPlayersTurn, this.number + 1);
			}
		}
		
		return this;
	}
	
	private void handleEndTurn() {
		this.game.dispatchEvent(new TurnCompletedEvent(this, this.activePlayer, new ArrayList<>(this.executedCommands)));	
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
	public void requestForTurnCompletion() {
		this.requestForTurnCompletion = true;
	}	
}

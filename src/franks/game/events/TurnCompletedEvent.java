/*
 * see license.txt 
 */
package franks.game.events;

import java.util.List;

import franks.game.Player;
import franks.game.actions.Command;
import leola.frontend.listener.Event;

/**
 * @author Tony
 *
 */
public class TurnCompletedEvent extends Event {

    private Player playersTurn;
    private List<Command> executeCommands;
    
    /**
     * 
     * @param source
     * @param playersTurn the players who's turn it is
     * @param executeCommands the commands executed in the turn.
     */
    public TurnCompletedEvent(Object source, Player playersTurn, List<Command> executeCommands) {
        super(source);
        
        this.playersTurn = playersTurn;
        this.executeCommands = executeCommands;
    }
    
    /**
     * @return the playersTurn
     */
    public Player getPlayersTurn() {
        return playersTurn;
    }
    
    /**
     * @return the executeCommands
     */
    public List<Command> getExecuteCommands() {
        return executeCommands;
    }

}

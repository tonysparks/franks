/*
 * see license.txt 
 */
package franks.game.actions;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import franks.game.Game;
import franks.game.GameState;
import franks.game.entity.Entity;

/**
 * @author Tony
 *
 */
public class CommandDispatcherQueue {

    public static interface CommandDispatcher {
        public Entity dispatchCommand(Game game);
    }
    

    private GameState gameState;
    private Entity activeEntity;
    private Queue<CommandDispatcher> dispatcherQueue;
    
    
    /**
     * If this queue is ready to be drained
     */
    private boolean isReady;
    
    /**
     * If this motion if from a Battle or not
     */
    private boolean isFromBattle;
    
    /**
     * @param game
     */
    public CommandDispatcherQueue(GameState gameState) {
        this.gameState = gameState;
        this.dispatcherQueue = new ConcurrentLinkedQueue<>();
        this.isReady = false;
        this.isFromBattle = false;
    }

    
    public void reset() {
        this.isReady = false;
        this.isFromBattle = false;
        this.activeEntity = null;
        this.dispatcherQueue.clear();
    }
    
    /**
     * @return the isFromBattle
     */
    public boolean isFromBattle() {
        return isFromBattle;
    }
    
    public void markFromBattle() {
        this.isFromBattle = true;
    }
    
    public void addDispatcher(CommandDispatcher dispatcher) {
        this.dispatcherQueue.add(dispatcher);
    }
    
    public void addDispatchers(List<CommandDispatcher> dispatchers) {
        this.dispatcherQueue.addAll(dispatchers);
    }
    
    /**
     * @return the isCompleted
     */
    public boolean isCompleted() {
        return checkIfCompleted();
    }
    
    /**
     * @return the isReady
     */
    public boolean isReady() {
        return isReady;
    }
    
    /**
     * Marks this as ready to start dispatching the {@link CommandDispatcher}
     */
    public void markReady() {
        this.isReady = true;
    }
    
    private boolean checkIfCompleted() {
        if(this.isReady) {
            
            // if we have any more CommandRequests to execute,
            // go ahead and do that
            if(activeEntity==null||activeEntity.isCommandQueueEmpty()) {        
                if(!this.dispatcherQueue.isEmpty()) {
                    CommandDispatcher dispatcher = this.dispatcherQueue.poll();
                    activeEntity = dispatcher.dispatchCommand(gameState.getActiveGame());                                
                }                
            }
            
            
            // if there are no more command commands left to execute, we can 
            // officially close out this turn
            if((activeEntity==null||activeEntity.isCommandQueueEmpty())&&this.dispatcherQueue.isEmpty()) {
                return true;
            }
        }
        
        return false;
    }
}

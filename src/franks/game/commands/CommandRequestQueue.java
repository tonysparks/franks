/*
 * see license.txt 
 */
package franks.game.commands;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import franks.game.Game;
import franks.game.entity.Entity;

/**
 * @author Tony
 *
 */
public class CommandRequestQueue {

	public static interface RequestDispatcher {
		public Entity dispatchRequest(Game game);
	}
	

	private Game game;
	private Entity activeEntity;
	private Queue<RequestDispatcher> requestQueue;
	
	
	/**
	 * If this queue is ready to be drained
	 */
	private boolean isReady;
	
	/**
	 * @param game
	 */
	public CommandRequestQueue(Game game) {
		this.game = game;
		this.requestQueue = new ConcurrentLinkedQueue<>();
		this.isReady = false;
	}

	
	public void reset() {
		this.isReady = false;
		this.activeEntity = null;
		this.requestQueue.clear();
	}
	
	public void addRequest(RequestDispatcher dispatcher) {
		this.requestQueue.add(dispatcher);
	}
	
	public void addRequests(List<RequestDispatcher> dispatchers) {
		this.requestQueue.addAll(dispatchers);
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
	 * Marks this as ready to start dispatching the {@link RequestDispatcher}
	 */
	public void markReady() {
		this.isReady = true;
	}
	
	private boolean checkIfCompleted() {
		if(this.isReady) {
			
			// if we have any more CommandRequests to execute,
			// go ahead and do that
			if(activeEntity==null||activeEntity.isCommandQueueEmpty()) {		
				if(!this.requestQueue.isEmpty()) {
					RequestDispatcher request = this.requestQueue.poll();
					activeEntity = request.dispatchRequest(game);								
				}				
			}
			
			
			// if there are no more command requests left to execute, we can 
			// officially close out this turn
			if((activeEntity==null||activeEntity.isCommandQueueEmpty())&&this.requestQueue.isEmpty()) {
				return true;
			}
		}
		
		return false;
	}
}

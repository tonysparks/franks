/*
 * see license.txt 
 */
package franks.game.commands;

import franks.game.commands.CommandQueue.CommandRequest;
import franks.gfx.Renderable;

/**
 * @author Tony
 *
 */
public abstract class CommandAction implements Renderable {

	public enum CompletionState {
		Success,
		InProgress,
		Failed,
		Cancelled
		;
		
		public boolean isCompleted() {
			return this != InProgress;
		}
	}
	
	private CommandRequest request;
	
	public CommandAction(CommandRequest request) {
		this.request = request;
	}
	
	/**
	 * @return the request
	 */
	public CommandRequest getRequest() {
		return request;
	}
	
	public CommandAction start() { return this; }
	public CommandAction end() { return this; }
	
	public abstract void cancel();
	public abstract CompletionState getCurrentState();	
}

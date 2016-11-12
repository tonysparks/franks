/*
 * see license.txt 
 */
package franks.game.actions;

import franks.gfx.Renderable;

/**
 * @author Tony
 *
 */
public abstract class ExecutedAction implements Renderable {

	public enum CompletionState {
		Success,
		InProgress,
		Failed,
		Cancelled,
		;
		
		public boolean isCompleted() {
			return this != InProgress;
		}
	}
	
	private Command command;
	private boolean isEnded;
	
	public ExecutedAction(Command command) {
		this.command = command;
	}
	
	/**
	 * @return the command
	 */
	public Command getCommand() {
		return command;
	}
	
	public ExecutedAction start() { return this; }
	public ExecutedAction end() {
		this.isEnded = true;
		return this; 
	}
	
	/**
	 * @return true if completed and the {@link ExecutedAction#end()}
	 * method has been completed.
	 */
	public boolean hasEnded() {
		return this.isEnded;
	}
	
	public abstract void cancel();
	public abstract CompletionState getCurrentState();	
}

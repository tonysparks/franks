/*
 * see license.txt 
 */
package franks.game.commands;

import franks.gfx.Renderable;

/**
 * @author Tony
 *
 */
public interface CommandAction extends Renderable {

	public enum CompletionState {
		Success,
		InProgress,
		Failed,
		Cancelled
		;
	}
	default public CommandAction start() { return this; }
	default public CommandAction end() { return this; }
	
	public void cancel();
	public CompletionState getCurrentState();	
}

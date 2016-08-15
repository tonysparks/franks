/*
 * see license.txt 
 */
package franks.game;

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
	
	public void cancel();
	public CompletionState getCurrentState();	
}

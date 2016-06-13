/*
 * see license.txt 
 */
package newera.game;

import newera.gfx.Renderable;

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

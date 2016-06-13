/*
 * see license.txt 
 */
package newera.gfx;

import newera.util.State;

/**
 * A screen such as a TitleScreen, InGameScreen, etc.
 * 
 * @author Tony
 *
 */
public interface Screen extends State {


	/**
	 * Clean up resources associated with this screen
	 */
	public void destroy();
	
	/**
	 * Render this object.
	 * 
	 * @param renderer
	 */
	public void render(Canvas canvas, float alpha);
	
	/**
	 * @return the {@link Inputs} handler
	 */
	public Inputs getInputs();
}

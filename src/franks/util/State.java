/*
 * see license.txt 
 */
package franks.util;


/**
 * @author Tony
 *
 */
public interface State {
	public void enter();
	public void update(TimeStep timeStep);
	public void exit();
}

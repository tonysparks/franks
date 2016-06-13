/*
 * see license.txt 
 */
package newera.util;


/**
 * @author Tony
 *
 */
public interface State {
	public void enter();
	public void update(TimeStep timeStep);
	public void exit();
}

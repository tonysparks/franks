/*
 * see license.txt 
 */
package newera.util;
/**
 * @author Tony
 *
 */
public interface Logger {
	
	/**
	 * @param msg
	 */
	public void print(Object msg);
	
	/**
	 * @param msg
	 */
	public void println(Object msg);
		
	/**
	 * @param msg
	 * @param args
	 */
	public void printf(Object msg, Object ...args );
}

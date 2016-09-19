/*
 * see license.txt 
 */
package franks.gfx;

/**
 * @author Tony
 *
 */
public enum KeyActions {

	UP(1<<0),
	DOWN(1<<1),
	LEFT(1<<2),
	RIGHT(1<<3),
	
	
	;
	
	private int mask;
	private KeyActions(int mask) {
		this.mask = mask;
	}
	
	/**
	 * @return the mask
	 */
	public int getMask() {
		return mask;
	}
	
	public boolean isDown(int inputKeys) {
		return (mask & inputKeys) != 0;
	}

}

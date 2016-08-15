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
	WALK(1<<4),
	FIRE(1<<5),
	RELOAD(1<<6),
	WEAPON_SWITCH_UP(1<<7),
	WEAPON_SWITCH_DOWN(1<<8),
	THROW_GRENADE(1<<9),
	
	SPRINT(1<<10),
	CROUCH(1<<11),
	
	USE(1<<12),
	DROP_WEAPON(1<<13),
	MELEE_ATTACK(1<<14),
	
	SAY(1<<15),
	TEAM_SAY(1<<16),
	
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

}

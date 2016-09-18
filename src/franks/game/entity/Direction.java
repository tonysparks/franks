/*
 * see license.txt 
 */
package franks.game.entity;

import franks.math.Vector2f;

/**
 * @author Tony
 *
 */
public enum Direction {
	NORTH(0, 1),
	NORTH_EAST(1, 1),
	EAST(1, 0),
	SOUTH_EAST(1, -1),
	SOUTH(0, -1),
	SOUTH_WEST(-1, -1),
	WEST(-1, 0),
	NORTH_WEST(-1, 1),
	;
	
	private Vector2f facing;
	
	private Direction(float x, float y) {
		this.facing = new Vector2f(x, y);
	}
	
	/**
	 * @return the facing
	 */
	public Vector2f getFacing() {
		return facing;
	}
	
	public int getX() {
		return (int) facing.x;
	}
	
	public int getY() {
		return (int) facing.y;
	}
	
	public Direction cardinalPerp() {
		switch(toCardinal()) {
			case EAST:
				return NORTH;					
			case NORTH:
				return EAST;					
			case SOUTH:
				return WEST;					
			case WEST:
				return SOUTH;
			default: return SOUTH;
		}
	}
	
	public Direction toCardinal() {
		switch(this) {
			case NORTH_EAST:
			case NORTH_WEST:
				return NORTH;
				
			case SOUTH_EAST:
			case SOUTH_WEST:
				return SOUTH;
			default:
				return this;
		}
	}
	
	public static final Direction[] values = values();
	
	public static Direction getDirection(Vector2f v) {
		return getDirection(v.x, v.y);
	}
	
	public static Direction getDirection(float x, float y) {
//		Vector2f v = new Vector2f(x,y);
//		if(!v.isZero())System.out.println(v);
		
		final float threshold = 0.35f;
		if(y > threshold) {
			if(x > threshold) {
				return Direction.SOUTH;
			}
			else if(x < -threshold) {
				return Direction.WEST;
			}
			else {
				return Direction.SOUTH_WEST;
			}
		}
		else if(y < -threshold) {
			if(x > threshold) {
				return Direction.EAST;
			}
			else if(x < -threshold) {
				return Direction.NORTH;
			}
			else {
				return Direction.NORTH_EAST;
			}
		}
		else {
			if(x > threshold) {
				return Direction.SOUTH_EAST;
			}
			else if(x < -threshold) {
				return Direction.NORTH_WEST;
			}
			else {
				return Direction.SOUTH;
			}
		}
	}

}

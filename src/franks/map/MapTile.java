/*
 * see license.txt 
 */
package franks.map;

import franks.game.TerrainData.TerrainTileData;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.math.Circle;
import franks.math.OBB;
import franks.math.Rectangle;
import franks.math.Triangle;
import franks.util.TimeStep;

/**
 * @author Tony
 *
 */
public interface MapTile {

	/**
	 * The type of surface the world {@link ImageTile} has.
	 * 
	 * @author Tony
	 *
	 */
	public static enum SurfaceType {
		UNKNOWN,
		
		CEMENT,
		METAL,
		WOOD,
		GRASS,
		DIRT,
		SAND,
		WATER,		
		;
		
		private static SurfaceType[] values = values();
		
		public static SurfaceType fromId(int id) {
			if(id < 0 || id >= values().length) {
				return UNKNOWN;
			}
			
			return values[id];
		}
		
		public static SurfaceType fromString(String type) {
			SurfaceType result = UNKNOWN;
			try {
				result = SurfaceType.valueOf(type.toUpperCase());			
			}
			catch(IllegalArgumentException e) {				
			}
			return result;
		}
	}
	
	public static enum CollisionMask {
		NO_COLLISION(0) {						
			@Override
			public boolean pointCollide(Rectangle a, int x, int y) {			
				return false;
			}
			
			@Override
			public boolean rectCollide(Rectangle a, OBB oob) {			
				return false;
			}
			
			@Override
			public boolean rectCollide(Rectangle a, Rectangle b) {			
				return false;
			}
		},
		ALL_SOLID(1) {			
			
			@Override
			public boolean pointCollide(Rectangle a, int x, int y) {
				return true;
			}
			
			@Override
			public boolean rectCollide(Rectangle a, OBB oob) {			
				return true;
			}
			
			@Override
			public boolean rectCollide(Rectangle a, Rectangle b) {			
				return true;
			}
		},
		
		WEST_HALF_SOLID(2) {
			
			@Override
			public boolean pointCollide(Rectangle a, int x, int y) {
				a.width /= 2;
				return a.contains(x, y);
			}
			
			@Override
			public boolean rectCollide(Rectangle a, OBB oob) {
				a.width /= 2;
				return oob.intersects(a);
			}
			
			@Override
			public boolean rectCollide(Rectangle a, Rectangle b) {
				a.width /= 2;
				return a.intersects(b);
			}
		},
		EAST_HALF_SOLID(3) {
			@Override
			public boolean pointCollide(Rectangle a, int x, int y) {
				a.x += (a.width/2);
				a.width /= 2;
				return a.contains(x, y);
			}
			
			@Override
			public boolean rectCollide(Rectangle a, OBB oob) {
				a.x += (a.width/2);
				a.width /= 2;
				return oob.intersects(a);
			}
			
			@Override
			public boolean rectCollide(Rectangle a, Rectangle b) {
				a.x += (a.width/2);
				a.width /= 2;
				return a.intersects(b);
			}
			
		},
		NORTH_HALF_SOLID(4) {
			
			@Override
			public boolean pointCollide(Rectangle a, int x, int y) {
				a.height /= 2;
				return a.contains(x, y);
			}
			
			@Override
			public boolean rectCollide(Rectangle a, OBB oob) {
				a.height /= 2;
				return oob.intersects(a);
			}
			
			@Override
			public boolean rectCollide(Rectangle a, Rectangle b) {
				a.height /= 2;
				return a.intersects(b);
			}
						
		},
		SOUTH_HALF_SOLID(5) {
			@Override
			public boolean pointCollide(Rectangle a, int x, int y) {
				a.y += (a.height/2);
				a.height /= 2;
				return a.contains(x, y);
			}
			

			@Override
			public boolean rectCollide(Rectangle a, OBB oob) {
				a.y += (a.height/2);
				a.height /= 2;
				return oob.intersects(a);
			}		
			
			@Override
			public boolean rectCollide(Rectangle a, Rectangle b) {
				a.y += (a.height/2);
				a.height /= 2;
				return a.intersects(b);
			}		
		},
		
		NORTH_WEST_HALF_SOLID(6) {
			@Override
			public boolean pointCollide(Rectangle a, int x, int y) {				
				int height = a.height;
								
				a.height /= 2;
				boolean north = a.contains(x, y);
				if(!north) {
					
					a.height = height;
					a.width /= 2;
					
					return a.contains(x,y);
				}
				return true;
			}
			
			@Override
			public boolean rectCollide(Rectangle a, OBB oob) {
				int height = a.height;
								
				a.height /= 2;
				boolean north = oob.intersects(a);
				if(!north) {
					
					a.height = height;
					a.width /= 2;
					
					return oob.intersects(a);
				}
				return true;
			}
			
			@Override
			public boolean rectCollide(Rectangle a, Rectangle b) {
				int height = a.height;
								
				a.height /= 2;
				boolean north = a.intersects(b);
				if(!north) {
					
					a.height = height;
					a.width /= 2;
					
					return a.intersects(b);
				}
				return true;
			}
		},
		NORTH_EAST_HALF_SOLID(7) {
			@Override
			public boolean pointCollide(Rectangle a, int x, int y) {				
				int width = a.width;
				int height = a.height;
								
				a.height /= 2;
				boolean north = a.contains(x, y);
				if(!north) {
					
					a.height = height;
					a.x += (width/2);
					a.width /= 2;
					
					return a.contains(x,y);
				}
				return true;
			}
			
			@Override
			public boolean rectCollide(Rectangle a, OBB oob) {
				int width = a.width;
				int height = a.height;
								
				a.height /= 2;
				boolean north = oob.intersects(a);
				if(!north) {
					
					a.height = height;
					a.x += (width/2);
					a.width /= 2;
					
					return oob.intersects(a);
				}
				return true;
			}
			
			@Override
			public boolean rectCollide(Rectangle a, Rectangle b) {
				int width = a.width;
				int height = a.height;
								
				a.height /= 2;
				boolean north = a.intersects(b);
				if(!north) {
					
					a.height = height;
					a.x += (width/2);
					a.width /= 2;
					
					return a.intersects(b);
				}
				return true;
			}
		},
		SOUTH_WEST_HALF_SOLID(8) {
			@Override
			public boolean pointCollide(Rectangle a, int x, int y) {				
				int height = a.height;
								
				a.height /= 2;
				a.y += a.height;
				
				boolean south = a.contains(x, y);
				if(!south) {
					
					a.height = height;					
					a.width /= 2;
					
					return a.contains(x,y);
				}
				return true;
			}
			
			@Override
			public boolean rectCollide(Rectangle a, OBB oob) {
				int height = a.height;
								
				a.height /= 2;
				a.y += a.height;
				
				boolean south = oob.intersects(a);
				if(!south) {
					
					a.height = height;					
					a.width /= 2;
					
					return oob.intersects(a);
				}
				return true;
			}
			
			@Override
			public boolean rectCollide(Rectangle a, Rectangle b) {
				int height = a.height;
								
				a.height /= 2;
				a.y += a.height;
				
				boolean south = a.intersects(b);
				if(!south) {
					
					a.height = height;					
					a.width /= 2;
					
					return a.intersects(b);
				}
				return true;
			}
		},
		SOUTH_EAST_HALF_SOLID(9) {
			@Override
			public boolean pointCollide(Rectangle a, int x, int y) {
				int ax = a.x;
				int ay = a.y;
				int width = a.width;
				int height = a.height;
				
				if(SOUTH_HALF_SOLID.pointCollide(a, x, y)) {
					return true;
				}
				
				a.set(ax, ay, width, height);
				if(EAST_HALF_SOLID.pointCollide(a, x, y)) {
					return true;
				}
				
				return false;				
			}
			
			@Override
			public boolean rectCollide(Rectangle a, OBB b) {										
				int ax = a.x;
				int ay = a.y;
				int width = a.width;
				int height = a.height;
				
				if(SOUTH_HALF_SOLID.rectCollide(a, b)) {
					return true;
				}
				
				a.set(ax, ay, width, height);
				if(EAST_HALF_SOLID.rectCollide(a, b)) {
					return true;
				}
				
				return false;
				
			}
			
			@Override
			public boolean rectCollide(Rectangle a, Rectangle b) {										
				int ax = a.x;
				int ay = a.y;
				int width = a.width;
				int height = a.height;
				
				if(SOUTH_HALF_SOLID.rectCollide(a, b)) {
					return true;
				}
				
				a.set(ax, ay, width, height);
				if(EAST_HALF_SOLID.rectCollide(a, b)) {
					return true;
				}
				
				return false;
				
			}
		},
		
		
		NORTH_SLICE_SOLID(10) {
			@Override
			public boolean pointCollide(Rectangle a, int x, int y) {				
				a.height = 5;				
				return a.contains(x, y);				
			}
			
			@Override
			public boolean rectCollide(Rectangle a, OBB oob) {						
				a.height = 5;				
				return oob.intersects(a);
				
			}
			
			@Override
			public boolean rectCollide(Rectangle a, Rectangle b) {						
				a.height = 5;				
				return a.intersects(b);
				
			}
		},
		SOUTH_SLICE_SOLID(11) {
			@Override
			public boolean pointCollide(Rectangle a, int x, int y) {				
				a.y += a.height - 5;
				a.height = 5;					
				return a.contains(x, y);				
			}
			
			@Override
			public boolean rectCollide(Rectangle a, OBB oob) {						
				a.y += a.height - 5;
				a.height = 5;					
				return oob.intersects(a);
				
			}
			
			@Override
			public boolean rectCollide(Rectangle a, Rectangle b) {						
				a.y += a.height - 5;
				a.height = 5;					
				return a.intersects(b);
				
			}
		},
		WEST_SLICE_SOLID(12) {
			@Override
			public boolean pointCollide(Rectangle a, int x, int y) {								
				a.width = 5;					
				return a.contains(x, y);				
			}
			
			@Override
			public boolean rectCollide(Rectangle a, OBB oob) {										
				a.width = 5;					
				return oob.intersects(a);
				
			}
			
			@Override
			public boolean rectCollide(Rectangle a, Rectangle b) {										
				a.width = 5;					
				return a.intersects(b);
				
			}
		},
		EAST_SLICE_SOLID(13) {
			@Override
			public boolean pointCollide(Rectangle a, int x, int y) {
				a.x += a.width - 5;
				a.width = 5;					
				return a.contains(x, y);				
			}
			
			@Override
			public boolean rectCollide(Rectangle a, OBB oob) {										
				a.x += a.width - 5;
				a.width = 5;					
				return oob.intersects(a);
				
			}
			
			@Override
			public boolean rectCollide(Rectangle a, Rectangle b) {										
				a.x += a.width - 5;
				a.width = 5;					
				return a.intersects(b);
				
			}
		},
		
		NORTH_EAST_SLICE_SOLID(14) {
			@Override
			public boolean pointCollide(Rectangle a, int x, int y) {
				int ax = a.x;
				int ay = a.y;
				int width = a.width;
				int height = a.height;
				
				if(NORTH_SLICE_SOLID.pointCollide(a, x, y)) {
					return true;
				}
				
				a.set(ax, ay, width, height);
				if(EAST_SLICE_SOLID.pointCollide(a, x, y)) {
					return true;
				}
				
				return false;				
			}
			
			@Override
			public boolean rectCollide(Rectangle a, OBB oob) {										
				int ax = a.x;
				int ay = a.y;
				int width = a.width;
				int height = a.height;
				
				if(NORTH_SLICE_SOLID.rectCollide(a, oob)) {
					return true;
				}
				
				a.set(ax, ay, width, height);
				if(EAST_SLICE_SOLID.rectCollide(a, oob)) {
					return true;
				}
				
				return false;
				
			}
			
			@Override
			public boolean rectCollide(Rectangle a, Rectangle b) {										
				int ax = a.x;
				int ay = a.y;
				int width = a.width;
				int height = a.height;
				
				if(NORTH_SLICE_SOLID.rectCollide(a, b)) {
					return true;
				}
				
				a.set(ax, ay, width, height);
				if(EAST_SLICE_SOLID.rectCollide(a, b)) {
					return true;
				}
				
				return false;
				
			}
		},
		NORTH_WEST_SLICE_SOLID(15) {
			@Override
			public boolean pointCollide(Rectangle a, int x, int y) {
				int ax = a.x;
				int ay = a.y;
				int width = a.width;
				int height = a.height;
				
				if(NORTH_SLICE_SOLID.pointCollide(a, x, y)) {
					return true;
				}
				
				a.set(ax, ay, width, height);
				if(WEST_SLICE_SOLID.pointCollide(a, x, y)) {
					return true;
				}
				
				return false;				
			}
			
			@Override
			public boolean rectCollide(Rectangle a, OBB oob) {										
				int ax = a.x;
				int ay = a.y;
				int width = a.width;
				int height = a.height;
				
				if(NORTH_SLICE_SOLID.rectCollide(a, oob)) {
					return true;
				}
				
				a.set(ax, ay, width, height);
				if(WEST_SLICE_SOLID.rectCollide(a, oob)) {
					return true;
				}
				
				return false;
				
			}
			
			@Override
			public boolean rectCollide(Rectangle a, Rectangle b) {										
				int ax = a.x;
				int ay = a.y;
				int width = a.width;
				int height = a.height;
				
				if(NORTH_SLICE_SOLID.rectCollide(a, b)) {
					return true;
				}
				
				a.set(ax, ay, width, height);
				if(WEST_SLICE_SOLID.rectCollide(a, b)) {
					return true;
				}
				
				return false;
				
			}
		},
		SOUTH_WEST_SLICE_SOLID(16) {
			@Override
			public boolean pointCollide(Rectangle a, int x, int y) {
				int ax = a.x;
				int ay = a.y;
				int width = a.width;
				int height = a.height;
				
				if(SOUTH_SLICE_SOLID.pointCollide(a, x, y)) {
					return true;
				}
				
				a.set(ax, ay, width, height);
				if(WEST_SLICE_SOLID.pointCollide(a, x, y)) {
					return true;
				}
				
				return false;				
			}
			
			@Override
			public boolean rectCollide(Rectangle a, OBB oob) {										
				int ax = a.x;
				int ay = a.y;
				int width = a.width;
				int height = a.height;
				
				if(SOUTH_SLICE_SOLID.rectCollide(a, oob)) {
					return true;
				}
				
				a.set(ax, ay, width, height);
				if(WEST_SLICE_SOLID.rectCollide(a, oob)) {
					return true;
				}
				
				return false;
				
			}
			
			@Override
			public boolean rectCollide(Rectangle a, Rectangle b) {										
				int ax = a.x;
				int ay = a.y;
				int width = a.width;
				int height = a.height;
				
				if(SOUTH_SLICE_SOLID.rectCollide(a, b)) {
					return true;
				}
				
				a.set(ax, ay, width, height);
				if(WEST_SLICE_SOLID.rectCollide(a, b)) {
					return true;
				}
				
				return false;
				
			}
		},		
		SOUTH_EAST_SLICE_SOLID(17) {
			@Override
			public boolean pointCollide(Rectangle a, int x, int y) {
				int ax = a.x;
				int ay = a.y;
				int width = a.width;
				int height = a.height;
				
				if(SOUTH_SLICE_SOLID.pointCollide(a, x, y)) {
					return true;
				}
				
				a.set(ax, ay, width, height);
				if(EAST_SLICE_SOLID.pointCollide(a, x, y)) {
					return true;
				}
				
				return false;				
			}
			
			@Override
			public boolean rectCollide(Rectangle a, OBB oob) {										
				int ax = a.x;
				int ay = a.y;
				int width = a.width;
				int height = a.height;
				
				if(SOUTH_SLICE_SOLID.rectCollide(a, oob)) {
					return true;
				}
				
				a.set(ax, ay, width, height);
				if(EAST_SLICE_SOLID.rectCollide(a, oob)) {
					return true;
				}
				
				return false;
				
			}
			
			@Override
			public boolean rectCollide(Rectangle a, Rectangle b) {										
				int ax = a.x;
				int ay = a.y;
				int width = a.width;
				int height = a.height;
				
				if(SOUTH_SLICE_SOLID.rectCollide(a, b)) {
					return true;
				}
				
				a.set(ax, ay, width, height);
				if(EAST_SLICE_SOLID.rectCollide(a, b)) {
					return true;
				}
				
				return false;
				
			}
		},
		
		NORTH_SOUTH_SLICE_SOLID(18) {
			@Override
			public boolean pointCollide(Rectangle a, int x, int y) {
				int ax = a.x;
				int ay = a.y;
				int width = a.width;
				int height = a.height;
				
				if(NORTH_SLICE_SOLID.pointCollide(a, x, y)) {
					return true;
				}
				
				a.set(ax, ay, width, height);
				if(SOUTH_SLICE_SOLID.pointCollide(a, x, y)) {
					return true;
				}
				
				return false;				
			}
			
			@Override
			public boolean rectCollide(Rectangle a, OBB oob) {										
				int ax = a.x;
				int ay = a.y;
				int width = a.width;
				int height = a.height;
				
				if(NORTH_SLICE_SOLID.rectCollide(a, oob)) {
					return true;
				}
				
				a.set(ax, ay, width, height);
				if(SOUTH_SLICE_SOLID.rectCollide(a, oob)) {
					return true;
				}
				
				return false;
				
			}
			
			@Override
			public boolean rectCollide(Rectangle a, Rectangle b) {										
				int ax = a.x;
				int ay = a.y;
				int width = a.width;
				int height = a.height;
				
				if(NORTH_SLICE_SOLID.rectCollide(a, b)) {
					return true;
				}
				
				a.set(ax, ay, width, height);
				if(SOUTH_SLICE_SOLID.rectCollide(a, b)) {
					return true;
				}
				
				return false;
				
			}
		},
		WEST_EAST_SLICE_SOLID(19) {
			@Override
			public boolean pointCollide(Rectangle a, int x, int y) {
				int ax = a.x;
				int ay = a.y;
				int width = a.width;
				int height = a.height;
				
				if(WEST_SLICE_SOLID.pointCollide(a, x, y)) {
					return true;
				}
				
				a.set(ax, ay, width, height);
				if(EAST_SLICE_SOLID.pointCollide(a, x, y)) {
					return true;
				}
				
				return false;				
			}
			
			@Override
			public boolean rectCollide(Rectangle a, OBB oob) {										
				int ax = a.x;
				int ay = a.y;
				int width = a.width;
				int height = a.height;
				
				if(WEST_SLICE_SOLID.rectCollide(a, oob)) {
					return true;
				}
				
				a.set(ax, ay, width, height);
				if(EAST_SLICE_SOLID.rectCollide(a, oob)) {
					return true;
				}
				
				return false;
				
			}
			
			@Override
			public boolean rectCollide(Rectangle a, Rectangle b) {										
				int ax = a.x;
				int ay = a.y;
				int width = a.width;
				int height = a.height;
				
				if(WEST_SLICE_SOLID.rectCollide(a, b)) {
					return true;
				}
				
				a.set(ax, ay, width, height);
				if(EAST_SLICE_SOLID.rectCollide(a, b)) {
					return true;
				}
				
				return false;
				
			}
		},
			
		SOUTH_EAST_BOX_SOLID(20) {
			@Override
			public boolean pointCollide(Rectangle a, int x, int y) {
				int ax = a.x;
				int ay = a.y;
				int width = a.width;
				int height = a.height;
				
				a.set(ax+width/2, ay+height/2, width/2, height/2);					
				return a.contains(x,y);				
			}
			
			@Override
			public boolean rectCollide(Rectangle a, OBB oob) {										
				int ax = a.x;
				int ay = a.y;
				int width = a.width;
				int height = a.height;
				
				a.set(ax+width/2, ay+height/2, width/2, height/2);					
				return oob.intersects(a);								
			}
			
			@Override
			public boolean rectCollide(Rectangle a, Rectangle b) {										
				int ax = a.x;
				int ay = a.y;
				int width = a.width;
				int height = a.height;
				
				a.set(ax+width/2, ay+height/2, width/2, height/2);					
				return a.intersects(b);								
			}
				
		},
		
		NORTH_WEST_BOX_SOLID(21) {
			@Override
			public boolean pointCollide(Rectangle a, int x, int y) {
				int ax = a.x;
				int ay = a.y;
				int width = a.width;
				int height = a.height;
				
				a.set(ax, ay, width/2, height/2);					
				return a.contains(x,y);				
			}
			
			@Override
			public boolean rectCollide(Rectangle a, OBB oob) {										
				int ax = a.x;
				int ay = a.y;
				int width = a.width;
				int height = a.height;
				
				a.set(ax, ay, width/2, height/2);					
				return oob.intersects(a);								
			}
			
			@Override
			public boolean rectCollide(Rectangle a, Rectangle b) {										
				int ax = a.x;
				int ay = a.y;
				int width = a.width;
				int height = a.height;
				
				a.set(ax, ay, width/2, height/2);					
				return a.intersects(b);								
			}
				
		},
		
		NORTH_EAST_BOX_SOLID(22) {
			@Override
			public boolean pointCollide(Rectangle a, int x, int y) {
				int ax = a.x;
				int ay = a.y;
				int width = a.width;
				int height = a.height;
				
				a.set(ax+width/2, ay, width/2, height/2);					
				return a.contains(x,y);				
			}
			
			@Override
			public boolean rectCollide(Rectangle a, OBB oob) {										
				int ax = a.x;
				int ay = a.y;
				int width = a.width;
				int height = a.height;
				
				a.set(ax+width/2, ay, width/2, height/2);					
				return oob.intersects(a);								
			}
			
			@Override
			public boolean rectCollide(Rectangle a, Rectangle b) {										
				int ax = a.x;
				int ay = a.y;
				int width = a.width;
				int height = a.height;
				
				a.set(ax+width/2, ay, width/2, height/2);					
				return a.intersects(b);								
			}
				
		},
		
		SOUTH_WEST_BOX_SOLID(23) {
			@Override
			public boolean pointCollide(Rectangle a, int x, int y) {
				int ax = a.x;
				int ay = a.y;
				int width = a.width;
				int height = a.height;
				
				a.set(ax, ay+height/2, width/2, height/2);					
				return a.contains(x,y);				
			}
			
			@Override
			public boolean rectCollide(Rectangle a, OBB oob) {										
				int ax = a.x;
				int ay = a.y;
				int width = a.width;
				int height = a.height;
				
				a.set(ax, ay+height/2, width/2, height/2);					
				return oob.intersects(a);								
			}
			
			@Override
			public boolean rectCollide(Rectangle a, Rectangle b) {										
				int ax = a.x;
				int ay = a.y;
				int width = a.width;
				int height = a.height;
				
				a.set(ax, ay+height/2, width/2, height/2);					
				return a.intersects(b);								
			}
				
		},
		
		MIDDLE_VERTICAL_SLICE_SOLID(24) {
			@Override
			public boolean pointCollide(Rectangle a, int x, int y) {
				a.x += a.width/2;
				a.width = 5;					
				return a.contains(x, y);				
			}
			
			@Override
			public boolean rectCollide(Rectangle a, OBB oob) {										
				a.x += a.width/2;
				a.width = 5;						
				return oob.intersects(a);
				
			}
			
			@Override
			public boolean rectCollide(Rectangle a, Rectangle b) {										
				a.x += a.width/2;
				a.width = 5;						
				return a.intersects(b);
				
			}
		},
		
		MIDDLE_HORIZONTAL_SLICE_SOLID(25) {
			@Override
			public boolean pointCollide(Rectangle a, int x, int y) {
				a.y += a.height/2;
				a.height = 5;					
				return a.contains(x, y);				
			}
			
			@Override
			public boolean rectCollide(Rectangle a, OBB oob) {
				a.y += a.height/2;
				a.height = 5;						
				return oob.intersects(a);
			}
			
			@Override
			public boolean rectCollide(Rectangle a, Rectangle b) {										
				a.y += a.height/2;
				a.height = 5;						
				return a.intersects(b);
				
			}
		},
		
		UPPER_LEFT_TRIANGLE(26) {
			
			@Override
			public boolean pointCollide(Rectangle a, int x, int y) {
				float x0 = a.x;
				float y0 = a.y;
				float x1 = a.x + a.width;
				float y1 = a.y;
				float x2 = a.x;
				float y2 = a.y + a.height;
								
				return Triangle.pointIntersectsTriangle(x, y, x0, y0, x1, y1, x2, y2);
			}
			
			@Override
			public boolean rectCollide(Rectangle a, OBB oob) {
				return true;
			}
			
			@Override
			public boolean rectCollide(Rectangle a, Rectangle b) {
				float x0 = a.x;
				float y0 = a.y;
				float x1 = a.x + a.width;
				float y1 = a.y;
				float x2 = a.x;
				float y2 = a.y + a.height;
								
				return Triangle.rectangleIntersectsTriangle(b, x0, y0, x1, y1, x2, y2);
			}
		},
		
		UPPER_RIGHT_TRIANGLE(27) {
			
			@Override
			public boolean pointCollide(Rectangle a, int x, int y) {
				float x0 = a.x;
				float y0 = a.y;
				float x1 = a.x + a.width;
				float y1 = a.y;
				float x2 = a.x + a.width;
				float y2 = a.y + a.height;
								
				return Triangle.pointIntersectsTriangle(x, y, x0, y0, x1, y1, x2, y2);
			}
			
			@Override
			public boolean rectCollide(Rectangle a, OBB oob) {
				return true;
			}
			
			@Override
			public boolean rectCollide(Rectangle a, Rectangle b) {
				float x0 = a.x;
				float y0 = a.y;
				float x1 = a.x + a.width;
				float y1 = a.y;
				float x2 = a.x + a.width;
				float y2 = a.y + a.height;
				
				return Triangle.rectangleIntersectsTriangle(b, x0, y0, x1, y1, x2, y2);
			}
		},
		
		BOTTOM_LEFT_TRIANGLE(28) {
			
			@Override
			public boolean pointCollide(Rectangle a, int x, int y) {
				float x0 = a.x;
				float y0 = a.y;
				float x1 = a.x + a.width;
				float y1 = a.y + a.height;
				float x2 = a.x;
				float y2 = a.y + a.height;
								
				return Triangle.pointIntersectsTriangle(x, y, x0, y0, x1, y1, x2, y2);
			}
			
			@Override
			public boolean rectCollide(Rectangle a, OBB oob) {
				return true;
			}
			
			@Override
			public boolean rectCollide(Rectangle a, Rectangle b) {
				float x0 = a.x;
				float y0 = a.y;
				float x1 = a.x + a.width;
				float y1 = a.y + a.height;
				float x2 = a.x;
				float y2 = a.y + a.height;
				
				return Triangle.rectangleIntersectsTriangle(b, x0, y0, x1, y1, x2, y2);
			}
		},
		
		BOTTOM_RIGHT_TRIANGLE(29) {
			
			@Override
			public boolean pointCollide(Rectangle a, int x, int y) {
				float x0 = a.x + a.width;
				float y0 = a.y;
				float x1 = a.x + a.width;
				float y1 = a.y + a.height;
				float x2 = a.x;
				float y2 = a.y + a.height;
								
				return Triangle.pointIntersectsTriangle(x, y, x0, y0, x1, y1, x2, y2);
			}
			
			@Override
			public boolean rectCollide(Rectangle a, OBB oob) {
				return true;
			}
			
			@Override
			public boolean rectCollide(Rectangle a, Rectangle b) {
				float x0 = a.x + a.width;
				float y0 = a.y;
				float x1 = a.x + a.width;
				float y1 = a.y + a.height;
				float x2 = a.x;
				float y2 = a.y + a.height;
				
				return Triangle.rectangleIntersectsTriangle(b, x0, y0, x1, y1, x2, y2);
			}
		},
		
		CENTER_CIRCLE(30) {
			
			@Override
			public boolean pointCollide(Rectangle a, int x, int y) {
				float circleX = a.x + a.width/2;
				float circleY = a.y + a.height/2;
				float radius = 16f;
								
				return Circle.circleContainsPoint(circleX, circleY, radius, x, y);
			}
			
			@Override
			public boolean rectCollide(Rectangle a, OBB oob) {
				return true;
			}
			
			@Override
			public boolean rectCollide(Rectangle a, Rectangle b) {
				float circleX = a.x + a.width/2;
				float circleY = a.y + a.height/2;
				float radius = 16f;
								
				return Circle.circleIntersectsRect(circleX, circleY, radius, b);
			}
		}
		
		;
		
		private int id;
		
		private CollisionMask(int id) {
			this.id = id;
		}
		
//		public abstract void setBounds(Rectangle rect);
		
		public abstract boolean rectCollide(Rectangle a, Rectangle b);
		public abstract boolean rectCollide(Rectangle a, OBB oob);
		public abstract boolean pointCollide(Rectangle a, int x, int y);
		
		
		public static CollisionMask fromId(int id) {
			for(CollisionMask m : values()) {
				if(m.id == id) {
					return m;
				}
			}
			return null;
		}
	}
	
	/**
	 * Flip masks
	 */
	public static final int isFlippedHorizontal=(1<<0), isFlippedVert=(1<<1), isFlippedDiagnally=(1<<2);
	
	int TILE_INVISIBLE = 0;
	int TILE_VISIBLE = 1;
	int TILE_NORTH_INVISIBLE = 2;
	int TILE_SOUTH_INVISIBLE = 4;
	int TILE_EAST_INVISIBLE = 8;
	int TILE_WEST_INVISIBLE = 16;

	/**
	 * @return the surfaceType
	 */
	SurfaceType getSurfaceType();

	/**
	 * @param surfaceType the surfaceType to set
	 */
	void setSurfaceType(SurfaceType surfaceType);

	/**
	 * @return the layer
	 */
	int getLayer();

	/**
	 * @return the height
	 */
	int getHeight();

	/**
	 * @return the width
	 */
	int getWidth();
	
	int getX();

	int getY();
	
	int getIsoX();
	int getIsoY();
	void setIsoPosition(int x, int y);

	public void setTerrainTileData(TerrainTileData terrain);
	public TerrainTileData geTerrainTileData();
	
	/**
	 * @return the xIndex
	 */
	int getXIndex();

	/**
	 * @return the yIndex
	 */
	int getYIndex();

	/**
	 * @param collisionMask the collisionMask to set
	 */
	void setCollisionMask(CollisionMask collisionMask);

	/**
	 * @return the collisionMask
	 */
	CollisionMask getCollisionMask();

	void setCollisionMaskById(int id);

	/**
	 * @param mask the mask to set
	 */
	void setMask(int mask);

	/**
	 * @return the mask
	 */
	int getMask();

	/**
	 * @param heightMask the heightMask to set
	 */
	void setHeightMask(int heightMask);

	/**
	 * @return the heightMask
	 */
	int getHeightMask();

	/**
	 * @return the isDestroyed
	 */
	boolean isDestroyed();

	/**
	 * @param isDestroyed the isDestroyed to set
	 */
	void setDestroyed(boolean isDestroyed);

	boolean isFlippedHorizontal();

	boolean isFlippedVertical();

	boolean isFlippedDiagnally();

	void setFlips(boolean isFlippedHorizontal, boolean isFlippedVert, boolean isFlippedDiagnally);

	/**
	 * Sets the index position
	 * @param x
	 * @param y
	 */
	void setIndexPosition(int x, int y);

	/**
	 * @param position the position to set
	 */
	void setPosition(int x, int y);

	void setRenderingPosition(int x, int y);

	/**
	 * @return the renderX
	 */
	int getRenderX();

	/**
	 * @return the renderY
	 */
	int getRenderY();

	/**
	 * Determines if a point collides with this tile 
	 * @param x
	 * @param y
	 * @return true if the point collides, false otherwise
	 */
	boolean pointCollide(int x, int y);

	/**
	 * Determines if the {@link Rectangle} collides with this tile
	 * @param rect
	 * @return true if the rectangle collides, false otherwise
	 */
	boolean rectCollide(Rectangle rect);

	void setSize(int width, int height);
	
	/**
	 * @return the bounds
	 */
	Rectangle getBounds();

	/* (non-Javadoc)
	 * @see leola.live.gfx.Renderable#update(leola.live.TimeStep)
	 */
	void update(TimeStep timeStep);

	/* (non-Javadoc)
	 * @see leola.live.gfx.Renderable#render(leola.live.gfx.Canvas, leola.live.gfx.Camera, long)
	 */
	void render(Canvas canvas, Camera camera, float alpha);

}
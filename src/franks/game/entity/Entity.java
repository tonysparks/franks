/*
 * see license.txt 
 */
package franks.game.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import franks.game.Cell;
import franks.game.Command;
import franks.game.CommandQueue;
import franks.game.CommandQueue.CommandRequest;
import franks.game.Game;
import franks.game.World;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.gfx.Renderable;
import franks.map.Map;
import franks.map.MapTile;
import franks.math.Rectangle;
import franks.math.Vector2f;
import franks.util.TimeStep;
import leola.vm.types.LeoMap;
import leola.vm.types.LeoObject;

/**
 * @author Tony
 *
 */
public class Entity implements Renderable {

	private static float MaxNeighborDistance = (float)Math.sqrt(World.RegionWidth*World.RegionWidth + World.RegionHeight*World.RegionHeight);
	/**
	 * Type of entity this is
	 * 
	 * @author Tony
	 *
	 */
	public static enum Type {
		HUMAN,
		TREE,
		STONE,
		BERRY_BUSH,
		
		BUILDING,
	}
	
	public static enum Direction {
		NORTH,
		NORTH_EAST,
		EAST,
		SOUTH_EAST,
		SOUTH,
		SOUTH_WEST,
		WEST,
		NORTH_WEST,
		;
		
		public static Direction getDirection(Vector2f v) {
			return getDirection(v.x, v.y);
		}
		
		public static Direction getDirection(float x, float y) {
//			Vector2f v = new Vector2f(x,y);
//			if(!v.isZero())System.out.println(v);
			
			final float threshold = 0.5f;
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
	
	public static enum State {
		IDLE,
		WALKING,
		COLLECTING,
		DROPPING,
		//DYING,
		DEAD,
		ATTACKING,
	}
	
	private Type type;
	private String name;
	protected Vector2f pos;
	protected Vector2f renderPos;
	private Vector2f centerPos;
	private Vector2f tilePos;
	protected Vector2f scratch;
	protected Rectangle bounds;
	private boolean isSelected;
	
	private List<Command> availableCommands;
	
	protected Game game;
	protected World world;
	
	private LeoMap attributes;
	
	protected int health;
	private boolean isDeleted;
		
	private State currentState;
	private Direction currentDirection;
	
	private CommandQueue commandQueue;
	
	public Entity(Game game, Type type, Vector2f pos, int width, int height) {
		this(game, type.name(), type, pos, width, height);
	}
	
	public Entity(Game game, String name, Type type, Vector2f pos, int width, int height) {
		this.game = game;
		this.name = name;
		this.type = type;	
		this.pos = pos;
		this.centerPos = new Vector2f();
		this.renderPos = new Vector2f();
		this.scratch = new Vector2f();
		
		this.tilePos = new Vector2f();
		
		this.bounds = new Rectangle(width, height);
		this.bounds.setLocation(pos);
		
		this.availableCommands = new ArrayList<>();
		this.attributes = new LeoMap();
		this.isDeleted = false;
		
		this.world = game.getWorld();
		
		this.commandQueue = new CommandQueue(game);
		
		this.currentState = State.IDLE;
		this.currentDirection = Direction.SOUTH;
		
		this.health = 5;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the commandQueue
	 */
	public CommandQueue getCommandQueue() {
		return commandQueue;
	}
	
	/**
	 * @return the currentDirection
	 */
	public Direction getCurrentDirection() {
		return currentDirection;
	}
	
	/**
	 * @return the currentState
	 */
	public State getCurrentState() {
		return currentState;
	}
	
	/**
	 * @param currentDirection the currentDirection to set
	 */
	public void setCurrentDirection(Direction currentDirection) {
		this.currentDirection = currentDirection;
	}
	
	/**
	 * @param currentState the currentState to set
	 */
	public void setCurrentState(State currentState) {
		this.currentState = currentState;		
	}
	
	//public void consumeResource(String resource)
	
	public void calculateStatus() {
		
	}
	
	/**
	 * Check to see the status of this entity
	 */
	public void checkStatus() {		
	}
	
	/**
	 * @return the health
	 */
	public int getHealth() {
		return health;
	}
	
	/**
	 * @return the isAlive
	 */
	public boolean isAlive() {
		return !this.currentState.equals(State.DEAD);
	}
	
	public void delete() {
		this.isDeleted = true;
	}
	
	public boolean isDeleted() {
		return this.isDeleted;
	}
	
	
	public void kill() {
		if(this.isAlive()) {
			doAction(new CommandRequest(game, "die", this));
		}
	}
	
	public void damage() {
		this.health--;
		if(this.health<=0) {
			kill();
		}
	}
	
	/**
	 * @return the attributes
	 */
	public LeoMap getAttributes() {
		return attributes;
	}
	
	public boolean hasAttribute(String name) {
		return LeoObject.isTrue(attributes.getByString(name));
	}
	
	public boolean isWithinRange(Entity other) {
		Vector2f delta = getPos().subtract(other.getPos());
		return delta.length() <= MaxNeighborDistance;
	}
	
	public int getResourceCollectionPower(String resource) {
		LeoObject value = attributes.getByString("resourceCollectionPower" + resource);
		if(LeoObject.isTrue(value)) {
			return value.asInt();
		}
		return 0;
	}
	
	public int deltaAttribute(String attr, int delta) {
		int value = attributeAsInt(attr);
		value += delta;
		if(value<0) {
			value = 0;
		}
		
		attribute(attr, value);
		return value;
	}
	
	public Entity attribute(String name, int value) {
		attributes.putByString(name, LeoObject.valueOf(value));
		return this;
	}
	
	public Entity attribute(String name, float value) {
		attributes.putByString(name, LeoObject.valueOf(value));
		return this;
	}
	
	public Entity attribute(String name, String value) {
		attributes.putByString(name, LeoObject.valueOf(value));
		return this;
	}
	
	public String attributeAsString(String name) {
		LeoObject value = attributes.getByString(name);
		if(LeoObject.isTrue(value)) {
			return value.toString();
		}
		return null;
	}
	
	public int attributeAsInt(String name) {
		LeoObject value = attributes.getByString(name);
		if(LeoObject.isTrue(value)) {
			return value.asInt();
		}
		return 0;
	}
	
	public float attributeAsFloat(String name) {
		LeoObject value = attributes.getByString(name);
		if(LeoObject.isTrue(value)) {
			return value.asFloat();
		}
		return 0.0f;
	}
	
	/**
	 * @return the game
	 */
	public Game getGame() {
		return game;
	}
	
	public boolean isSelected() {
		return this.isSelected;
	}
	
	public void isSelected(boolean selected) {
		this.isSelected = selected;
	}
	
	public Optional<Command> getCommand(final String name) {
		return this.availableCommands.stream().filter(cmd -> cmd.getName().equalsIgnoreCase(name)).findFirst();
	}
	
	public Entity moveToRegion(int x, int y) {
		this.pos.set(x * world.getRegionWidth(), y * world.getRegionHeight());
		this.bounds.setLocation(pos);
		return this;
	}
	
	
	public Entity moveTo(Vector2f pos) {
		this.pos.set(pos);
		this.bounds.setLocation(pos);
		return this;
	}
	
	public Entity moveTo(int x, int y) {
		this.pos.set(x,y);
		this.bounds.setLocation(pos);
		return this;
	}
	
	public Entity moveTo(float x, float y) {
		this.pos.set(x, y);
		this.bounds.setLocation(pos);
		return this;
	}
	
	public Entity lookAt(float x, float y) {
		scratch.set(x, y);
		return lookAt(scratch);
	}
	
	public Entity lookAt(Vector2f pos) {
		Vector2f.Vector2fSubtract(pos, getCenterPos(), scratch);		
		Vector2f.Vector2fNormalize(scratch, scratch);
		Direction dir = Direction.getDirection(scratch);
		setCurrentDirection(dir);
		return this;
	}
	
	public Entity lookAt(MapTile tile) {
		int tileX = (int)(pos.x / world.getRegionWidth());
		int tileY = (int)(pos.y / world.getRegionHeight());
		int dirX = tile.getXIndex() - tileX;
		int dirY = tile.getYIndex() - tileY;
		setCurrentDirection(Direction.getDirection(dirX, dirY));
		return this;
	}
	
	public boolean hasAction(String actionName) {
		for(Command cmd : this.availableCommands) {
			if(cmd.getName().equals(actionName)) {
				return true;
			}
		}
		return false;
	}
	
	public void queueAction(CommandRequest request) {
		this.commandQueue.add(request);
	}
	
	public void doAction(CommandRequest request) {
		this.commandQueue.cancel();
		queueAction(request);
	}
	
	public boolean canDo(String actionName) {
		return hasAction(actionName);
	}
	
	public Entity addAvailableAction(Command action) {
		this.availableCommands.add(action);
		return this;
	}
	
	public boolean isDead() {
		return getCurrentState().equals(State.DEAD);
	}
	
	/**
	 * @return the availableActions
	 */
	public List<Command> getAvailableActions() {
		return availableCommands;
	}
	
	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}
	
	/**
	 * @return the tilePos
	 */
	public Vector2f getTilePos() {
		float tileX = (pos.x / world.getRegionWidth());
		float tileY = (pos.y / world.getRegionHeight());
		tilePos.set(tileX, tileY);
		return tilePos;
	}
	
	/**
	 * @return the pos
	 */
	public Vector2f getPos() {
		return pos;
	}
	
	public Vector2f getCenterPos() {
		centerPos.set(pos.x + bounds.width/2, pos.y + bounds.height/2);
		return centerPos;
	}
	
	/**
	 * @return the bounds
	 */
	public Rectangle getBounds() {
		return bounds;
	}
	
	public int getZOrder() {
		World world = game.getWorld();		
		int tileY = (int)(pos.y / world.getRegionHeight());
		return tileY ;//+ bounds.height;
//		MapTile tile = game.getWorld().getMapTileByWorldPos(getCenterPos());
//		if(tile!=null) {
//			return tile.getRenderY();
//		}
//		return -1;
	}
	
	/**
	 * Test to see if this Entity currently resides in the supplied {@link Cell}
	 * @param cell
	 * @return true if this entity is inside the supplied cell
	 */
	public boolean inCell(Cell cell) {
		return cell.getBounds().contains(getCenterPos());
	}
	
	
	/**
	 * Returns the distance of this Entity to the Cell in tile units
	 * @param cell
	 * @return the number of tiles that separate these two
	 */
	public int distanceFrom(Cell cell) {
		Rectangle r = cell.getTileBounds();
		int centerX = r.x + r.width / 2;
		int centerY = r.y + r.height / 2;
		
		Vector2f centerPos = getCenterPos();
		
		Map map = game.getMap();
		int tileX = map.worldToTileX((int)centerPos.x);
		int tileY = map.worldToTileY((int)centerPos.y);
		
		return (int)Math.sqrt((centerX - tileX) * (centerX - tileX) + (centerY - tileY) * (centerY - tileY));		
	}
	
	public int distanceFrom(Entity other) {
		Vector2f centerPos = getCenterPos();
		
		Map map = game.getMap();
		int meX = map.worldToTileX((int)centerPos.x);
		int meY = map.worldToTileY((int)centerPos.y);
		
		centerPos = other.getCenterPos();
		int otherX = map.worldToTileX((int)centerPos.x);
		int otherY = map.worldToTileY((int)centerPos.y);
		
		return (int)Math.sqrt((otherX - meX) * (otherX - meX) + (otherY - meY) * (otherY - meY));
	}
	
	public float getDiameter() {
		return (float)Math.sqrt(bounds.width*bounds.width + bounds.height*bounds.height);
	}

	/* (non-Javadoc)
	 * @see newera.gfx.Renderable#update(newera.util.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		this.commandQueue.update(timeStep);
	}
	
	/* (non-Javadoc)
	 * @see newera.gfx.Renderable#render(newera.gfx.Canvas, newera.gfx.Camera, float)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {
		Vector2f cameraPos = camera.getRenderPosition(alpha);
//		float dx = pos.x - cameraPos.x;
//		float dy = pos.y - cameraPos.y;
		
		float dx = -1;
		float dy = -1;
		
//		Vector2f pos = new Vector2f(getCenterPos());
//		pos.x += 64 * 32;
//		//pos.y -= 16;
//		
//		MapTile tile = world.getMapTileByWorldPos(pos);
//		if(tile!=null) {
//			dx = tile.getRenderX();//-cameraPos.x;
//			dy = tile.getRenderY();//-cameraPos.y;
//		}
		
		Vector2f pos = getPos();
		int isoX = (int) (pos.x / world.getRegionWidth());
		int isoY = (int) (pos.y / world.getRegionHeight());
		
		MapTile tile = world.getMap().getTile(0, isoX, isoY);
		if(tile!=null) {
			dx = tile.getIsoX() + 16 - cameraPos.x;
			dy = tile.getIsoY() -  8 - cameraPos.y;
		}
		
//		
//		renderPos.zeroOut();
//		world.isoIndexToWorld(isoX, isoY, renderPos);
//				
//		
//		float dx = this.renderPos.x - cameraPos.x;
//		float dy = this.renderPos.y - cameraPos.y;
//	
//	    canvas.drawString((int)renderPos.x+","+ (int)renderPos.y, dx, dy, 0xffffffff);
		
		doRender(dx, dy, canvas, camera, alpha);
	}
	

	/**
	 * @param dx
	 * @param dy
	 * @param canvas
	 * @param camera
	 * @param alpha
	 */
	protected void doRender(float dx, float dy, Canvas canvas, Camera camera, float alpha) {
		if(isSelected()) {
			canvas.fillCircle(getDiameter()/2.8f, dx, dy, 0xcfffffff);
		}
		//canvas.drawImage(this.image, dx, dy, null);
	}
	
	public Vector2f getRenderPosition(Camera camera, float alpha) {
		Vector2f cameraPos = camera.getRenderPosition(alpha);
		
		World world = game.getWorld();
		Vector2f tilePos = getTilePos();
		
		world.getMap().isoIndexToScreen(tilePos.x, tilePos.y, renderPos);
		Vector2f.Vector2fSubtract(renderPos, cameraPos, renderPos);
		
		renderPos.x -= 32;
		renderPos.y -= 32;
		
		return renderPos;
	}
}


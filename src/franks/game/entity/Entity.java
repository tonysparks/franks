/*
 * see license.txt 
 */
package franks.game.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import franks.game.Cell;
import franks.game.Command;
import franks.game.CommandQueue;
import franks.game.CommandQueue.CommandRequest;
import franks.game.Game;
import franks.game.ActionMeter;
import franks.game.Team;
import franks.game.World;
import franks.game.action.AttackCommand;
import franks.game.action.DieCommand;
import franks.game.action.MovementCommand;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.gfx.Colors;
import franks.gfx.Renderable;
import franks.map.IsometricMap;
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

	private static float MaxNeighborDistance = (float)Math.sqrt(World.TileWidth*World.TileWidth + World.TileHeight*World.TileHeight);
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
	protected Rectangle tileBounds;
	
	private boolean isSelected;
	
	private java.util.Map<String, Command> availableCommands;
	
	protected Game game;
	protected World world;
	
	private LeoMap attributes;
	
	protected int health;
		
	private boolean isDeleted;
		
	private State currentState;
	private Direction currentDirection;
	private Direction desiredDirection;
	 
	
	private EntityModel model;
	
	private ActionMeter meter;
	
	private CommandQueue commandQueue;
	private Team team;
	
	private EntityData data;
				
	public Entity(Game game, Team team, EntityData data) {
		this.game = game;
		this.team = team;
		this.data = data;
		
		this.name = data.name;
		this.type = data.type;	
		
		this.world = game.getWorld();
		
		this.meter = new ActionMeter(data.movements);
		this.commandQueue = new CommandQueue(game);
		
		this.pos = new Vector2f();
		this.centerPos = new Vector2f();
		this.renderPos = new Vector2f();
		this.scratch = new Vector2f();
		
		this.tilePos = new Vector2f();
		
		this.tileBounds = new Rectangle(data.width/World.TileWidth, data.height/World.TileHeight);
		this.tileBounds.setLocation(getTilePos());
		this.bounds = new Rectangle(data.width, data.height);
		this.bounds.setLocation(pos);
		
		this.availableCommands = new HashMap<>();
		this.attributes = new LeoMap();
		this.isDeleted = false;
		
		this.currentState = State.IDLE;
		this.currentDirection = Direction.SOUTH;
		this.desiredDirection = Direction.SOUTH;
		
		this.health = 5;
		
		this.team.addMember(this);
		
		this.model = new EntityModel(game, this, data.graphics);
		
		this.health = data.health;
		
		
		if(data.attackAction!=null) {			
			addAvailableAction(new AttackCommand(game, this, 
									data.attackAction.cost,
									data.attackAction.attackRange,
									data.attackAction.hitPercentage));
		}
		
		if(data.moveAction != null) {
			addAvailableAction(new MovementCommand(game, this, data.moveAction.movementSpeed));
		}
		
		if(data.dieAction != null) {
			addAvailableAction(new DieCommand(this));
		}
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
	 * @return the meter
	 */
	public ActionMeter getMeter() {
		return meter;
	}
	
	/**
	 * @return the data
	 */
	public EntityData getData() {
		return data;
	}
	
	/**
	 * Calculates the movement cost for this Unit to move to the desired screen position
	 * 
	 * @param screenCameraPos
	 * @return the movement cost of moving, or -1 if invalid
	 */
	public int calculateMovementCost(Vector2f screenCameraPos) {
		Command cmd = this.availableCommands.get("moveto");
		if(cmd instanceof MovementCommand) {
			MovementCommand moveCmd = (MovementCommand)cmd;
			return moveCmd.calculateCost(screenCameraPos);
		}
		return -1;
	}
	
	public int calculateAttackCost(Entity enemy) {
		Command cmd = this.availableCommands.get("attack");
		if(cmd instanceof AttackCommand) {
			AttackCommand attackCmd = (AttackCommand) cmd;
			return attackCmd.calculateCost(enemy);
		}
		return -1;
	}
	
	public boolean canAttack() {
		return type.equals(Type.HUMAN);
	}
	
	public int attackCost() {
		if(data.attackAction!=null) {
			return data.attackAction.cost;
		}
		
		return Integer.MAX_VALUE;
	}
	
	public int attackRange() {
		if(data.attackAction!=null) {
			return data.attackAction.attackRange;
		}
		return 0;
	}
	
	public int movementCost() {
		if(data.moveAction!=null) {
			return data.moveAction.cost;
		}
		return Integer.MAX_VALUE;
	}
	
	public int defenseScore() {
		return data.defense.defensePercentage + calculateAdjacentBonus();
	}
	
	
	public int getRemainingActionPoints() {
		return this.meter.remaining();
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
	
	public void setToDesiredDirection() {
		setCurrentDirection(getDesiredDirection());
	}
	
	/**
	 * @return the desiredDirection
	 */
	public Direction getDesiredDirection() {
		return desiredDirection;
	}
	
	/**
	 * @param desiredDirection the desiredDirection to set
	 */
	public void setDesiredDirection(Direction desiredDirection) {
		this.desiredDirection = desiredDirection;
	}
	
	/**
	 * @param currentState the currentState to set
	 */
	public void setCurrentState(State currentState) {
		this.currentState = currentState;
		this.model.resetAnimation();
	}
	
	public int calculateAdjacentBonus() {
		Vector2f tilePos = getTilePos();
		int tileX = (int) tilePos.x;
		int tileY = (int) tilePos.y;
		
		int bonusSum = 0;
		bonusSum += calculateAdjacentBonus(Direction.NORTH, tileX, tileY);
		bonusSum += calculateAdjacentBonus(Direction.EAST, tileX, tileY);
		bonusSum += calculateAdjacentBonus(Direction.SOUTH, tileX, tileY);
		bonusSum += calculateAdjacentBonus(Direction.WEST, tileX, tileY);
		
		return bonusSum;		
	}
	
	private int calculateAdjacentBonus(Direction dir, int tileX, int tileY) {
		tileX += dir.getX();
		tileY += dir.getY();
		
		IsometricMap map = game.getMap();
		if(!map.checkTileBounds(tileX, tileY)) {
			MapTile tile = map.getTile(0, tileX, tileY);
			Entity ent = game.getEntityOnTile(tile);
			if(ent!=null && ent.isTeammate(this)) {
				return data.defense.groupBonusPercentage;
			}
		}
		return 0;
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
	
	public int getMaxHealth() {
		return 5;
	}
	
	/**
	 * @return the isAlive
	 */
	public boolean isAlive() {
		return !this.currentState.equals(State.DEAD);
	}
	
	public void delete() {
		this.isDeleted = true;
		this.team.removeMember(this);
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
	
	public boolean isTeammate(Entity other) {
		if(other!=null) {
			if(other.team != null && team!=null) {
				return other.team.equals(team);
			}
		}
		return false;
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
		return Optional.ofNullable(this.availableCommands.get(name.toLowerCase()));
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
		return this.availableCommands.get(actionName.toLowerCase()) != null;
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
		this.availableCommands.put(action.getName().toLowerCase(), action);
		return this;
	}
	
	public boolean isDead() {
		return getCurrentState().equals(State.DEAD);
	}
	
	/**
	 * @return the availableActions
	 */
	public List<Command> getAvailableActions() {
		return new ArrayList<>(availableCommands.values());
	}
	
	/**
	 * @return the team
	 */
	public Team getTeam() {
		return team;
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
		//bounds.setLocation(getPos());
		return bounds;
	}
	
	public Rectangle getTileBounds() {
		tileBounds.setLocation(getTilePos());
		return tileBounds;
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
	
	public int distanceFrom(MapTile tile) {
		Vector2f tilePos = getTilePos();
		return (int)Math.sqrt((tile.getXIndex() - tilePos.x) * (tile.getXIndex() - tilePos.x) + 
				              (tile.getYIndex() - tilePos.y) * (tile.getYIndex() - tilePos.y));
	}
	
	public int distanceFrom(Entity other) {
		Vector2f tilePos = getTilePos();
		Vector2f otherTilePos = other.getTilePos();
		
		return (int)Math.sqrt((otherTilePos.x - tilePos.x) * (otherTilePos.x - tilePos.x) + (otherTilePos.y - tilePos.y) * (otherTilePos.y - tilePos.y));
	}
	
	public float getDiameter() {
		return (float)Math.sqrt(bounds.width*bounds.width + bounds.height*bounds.height);
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

	public void endTurn() {
		this.meter.reset(data.movements);
	}
	
	/* (non-Javadoc)
	 * @see newera.gfx.Renderable#update(newera.util.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		this.commandQueue.update(timeStep);
		this.model.update(timeStep);
	}
	
	/* (non-Javadoc)
	 * @see newera.gfx.Renderable#render(newera.gfx.Canvas, newera.gfx.Camera, float)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {	
		model.render(canvas, camera, alpha);
		
		if(getType().equals(Type.HUMAN)) {
			Vector2f pos = getRenderPosition(camera, alpha);
			int health = getHealth();
//			for(int i = 0; i < health; i++) {
//				canvas.drawString("*", pos.x+23 + (i*10), pos.y+68, 0xffffffff);
//			}
			
			drawMeter(canvas, pos.x + 42, pos.y + 68, health, getMaxHealth(), 0x9fFF0000, 0xffafFFaf);
			drawMeter(canvas, pos.x + 42, pos.y + 74, meter.remaining(), data.movements, 0x8f4a5f8f, 0xff3a9aFF);
			
			
			
			//canvas.resizeFont(12f);
			//canvas.drawString("x" + this.meter.getMovementAmount(), pos.x, pos.y+72, 0xffffffff);
			
			//canvas.drawString("WorldPos: " + (int)getPos().x+","+(int)getPos().y,pos.x, pos.y+72, 0xffffffff);
			//canvas.drawString("ScreenPos: " + (int)pos.x+","+(int)pos.y,pos.x, pos.y+92, 0xffffffff);
		}
	}
	
	
	private void drawMeter(Canvas canvas, float px, float py, int metric, int max, int backgroundColor, int foregroundColor) {
		int x = (int) px;
		int y = (int) py;
		
		int width = 30;
		int height = 5;		
		
		//0xff9aFF1a
//		int backgroundColor = isSelected ? 0x9fFF0000 : 0x0fFF0000;
//		int foregroundColor = isSelected ? 0xffafFFaf : 0x7f9aFF1a;
		
		backgroundColor = isSelected ? backgroundColor : Colors.setAlpha(backgroundColor, 0x0f);
		foregroundColor = isSelected ? foregroundColor : Colors.setAlpha(foregroundColor, 0x7f);
		
		canvas.fillRect( x, y, width, height, backgroundColor );
		if (health > 0) {
			canvas.fillRect( x, y, (width * metric/max), height, foregroundColor );
		}
		canvas.drawRect( x-1, y, width+1, height, 0xff000000 );
		
		// add a shadow effect
		canvas.drawLine( x, y+1, x+width, y+1, 0x8f000000 );
		canvas.drawLine( x, y+2, x+width, y+2, 0x5f000000 );
		canvas.drawLine( x, y+3, x+width, y+3, 0x2f000000 );
		canvas.drawLine( x, y+4, x+width, y+4, 0x0f000000 );
		canvas.drawLine( x, y+5, x+width, y+5, 0x0b000000 );
		
		y = y+height;
		canvas.drawLine( x, y-5, x+width, y-5, 0x0b000000 );
		canvas.drawLine( x, y-4, x+width, y-4, 0x0f000000 );
		canvas.drawLine( x, y-3, x+width, y-3, 0x2f000000 );
		canvas.drawLine( x, y-2, x+width, y-2, 0x5f000000 );
		canvas.drawLine( x, y-1, x+width, y-1, 0x8f000000 );				
	}
}


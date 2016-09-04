/*
 * see license.txt 
 */
package franks.game.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import franks.game.ActionMeter;
import franks.game.Game;
import franks.game.Team;
import franks.game.World;
import franks.game.action.AttackCommand;
import franks.game.action.DieCommand;
import franks.game.action.MovementCommand;
import franks.game.commands.Command;
import franks.game.commands.Command.CommandType;
import franks.game.commands.CommandQueue;
import franks.game.commands.CommandQueue.CommandRequest;
import franks.game.net.NetEntity;
import franks.game.net.NetEntityPartial;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.gfx.Colors;
import franks.gfx.Renderable;
import franks.map.IsometricMap;
import franks.map.MapTile;
import franks.math.Rectangle;
import franks.math.Vector2f;
import franks.util.TimeStep;

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
		
	/**
	 * The state the Entity can be in
	 * 
	 * @author Tony
	 *
	 */
	public static enum State {
		IDLE,
		WALKING,
		ATTACKING,
		DEAD,
	}
	
	private final int id;
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
	
	private java.util.Map<CommandType, Command> availableCommands;
	
	protected Game game;
	protected World world;
	
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
				
	public Entity(int id, Game game, Team team, EntityData data) {
		this.id = id;
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
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return true if there are no pending commands or any commands
	 * being executed
	 */
	public boolean isCommandQueueEmpty() {
		return commandQueue.isEmpty();
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
	 * Calculates the defensive score.
	 * 
	 * @return the total defense score
	 */
	public int calculateDefenseScore() {
		return data.defense.defensePercentage + calculateAdjacentBonus();
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
	
	
	/**
	 * Calculates the total cost of attacking the supplied enemy
	 * 
	 * @param enemy
	 * @return the total cost of attacking the supplied entity, or -1 if invalid
	 */
	public int calculateAttackCost(Entity enemy) {
		Command cmd = this.availableCommands.get("attack");
		if(cmd instanceof AttackCommand) {
			AttackCommand attackCmd = (AttackCommand) cmd;
			return attackCmd.calculateCost(enemy);
		}
		return -1;
	}
	
	public boolean canAttack() {
		return data.attackAction != null;
	}
	
	public boolean canMove() {
		return data.moveAction != null;
	}
	
	public int attackBaseCost() {
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
	
	public int movementBaseCost() {
		if(data.moveAction!=null) {
			return data.moveAction.cost;
		}
		return Integer.MAX_VALUE;
	}
	

	
	/**
	 * Action points remaining for this unit
	 * @return Action points remaining for this unit
	 */
	public int remainingActionPoints() {
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
	
	
	/**
	 * Sets the current direction to the desired direction
	 */
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
	
	private int calculateAdjacentBonus() {
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
	
	/**
	 * The current health of this unit
	 * 
	 * @return the health
	 */
	public int getHealth() {
		return health;
	}
	
	
	/**
	 * The maximum health this unit can obtain
	 * 
	 * @return The maximum health this unit can obtain
	 */
	public int getMaxHealth() {
		return 5;
	}
	
	/**
	 * @return the isAlive
	 */
	public boolean isAlive() {
		return !this.currentState.equals(State.DEAD);
	}
	
	
	/**
	 * Delete removes this unit from the world entirely
	 */
	public void delete() {
		this.isDeleted = true;
		this.team.removeMember(this);
	}
	
	/**
	 * Determine if this unit should be removed from the world
	 * 
	 * @return Determine if this unit should be removed from the world
	 */
	public boolean isDeleted() {
		return this.isDeleted;
	}
	
	/**
	 * Place this unit in the DEAD state, which is still
	 * part of the game world
	 */
	public void kill() {
		if(this.isAlive()) {
			doAction(new CommandRequest(game, CommandType.Die, this));
		}
	}
	
	
	/**
	 * Damage this unit.  If the health get to zero, the unit
	 * is placed in the DEAD state.
	 */
	public void damage() {
		this.health--;
		if(this.health<=0) {
			kill();
		}
	}

	/**
	 * If the other {@link Entity} is within a tile's length away
	 * 
	 * @param other
	 * @return true if within a tiles length away
	 */
	public boolean isWithinRange(Entity other) {
		Vector2f delta = getPos().subtract(other.getPos());
		return delta.length() <= MaxNeighborDistance;
	}
	
	/**
	 * If this entity and the supplied entity are team mates.
	 * 
	 * @param other
	 * @return true if they are team mates
	 */
	public boolean isTeammate(Entity other) {
		if(other!=null) {
			if(other.team != null && team!=null) {
				return other.team.equals(team);
			}
		}
		return false;
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
	
	public Optional<Command> getCommand(final CommandType type) {
		return Optional.ofNullable(this.availableCommands.get(type));
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
	
	public boolean hasAction(CommandType type) {
		return this.availableCommands.get(type) != null;
	}
	
	public void queueAction(CommandRequest request) {
		this.commandQueue.add(request);
	}
	
	public void doAction(CommandRequest request) {
		this.commandQueue.cancel();
		queueAction(request);
	}
	
	public boolean canDo(CommandType type) {
		return hasAction(type);
	}
	
	public Entity addAvailableAction(Command action) {
		this.availableCommands.put(action.getType(), action);
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
	 * @return the {@link MapTile} this Entity is on.
	 */
	public MapTile getTileOn() {
		Vector2f tilePos = getTilePos();
		return game.getTile(tilePos);
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
		
		world.getScreenPosByMapTileIndex(tilePos, renderPos);		
		Vector2f.Vector2fSubtract(renderPos, cameraPos, renderPos);
		
		renderPos.x -= 32;
		renderPos.y -= 32;
		
		return renderPos;
	}

	public void endTurn() {
		this.meter.reset(data.movements);
	}
	
	public NetEntity getNetEntity() {
		NetEntity net = new NetEntity();
		net.id = id;
		net.type = type;
		net.name = name;
		net.pos = pos.createClone();
		net.currentDirection = currentDirection;
		net.currentState = currentState;
		net.actionPointsAmount = meter.remaining();
		net.health = health;
		net.dataFile = data.dataFile;
		return net;
	}
	
	public void syncFrom(NetEntityPartial net) {
		type=net.type;
		name=net.name;
		pos.set(net.pos);
		setCurrentDirection(net.currentDirection);
		setCurrentState(net.currentState);
		setDesiredDirection(net.currentDirection);
		meter.reset(net.actionPointsAmount);
		health=net.health;
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


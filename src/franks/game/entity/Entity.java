/*
 * see license.txt 
 */
package franks.game.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import franks.game.ActionMeter;
import franks.game.Army;
import franks.game.Game;
import franks.game.Player;
import franks.game.World;
import franks.game.actions.Action;
import franks.game.actions.Action.ActionType;
import franks.game.actions.AttackAction;
import franks.game.actions.BattleAttackAction;
import franks.game.actions.BuildAction;
import franks.game.actions.Command;
import franks.game.actions.CommandQueue;
import franks.game.actions.DieAction;
import franks.game.actions.MovementAction;
import franks.game.net.NetEntity;
import franks.game.net.NetEntityPartial;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.gfx.Colors;
import franks.gfx.Renderable;
import franks.map.IsometricMap;
import franks.map.MapTile;
import franks.map.MapTile.Visibility;
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
        GENERAL,
        SCOUT,
        WORKER,
        FRANK,
        TREE,
        STONE,
        BERRY_BUSH,
        
        BUILDING,
        ;
        
        public boolean isAttackable() {
            return this == GENERAL ||
                   this == SCOUT ||
                   this == FRANK;
        }
    }
    
        
    /**
     * The gameState the Entity can be in
     * 
     * @author Tony
     *
     */
    public static enum State {
        IDLE,
        WALKING,
        ATTACKING,
        DEAD,
        
        BUILDING,
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
    
    protected java.util.Map<ActionType, Action> availableActions;
    
    protected Game game;
    protected EntityAttribute health;
        
    private boolean isDeleted;
        
    private State currentState;
    private Direction currentDirection;
    private Direction desiredDirection;
     
    
    private EntityModel model;
    
    private ActionMeter meter;
    
    private CommandQueue commandQueue;
    private Army army;
    
    private EntityData data;
                
    public Entity(int id, Game game, Army army, EntityData data) {
        this.id = id;
        this.game = game;
        this.army = army;
        this.data = data;
        
        this.name = data.name;
        this.type = data.type;    
                
        EntityAttribute actionPoints = data.getActionPoints();
        this.meter = new ActionMeter(actionPoints.getMaxValue());
        this.commandQueue = new CommandQueue(game);
        
        this.pos = new Vector2f();
        this.centerPos = new Vector2f();
        this.renderPos = new Vector2f();
        this.scratch = new Vector2f();
        
        this.tilePos = new Vector2f();
        
        int tileWidth  = game.getWorld().getMap().getTileWidth();
        int tileHeight = game.getWorld().getMap().getTileHeight();
        
        int tw = data.width  / tileWidth  + ((data.width  % tileWidth  > 0) ? 1 : 0);
        int th = data.height / tileHeight + ((data.height % tileHeight > 0) ? 1 : 0);
        
            
        this.tileBounds = new Rectangle(tw * tileWidth, th * tileHeight);
        this.tileBounds.setLocation(getTilePos());
        
        this.bounds = new Rectangle(data.width, data.height);
        this.bounds.setLocation(pos);
        
        
        this.availableActions = new HashMap<>();
        this.isDeleted = false;
        
        this.currentState = State.IDLE;
        this.currentDirection = Direction.SOUTH;
        this.desiredDirection = Direction.SOUTH;
        
        this.health = data.getHealth();        
        this.model = new EntityModel(game, this, data.graphics);
        
        
        if(data.attackAction!=null) {            
            addAvailableAction(new BattleAttackAction(game, this, data.attackAction));
        }
        
        if(data.moveAction != null) {
            addAvailableAction(new MovementAction(game, this, data.moveAction));
        }
        
        if(data.dieAction != null) {
            addAvailableAction(new DieAction(this));
        }
        
        if(data.buildAction != null) {
            addAvailableAction(new BuildAction(data.buildAction, this));
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
    
    public int defenseBaseScore() {
        return data.defense != null ? data.defense.defensePercentage : 0;
    }
    
    /**
     * Calculates the defensive score.
     * 
     * @return the total defense score
     */
    public int calculateDefenseScore() {
        return defenseBaseScore() + calculateAdjacentBonus();
    }
    
    /**
     * Calculates the movement cost for this Unit to move to the desired screen position
     * 
     * @param tilePos
     * @return the movement cost of moving, or -1 if invalid
     */
    public int calculateMovementCost(Vector2f tilePos) {
        Action cmd = this.availableActions.get(ActionType.Move);
        if(cmd instanceof MovementAction) {
            MovementAction moveCmd = (MovementAction)cmd;
            return moveCmd.calculateCost(tilePos);
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
        AttackAction attackCmd = getAttackAction();
        if(attackCmd != null) {
            return attackCmd.calculateCost(enemy);
        }
        return -1;
    }
    
    public int calculateAttackCost(MapTile tile) {
        AttackAction attackCmd = getAttackAction();
        if(attackCmd != null) {
            return attackCmd.calculateCost(tile);
        }
        return -1;
    }
    
    public int calculateStrictAttackPercentage() {
        AttackAction attackCmd = getAttackAction();
        if(attackCmd != null) {            
            return attackCmd.calculateStrictAttackPercentage(this);
        }
        return -1;
    }
    
    public int calculateStrictDefensePercentage() {
        AttackAction attackCmd = getAttackAction();
        if(attackCmd != null) {            
            return attackCmd.calculateStrictDefencePercentage(this);
        }
        return -1;
    }
    
    public AttackAction getAttackAction() {
        Action cmd = this.availableActions.get(ActionType.Attack);
        if(cmd instanceof AttackAction) {
            AttackAction attackCmd = (AttackAction) cmd;
            return attackCmd;
        }
        return null;
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
    
    public int visibilityRange() {
        return data.getVisibilityRange().getCurrentValue();
    }
    
    public void visitTiles(IsometricMap map) {
        Vector2f pos = getScreenPosition();
        
        List<MapTile> tiles = map.getAllTilesInCircle( (int)pos.x, (int)pos.y, visibilityRange()*16, new ArrayList<>());
        for(MapTile tile : tiles) {
            tile.setVisibility(Visibility.VISIBLE);
        }
    }
    
    /**
     * Action points remaining for this unit
     * @return Action points remaining for this unit
     */
    public int remainingActionPoints() {
        return this.meter.remaining();
    }
    
    /**
     * @return the number of action points this unit has
     * at the start of a turn
     */
    public int startingActionPoints() {
        return this.data.getActionPoints().getMaxValue();
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
            if(tile!=null) {
                Entity ent = game.getEntityOnTile(tile);
                if(ent!=null && ent.isTeammate(this)) {
                    return data.defense != null ? data.defense.groupBonusPercentage : 0;
                }
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
        return health.getCurrentValue();
    }
    
    
    /**
     * The maximum health this unit can obtain
     * 
     * @return The maximum health this unit can obtain
     */
    public int getMaxHealth() {
        return health.getMaxValue();
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
        this.army.removeMember(this);
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
     * Place this unit in the DEAD gameState, which is still
     * part of the game world
     */
    public void kill() {
        if(this.isAlive()) {
            doAction(new Command(game, ActionType.Die, this));
        }
    }
    
    
    /**
     * Damage this unit.  If the health get to zero, the unit
     * is placed in the DEAD gameState.
     */
    public void damage() {
        this.health.delta(-1);
        if(this.health.getCurrentValue()<=0) {
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
     * If this entity and the supplied entity are army mates.
     * 
     * @param other
     * @return true if they are army mates
     */
    public boolean isTeammate(Entity other) {
        if(other!=null) {
            if(other.army != null && army!=null) {
                return other.army.equals(army);
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
    
    public Optional<Action> getCommand(final ActionType type) {
        return Optional.ofNullable(this.availableActions.get(type));
    }
    
    public Entity moveToRegion(int x, int y) {
        World world = game.getWorld();
        IsometricMap map = world.getMap();
        
        if(map.getTileWidth() > 32) {        
            this.pos.set((x * world.getRegionWidth()) + map.getTileWidth()/2 + bounds.width/2, 
                         (y * world.getRegionHeight()) + map.getTileHeight()/2 - bounds.height/2);
        }
        else {
            this.pos.set((x * world.getRegionWidth()), 
                         (y * world.getRegionHeight()) );
        }
        
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
        World world = game.getWorld();
        int tileX = (int)(pos.x / world.getRegionWidth());
        int tileY = (int)(pos.y / world.getRegionHeight());
        int dirX = tile.getXIndex() - tileX;
        int dirY = tile.getYIndex() - tileY;
        setCurrentDirection(Direction.getDirection(dirX, dirY));
        return this;
    }
    
    public boolean hasAction(ActionType type) {
        return this.availableActions.get(type) != null;
    }
    
    public void queueAction(Command request) {
        this.commandQueue.add(request);
    }
        
    public void doAction(Command request) {
        this.commandQueue.cancel();
        queueAction(request);
    }
    
    public boolean canDo(ActionType type) {
        return hasAction(type);
    }
    
    public Entity addAvailableAction(Action action) {
        this.availableActions.put(action.getType(), action);
        return this;
    }
    
    /**
     * @return true if the cursor is over this entity
     */
    public boolean isHoveredOver() {
        return game.getEntityOverMouse() == this;
    }
    
    public boolean isDead() {
        return getCurrentState().equals(State.DEAD);
    }
    
    /**
     * @return the availableActions
     */
    public List<Action> getAvailableActions() {
        return new ArrayList<>(availableActions.values());
    }
    
    public Player getPlayer() {
        return army.getPlayer();
    }
    
    /**
     * @return the army
     */
    public Army getTeam() {
        return army;
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
        World world = game.getWorld();
        // Test out 0 based cells, might have to use
        // CenterPos for more accurate conversion
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
        IsometricMap map = game.getMap();
        
        // Doesn't like 0 based cells, so I couldn't
        // use Game.getTile(..); investigate
        int tileX = (int)tilePos.x;
        int tileY = (int)tilePos.y;
        if(!map.checkTileBounds(tileX, tileY)) {
            return map.getTile(0, tileX, tileY);
        }
        
        return null;
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
//        MapTile tile = game.getWorld().getMapTileByWorldPos(getCenterPos());
//        if(tile!=null) {
//            return tile.getRenderY();
//        }
//        return -1;
    }
    
    
    public int distanceFrom(MapTile tile) {
        Vector2f tilePos = getTilePos();
        return (int)Math.sqrt((tile.getXIndex() - tilePos.x) * (tile.getXIndex() - tilePos.x) + 
                              (tile.getYIndex() - tilePos.y) * (tile.getYIndex() - tilePos.y));
    }
    
    public boolean inAttackRange(Entity enemy) {
        int numberOfTilesAway = distanceFrom(enemy);
        return (numberOfTilesAway <= attackRange());
    }
    
    public int distanceFrom(Entity other) {
        Vector2f tilePos = getTilePos();
        Vector2f otherTilePos = other.getTilePos();
        
        return (int)Math.sqrt((otherTilePos.x - tilePos.x) * (otherTilePos.x - tilePos.x) + (otherTilePos.y - tilePos.y) * (otherTilePos.y - tilePos.y));
    }
    
    public float getDiameter() {
        return (float)Math.sqrt(bounds.width*bounds.width + bounds.height*bounds.height);
    }
    
    
    /**
     * The screen position of this {@link Entity}.  This is 
     * almost identical to {@link Entity#getRenderPosition(Camera, float)} other
     * than the fact that it isn't offset by the {@link Camera} position.
     * 
     * @return the screen position of this {@link Entity}
     */
    public Vector2f getScreenPosition() {
        World world = game.getWorld();
        Vector2f tilePos = getTilePos();
        
        world.getScreenPosByMapTileIndex(tilePos, renderPos);        
        
        return renderPos;
    }
    
    /**
     * The rendering position on screen relative to the supplied {@link Camera}
     * 
     * @param camera
     * @param alpha
     * @return the screen position this entity should be rendered at
     */
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
        this.meter.reset(startingActionPoints());
    }
    
    
    /**
     * Calculate the battle experience points attained
     */
    public void calculateBattleXP(boolean isVictor) {
        if(isAlive()) {
            endTurn();
            this.data.postBattle(isVictor);            
        }
    }
    
    protected NetEntity getNetEntity(NetEntity net) {
        net.id = id;
        net.type = type;
        net.name = name;
        net.pos = pos.createClone();
        net.currentDirection = currentDirection;
        net.currentState = currentState;
        net.actionPointsAmount = meter.remaining();
        net.health = health.getCurrentValue();
        net.dataFile = data.dataFile;
        return net;
    }
    
    public NetEntity getNetEntity() {        
        return getNetEntity(new NetEntity());
    }
    
    public void syncFrom(NetEntityPartial net) {
        type=net.type;
        name=net.name;
        pos.set(net.pos);
        setCurrentDirection(net.currentDirection);
        setCurrentState(net.currentState);
        setDesiredDirection(net.currentDirection);
        meter.reset(net.actionPointsAmount);
        health.setCurrentValue(net.health);
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
        MapTile tile = getTileOn();
        
        // Don't render the entity if they are not on a Visible tile
        if(tile!=null && tile.getVisibility() != Visibility.VISIBLE) {
            return;            
        }
        
        model.render(canvas, camera, alpha);
        
        commandQueue.render(canvas, camera, alpha);
        
        Vector2f pos = getRenderPosition(camera, alpha);
        int health = getHealth();
        
        drawMeter(canvas, pos.x + 42, pos.y + 68, health, getMaxHealth(), 0x9fFF0000, 0xffafFFaf);
        drawMeter(canvas, pos.x + 42, pos.y + 74, meter.remaining(), startingActionPoints(), 0x8f4a5f8f, 0xff3a9aFF);
                
        //canvas.resizeFont(12f);
        //canvas.drawString("x" + this.meter.getMovementAmount(), pos.x, pos.y+72, 0xffffffff);
        
        //canvas.drawString("WorldPos: " + (int)getPos().x+","+(int)getPos().y,pos.x, pos.y+72, 0xffffffff);
        //canvas.drawString("ScreenPos: " + (int)pos.x+","+(int)pos.y,pos.x, pos.y+92, 0xffffffff);
        
    }
    
    
    private void drawMeter(Canvas canvas, float px, float py, int metric, int max, int backgroundColor, int foregroundColor) {
        int x = (int) px;
        int y = (int) py;
        
        int width = 30;
        int height = 5;        
        
        //0xff9aFF1a
//        int backgroundColor = isSelected ? 0x9fFF0000 : 0x0fFF0000;
//        int foregroundColor = isSelected ? 0xffafFFaf : 0x7f9aFF1a;
        
        backgroundColor = isSelected ? backgroundColor : Colors.setAlpha(backgroundColor, 0x0f);
        foregroundColor = isSelected ? foregroundColor : Colors.setAlpha(foregroundColor, 0x7f);
        
        max = Math.max(max, 1);
        
        canvas.fillRect( x, y, width, height, backgroundColor );
        if (getHealth() > 0) {
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


/*
 * see license.txt 
 */
package newera.game.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import leola.vm.types.LeoMap;
import leola.vm.types.LeoObject;
import newera.game.Command;
import newera.game.Game;
import newera.game.World;
import newera.gfx.Camera;
import newera.gfx.Canvas;
import newera.gfx.Renderable;
import newera.math.Rectangle;
import newera.math.Vector2f;
import newera.util.TimeStep;

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
	}
	
	private Type type;
	protected Vector2f pos;
	private Vector2f centerPos;
	protected Rectangle bounds;
	private boolean isSelected;
	
	private List<Command> availableCommands;
	
	protected Game game;
	private LeoMap attributes;
	
	private boolean isAlive;
	
	/**
	 * 
	 */
	public Entity(Game game, Type type, Vector2f pos, int width, int height) {
		this.game = game;
		this.type = type;
		this.pos = pos;
		this.centerPos = new Vector2f();
		
		this.bounds = new Rectangle(width, height);
		this.bounds.setLocation(pos);
		
		this.availableCommands = new ArrayList<>();
		this.attributes = new LeoMap();
		this.isAlive = true;
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
	 * @return the isAlive
	 */
	public boolean isAlive() {
		return isAlive;
	}
	
	public void kill() {
		this.isAlive = false;
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
		this.pos.set(x * World.RegionWidth, y * World.RegionHeight);
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
	
	public Entity addAvailableAction(Command action) {
		this.availableCommands.add(action);
		return this;
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
	

	/* (non-Javadoc)
	 * @see newera.gfx.Renderable#update(newera.util.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {		
	}
	
	/* (non-Javadoc)
	 * @see newera.gfx.Renderable#render(newera.gfx.Canvas, newera.gfx.Camera, float)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {
		Vector2f cameraPos = camera.getRenderPosition(alpha);
		float dx = pos.x - cameraPos.x;
		float dy = pos.y - cameraPos.y;
		
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
	}
}

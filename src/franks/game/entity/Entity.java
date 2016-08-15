/*
 * see license.txt 
 */
package franks.game.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import franks.game.Command;
import franks.game.Game;
import franks.game.World;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.gfx.Renderable;
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
	
	private Type type;
	protected Vector2f pos;
	protected Vector2f renderPos;
	private Vector2f centerPos;
	protected Rectangle bounds;
	private boolean isSelected;
	
	private List<Command> availableCommands;
	
	protected Game game;
	protected World world;
	
	private LeoMap attributes;
	
	private boolean isAlive;
	private TextureRegion image;
	
	/**
	 * 
	 */
	public Entity(Game game, Type type, TextureRegion image, Vector2f pos, int width, int height) {
		this.game = game;
		this.type = type;
		this.image = image;
		this.pos = pos;
		this.centerPos = new Vector2f();
		this.renderPos = new Vector2f();
		
		this.bounds = new Rectangle(width, height);
		this.bounds.setLocation(pos);
		
		this.availableCommands = new ArrayList<>();
		this.attributes = new LeoMap();
		this.isAlive = true;
		
		this.world = game.getWorld();
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
	
	public float getDiameter() {
		return (float)Math.sqrt(bounds.width*bounds.width + bounds.height*bounds.height);
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
			dx = tile.getRenderX() + 16;//-cameraPos.x;
			dy = tile.getRenderY() - 8;//-cameraPos.y;
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
		canvas.drawImage(this.image, dx, dy, null);
	}
	
}

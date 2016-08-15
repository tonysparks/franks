/*
 * see license.txt 
 */
package franks.map;

import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.math.Rectangle;
import franks.util.TimeStep;

/**
 * @author Tony
 *
 */
public class AbstractTile implements MapTile {

	/**
	 * Flip masks
	 */
	private static final int isFlippedHorizontal = (1 << 0), isFlippedVert = (1 << 1), isFlippedDiagnally = (1 << 2);

	protected int x, y;
	protected int width, height;
	protected int xIndex, yIndex;
	protected int layer;

	protected int renderX, renderY;
	protected int mask;
	protected int heightMask;
	protected int flipMask;
	protected CollisionMask collisionMask;
	protected Rectangle bounds;

	protected SurfaceType surfaceType;

	protected boolean isDestroyed;

	/**
	 * 
	 */
	public AbstractTile(int layer, int width, int height) {
		this.layer = layer;
		this.width = width;
		this.height = height;
		this.bounds = new Rectangle();
		this.collisionMask = CollisionMask.NO_COLLISION;
		this.surfaceType = SurfaceType.CEMENT;
		this.isDestroyed = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see newera.map.MapTile#getSurfaceType()
	 */
	@Override
	public SurfaceType getSurfaceType() {
		return surfaceType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see newera.map.MapTile#setSurfaceType(newera.map.Tile.SurfaceType)
	 */
	@Override
	public void setSurfaceType(SurfaceType surfaceType) {
		this.surfaceType = surfaceType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see newera.map.MapTile#getLayer()
	 */
	@Override
	public int getLayer() {
		return layer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see newera.map.MapTile#getHeight()
	 */
	@Override
	public int getHeight() {
		return height;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see newera.map.MapTile#getWidth()
	 */
	@Override
	public int getWidth() {
		return width;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see newera.map.MapTile#getX()
	 */
	@Override
	public int getX() {
		return this.x;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see newera.map.MapTile#getY()
	 */
	@Override
	public int getY() {
		return this.y;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see newera.map.MapTile#getXIndex()
	 */
	@Override
	public int getXIndex() {
		return xIndex;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see newera.map.MapTile#getYIndex()
	 */
	@Override
	public int getYIndex() {
		return yIndex;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see newera.map.MapTile#setCollisionMask(newera.map.Tile.CollisionMask)
	 */
	@Override
	public void setCollisionMask(CollisionMask collisionMask) {
		this.collisionMask = collisionMask;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see newera.map.MapTile#getCollisionMask()
	 */
	@Override
	public CollisionMask getCollisionMask() {
		return collisionMask;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see newera.map.MapTile#setCollisionMaskById(int)
	 */
	@Override
	public void setCollisionMaskById(int id) {
		this.collisionMask = CollisionMask.fromId(id);
		if (this.collisionMask == null) {
			this.collisionMask = CollisionMask.ALL_SOLID;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see newera.map.MapTile#setMask(int)
	 */
	@Override
	public void setMask(int mask) {
		this.mask = mask;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see newera.map.MapTile#getMask()
	 */
	@Override
	public int getMask() {
		return mask;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see newera.map.MapTile#setHeightMask(int)
	 */
	@Override
	public void setHeightMask(int heightMask) {
		this.heightMask = heightMask;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see newera.map.MapTile#getHeightMask()
	 */
	@Override
	public int getHeightMask() {
		return heightMask;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see newera.map.MapTile#isDestroyed()
	 */
	@Override
	public boolean isDestroyed() {
		return isDestroyed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see newera.map.MapTile#setDestroyed(boolean)
	 */
	@Override
	public void setDestroyed(boolean isDestroyed) {
		this.isDestroyed = isDestroyed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see newera.map.MapTile#isFlippedHorizontal()
	 */
	@Override
	public boolean isFlippedHorizontal() {
		return (this.flipMask & isFlippedHorizontal) != 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see newera.map.MapTile#isFlippedVertical()
	 */
	@Override
	public boolean isFlippedVertical() {
		return (this.flipMask & isFlippedVert) != 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see newera.map.MapTile#isFlippedDiagnally()
	 */
	@Override
	public boolean isFlippedDiagnally() {
		return (this.flipMask & isFlippedDiagnally) != 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see newera.map.MapTile#setFlips(boolean, boolean, boolean)
	 */
	@Override
	public void setFlips(boolean isFlippedHorizontal, boolean isFlippedVert, boolean isFlippedDiagnally) {
		if (isFlippedDiagnally)
			this.flipMask |= MapTile.isFlippedDiagnally;
		if (isFlippedHorizontal)
			this.flipMask |= MapTile.isFlippedHorizontal;
		if (isFlippedVert)
			this.flipMask |= MapTile.isFlippedVert;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see newera.map.MapTile#setIndexPosition(int, int)
	 */
	@Override
	public void setIndexPosition(int x, int y) {
		this.xIndex = x;
		this.yIndex = y;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see newera.map.MapTile#setPosition(int, int)
	 */
	@Override
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;

		this.xIndex = this.x / this.width;
		this.yIndex = this.y / this.height;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see newera.map.MapTile#setRenderingPosition(int, int)
	 */
	@Override
	public void setRenderingPosition(int x, int y) {
		this.renderX = x;
		this.renderY = y;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see newera.map.MapTile#getRenderX()
	 */
	@Override
	public int getRenderX() {
		return renderX;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see newera.map.MapTile#getRenderY()
	 */
	@Override
	public int getRenderY() {
		return renderY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see newera.map.MapTile#pointCollide(int, int)
	 */
	@Override
	public boolean pointCollide(int x, int y) {
		bounds.set(this.x, this.y, width, height);
		return this.collisionMask.pointCollide(bounds, x, y);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see newera.map.MapTile#rectCollide(newera.math.Rectangle)
	 */
	@Override
	public boolean rectCollide(Rectangle rect) {
		bounds.set(this.x, this.y, width, height);
		return this.collisionMask.rectCollide(bounds, rect);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see newera.map.MapTile#getBounds()
	 */
	@Override
	public Rectangle getBounds() {
		bounds.set(this.x, this.y, width, height);
		return bounds;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see newera.map.MapTile#update(newera.util.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see newera.map.MapTile#render(newera.gfx.Canvas, newera.gfx.Camera,
	 * float)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {
	}
}

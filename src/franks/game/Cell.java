/*
 * see license.txt 
 */
package franks.game;

import java.util.ArrayList;
import java.util.List;

import franks.game.entity.Entity;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.gfx.Renderable;
import franks.map.Map;
import franks.math.Rectangle;
import franks.math.Vector2f;
import franks.util.TimeStep;

/**
 * @author Tony
 *
 */
public class Cell implements Renderable {

	private Rectangle bounds;
	private Rectangle tileBounds;
	private Vector2f centerPos, renderPos;
	private Game game;
	
	private List<Entity> entities;
	
	/**
	 * 
	 */
	public Cell(Game game, Map map, int tileX, int tileY, int widthInTiles, int heightInTiles) {
		this.game = game;
		
		this.tileBounds = new Rectangle(tileX, tileY, widthInTiles, heightInTiles);
		this.bounds = new Rectangle(map.getTileWidth() * tileX, 
				                    map.getTileHeight() * tileY, 
									widthInTiles * map.getTileWidth(), 
									heightInTiles * map.getTileHeight());
	
		this.renderPos = new Vector2f();
		this.centerPos = new Vector2f();
		this.centerPos.set(bounds.x + bounds.width/2f, bounds.y + bounds.height/2f);
		
		this.entities = new ArrayList<>();
		
	}
	
	/**
	 * @return the tileBounds
	 */
	public Rectangle getTileBounds() {
		return tileBounds;
	}
	
	/**
	 * @return the bounds
	 */
	public Rectangle getBounds() {
		return bounds;
	}
	
	/**
	 * @return the centerPos
	 */
	public Vector2f getCenterPos() {
		return centerPos;
	}

	public List<Entity> getEntities() {
		this.entities.clear();
		
		List<Entity> entities = game.getEntities();
		for(Entity ent : entities) {
			if(ent.inCell(this)) {
				this.entities.add(ent);
			}
		}
		
		return this.entities;
	}
	
	
	@Override
	public void update(TimeStep timeStep) {
	}
	
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {
		Vector2f c = camera.getRenderPosition(alpha);
		
		game.getMap().isoIndexToScreen(tileBounds.x, tileBounds.y, renderPos);
		Vector2f.Vector2fSubtract(renderPos, c, renderPos);
		this.game.getMap().renderIsoRect(canvas, renderPos.x-92, renderPos.y+24, bounds.width-16, bounds.height-16, 0xffffffff);
		//this.game.getMap().renderIsoRect(canvas, renderPos.x-16, renderPos.y-24, bounds.width, bounds.height+32, 0xffffffff);
	}
}

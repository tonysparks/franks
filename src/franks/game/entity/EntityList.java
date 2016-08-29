/*
 * see license.txt 
 */
package franks.game.entity;

import java.util.Arrays;
import java.util.Comparator;

import franks.game.Game;
import franks.game.Team;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.gfx.Renderable;
import franks.map.MapTile;
import franks.math.Rectangle;
import franks.util.TimeStep;

/**
 * @author Tony
 *
 */
public class EntityList implements Renderable {


	
	private Comparator<Entity> renderOrder = new Comparator<Entity>() {
		
		@Override
		public int compare(Entity left, Entity right) {
			if(left==null) {
				if(right==null) {
					return 0;
				}
			
				return 1;
			}
			else if(right==null) {
				return -1;
			}
			
			return left.getZOrder() - right.getZOrder();
		}
	};
	
	
	private Entity[] entities;
	private Game game;
	/**
	 * 
	 */
	public EntityList(Game game) {
		this.game = game;
		this.entities = new Entity[Game.MAX_ENTITIES];
	}

	private int getNextId() {
		for(int i = 0; i < entities.length; i++) {
			if(entities[i] == null) {
				return i;
			}
		}
		
		throw new IllegalArgumentException("Hit max entity count");
	}
	
	public Entity buildEntity(Team team, EntityData data) {
		return buildEntity(getNextId(), team, data);
	}
	
	public Entity buildEntity(int id, Team team, EntityData data) {
		Entity ent = new Entity(id, game, team, data);
		this.entities[ent.getId()] = ent;
		return ent;
	}
	
	public Entity getEntity(int id) {
		if(id>=0 && id < entities.length) {
			return this.entities[id];
		}
		return null;
	}
	
	public Entity getEntityOnTile(MapTile tile) {
		return getEntityByBounds(tile.getBounds());
	}
	
	public Entity getEntityByBounds(Rectangle bounds) {
		
		for(int i = 0; i < entities.length; i++) {
			Entity ent = entities[i]; 
			if(ent != null) {
				if(bounds.intersects(ent.getBounds()) ||
				   bounds.contains(ent.getCenterPos())) {
					return ent;
				}
			}
		}
		
		return null;
	}
	
	public void endTurn() {
		for(int i = 0; i < entities.length; i++) {
			Entity ent = entities[i]; 
			if(ent != null) {
				ent.endTurn();
			}
		}
	}
	
	public void clear() {
		for(int i = 0; i < entities.length; i++) {
			entities[i] = null;
		}
	}
	
	@Override
	public void update(TimeStep timeStep) {
		
		for(int i = 0; i < entities.length; i++) {
			Entity ent = entities[i]; 
			if(ent != null) {
				ent.update(timeStep);
				if(ent.isDeleted()) {
					entities[i] = null;
				}
			}
		}	
	}
	
	
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {

		Arrays.sort(entities, renderOrder);
		for(int i = 0; i < entities.length; i++) {
			Entity ent = entities[i]; 
			if(ent != null) {
				ent.render(canvas, camera, alpha);
			}
		}				
	}
}

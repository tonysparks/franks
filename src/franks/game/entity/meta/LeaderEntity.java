/*
 * see license.txt 
 */
package franks.game.entity.meta;

import franks.game.Game;
import franks.game.Randomizer;
import franks.game.Army;
import franks.game.World;
import franks.game.commands.LeaderAttackCommand;
import franks.game.commands.LeaderMovementCommand;
import franks.game.entity.Direction;
import franks.game.entity.Entity;
import franks.game.entity.EntityData;
import franks.game.entity.EntityList;
import franks.map.IsometricMap;
import franks.math.Rectangle;
import franks.math.Vector2f;

/**
 * @author Tony
 *
 */
public class LeaderEntity extends Entity {

	private EntityList entities;
	//private ResourceContainer resources;
	
	//private int maxSquadSize;
	
	/**
	 * @param id
	 * @param game
	 * @param army
	 * @param data
	 */
	public LeaderEntity(int id, Game game, Army army, EntityData data) {
		super(id, game, army, data);
		
		this.entities = new EntityList(game.getEntitityIds());
		//this.resources = new ResourceContainer();
		
		//this.availableCommands.clear();
		addAvailableAction(new LeaderAttackCommand(game, this, data.attackAction.cost, data.attackAction.attackRange));
		addAvailableAction(new LeaderMovementCommand(game, this, data.moveAction.movementSpeed));
		
	}
	
	
	public void addEntity(Entity entity) {
		entities.addEntity(entity);
	}
	
	public void removeDead() {
		this.entities.removeDead();
//		List<Entity> aliveEntities = new ArrayList<>();
//		for(Entity ent : this.entities) {
//			if(ent.isAlive()) {
//				aliveEntities.add(ent);
//			}
//		}
//		
//		this.entities.clear();
//		this.entities.addAll(aliveEntities);
	}
	
	/**
	 * @return the entities
	 */
	public EntityList getEntities() {
		return entities;
	}
	
	/**
	 * @return the size of this leaders squad
	 */
	public int getSquadSize() {
		return entities.size();
	}
	
	
	public void enterBattle(World world, boolean topPosition) {
		IsometricMap map = world.getMap();
		int maxX = map.getTileWorldWidth();
		int maxY = map.getTileWorldHeight();
		
		int x = 0;
		int y = 0;
		
		int xInc = 1;
		
		if(!topPosition) {
			x = maxX - 1;
			y = 0;
			xInc = -1;
		}
		
		for(Entity ent : this.entities) {
			if(y >= maxY) {
				x += xInc;
				y = 0;
			}
			
			ent.moveToRegion(x, y);
			ent.setDesiredDirection(topPosition ? Direction.SOUTH_EAST : Direction.NORTH_WEST);
			ent.setToDesiredDirection();
			
			y++;
		}
		
		shufflePossePositions(game.getRandomizer());
	}
	
	public void leaveBattle(boolean isVictor) {		
		if(getEntities().size()<=0) {
			kill();
		}
		else {
			calculateBattleXP(isVictor);
			getEntities().forEach(ent -> ent.calculateBattleXP(isVictor));			
		}
	}

	private void shufflePossePositions(Randomizer rand) {
		int minX = 0;
		int maxX = 0;
		int minY = 0;
		int maxY = 0;
		
		for(Entity ent : this.entities) {
			Rectangle bounds = ent.getTileBounds();
			if(bounds.x < minX) {
				minX = bounds.x;
			}
			if(bounds.x > maxX) {
				maxX = bounds.x;
			}
			if(bounds.y < minY) {
				minY = bounds.y;
			}
			if(bounds.y > maxY) {
				maxY = bounds.y;
			}
		}
		
		int size = this.entities.size();
		for(int i = 0; i < size; i++) {
			int left = rand.nextInt(size);
			int right = rand.nextInt(size);
			if(left!=right) {
				Entity leftEnt = this.entities.get(left);
				Entity rightEnt = this.entities.get(right);
				Vector2f t = leftEnt.getPos().createClone();
				leftEnt.moveTo(rightEnt.getPos());
				rightEnt.moveTo(t);
			}
		}
	}
}

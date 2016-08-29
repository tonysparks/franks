/*
 * see license.txt 
 */
package franks.game.entity;

import java.util.ArrayList;
import java.util.List;

import franks.game.Game;
import franks.game.Team;

/**
 * A group of {@link Entity}'s
 * 
 * @author Tony
 *
 */
public class EntityGroupData {

	/**
	 * The entity instance specifies the type of entity to create and
	 * position
	 * 
	 * @author Tony
	 *
	 */
	public static class EntityInstanceData {
		public String dataFile;
		public int x;
		public int y;
		
		public Direction direction=Direction.SOUTH_EAST;
	}
	
	public List<EntityInstanceData> entities;	
	
	
	/**
	 * Build the entities from this {@link EntityGroupData}
	 * 
	 * @param team
	 * @param game
	 * @return the list of {@link Entity}s
	 */
	public List<Entity> buildEntities(Team team, Game game) {
		List<Entity> result = new ArrayList<>();
		if(entities!=null) {
			for(EntityInstanceData ref : entities) {
				result.add(game.buildEntity(team, ref));
			}
		}
		
		return result;
	}
}

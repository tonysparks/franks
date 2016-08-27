/*
 * see license.txt 
 */
package franks.game.action;

import java.util.List;

import franks.game.Cell;
import franks.game.Game;
import franks.game.entity.Entity;
import franks.math.Vector2f;

/**
 * @author Tony
 *
 */
public class BatchedAttackCommand  {

	private List<Entity> entities;
	private Vector2f targetPos;
	
	/**
	 * @param entities
	 */
	public BatchedAttackCommand(List<Entity> entities, Vector2f targetPos) {
		this.entities = entities;		
		this.targetPos = targetPos;	
	}

	public void execute(Game game) {
		Cell cell = game.getWorld().getCell(targetPos);
		if(cell != null) {
			
		}
	}

	
	

}

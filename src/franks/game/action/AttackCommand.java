/*
 * see license.txt 
 */
package franks.game.action;

import franks.game.Command;
import franks.game.CommandAction;
import franks.game.CommandQueue.CommandRequest;
import franks.game.Game;
import franks.game.PreconditionResponse;
import franks.game.Randomizer;
import franks.game.entity.Entity;
import franks.game.entity.Entity.State;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.map.MapTile;
import franks.util.TimeStep;
import franks.util.Timer;

/**
 * @author Tony
 *
 */
public class AttackCommand extends Command {


	private Entity attacker;
	private int attackPoints;
	private int attackDistance;
	/**
	 * @param name
	 * @param movementCost
	 */
	public AttackCommand(Entity attacker, int movementPoints, int attackDistance, int attackPoints) {
		super("attack",  movementPoints);
		
		this.attacker = attacker;
		
		this.attackDistance = attackDistance;
		this.attackPoints = attackPoints;
		
	}

	/* (non-Javadoc)
	 * @see franks.game.Command#checkPreconditions(franks.game.Game, franks.game.CommandQueue.CommandRequest)
	 */
	@Override
	public PreconditionResponse checkPreconditions(Game game, CommandRequest request) {
		PreconditionResponse response = new PreconditionResponse();
		
		if(!attacker.canDo(getName())) {
			response.addFailure("This entity can not attack");
		}
		
		Entity enemy = game.getEntityOverMouse();
		if(enemy == null || enemy.isDead()) {
			response.addFailure("No enemy target");
		}
		else {
			int numberOfTilesAway = attacker.distanceFrom(enemy);
			if(numberOfTilesAway > attackDistance) {
				response.addFailure("Enemy target is too far away");
			}			
		}
		
		checkMovement(response, game);
		return response;
	}

	/* (non-Javadoc)
	 * @see franks.game.Command#doAction(franks.game.Game, franks.game.CommandQueue.CommandRequest)
	 */
	@Override
	public CommandAction doAction(Game game, CommandRequest request) {
		MapTile tile = game.getTileOverMouse();		
		Entity enemy = game.getEntityOverMouse();
		return new CommandAction() {
			
			Timer timer = new Timer(false, 14*120);
						
			@Override
			public CommandAction start() {
				timer.start();
				

				if(tile != null) {
					attacker.lookAt(tile);//tile.getX(), tile.getY());
				}
				
				attacker.setCurrentState(State.ATTACKING);
				return this;
			}
			
			@Override
			public CommandAction end() {

				Randomizer rand = game.getRandomizer();
				int x = rand.nextInt(100);
				System.out.println("x=" + x);
				if( x <= attackPoints) {
					enemy.damage();
				}
				
				attacker.setCurrentState(State.IDLE);
				return this;
			}
			
			@Override
			public void update(TimeStep timeStep) {		
				timer.update(timeStep);
				//int damage = attacker.attributeAsInt("attackDamage");
			}
			
			@Override
			public void render(Canvas canvas, Camera camera, float alpha) {
			}
			
			@Override
			public CompletionState getCurrentState() {
				return timer.isTime() ? CompletionState.Success : CompletionState.InProgress;
			}
			
			@Override
			public void cancel() {
				timer.setEndTime(0);
			}
		};
	}

	
}

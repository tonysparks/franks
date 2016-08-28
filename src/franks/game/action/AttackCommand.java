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

	private int hitPercentage;
	private int attackDistance;
	
	private Game game;
	
	/**
	 * @param name
	 * @param movementCost
	 */
	public AttackCommand(Game game, Entity attacker, int cost, int attackDistance, int hitPercentage) {
		super("attack",  cost, attacker);
		
		this.game = game;
		
		this.attackDistance = attackDistance;
		this.hitPercentage = hitPercentage;
		
	}


	/* (non-Javadoc)
	 * @see franks.game.Command#checkPreconditions(franks.game.Game, franks.game.CommandQueue.CommandRequest)
	 */
	@Override
	public PreconditionResponse checkPreconditions(Game game, CommandRequest request) {
		PreconditionResponse response = new PreconditionResponse();
		
		Entity attacker = getEntity();
		if(!attacker.canDo(getName())) {
			response.addFailure("This entity can not attack");
		}
		
		Entity enemy = game.getEntityOverMouse();
		if(enemy == null || enemy.isDead()) {
			response.addFailure("No enemy target");
		}
		else {
			
			if(enemy.isTeammate(attacker)) {
				response.addFailure("Can't attack team member");
			}
			
			int numberOfTilesAway = attacker.distanceFrom(enemy);
			if(numberOfTilesAway > attackDistance) {
				response.addFailure("Enemy target is too far away");
			}			
		}
		
		checkCost(response, game);
		return response;
	}

	@Override
	protected CommandAction doActionImpl(Game game, CommandRequest request) {
		MapTile tile = game.getTileOverMouse();
		Entity attacker = getEntity();
		Entity enemy = game.getEntityOverMouse();
		return new CommandAction() {
			
			Timer timer = new Timer(false, attacker.getData().getAnimationTime(State.ATTACKING));
						
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

				if( x <= hitPercentage) {
					enemy.damage();
				}
				
				int attackPercentage = calculateAttackPercentage(attacker);
				int defensePercentage = calculateDefencePercentage(enemy);
				
				if(attackPercentage >= defensePercentage) {
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


	
	public int calculateCost(Entity enemy) {
		int numberOfTilesAway = getEntity().distanceFrom(enemy);
		if(numberOfTilesAway <= attackDistance) {
			return getActionCost();
		}
		return -1;
	}

	public int calculateAttackPercentage(Entity attacker) {
		Randomizer rand = game.getRandomizer();
		int d10 = rand.nextInt(10) * 10;						
		return d10 + hitPercentage;
	}
	
	public int calculateDefencePercentage(Entity defender) {
		Randomizer rand = game.getRandomizer();
		int d10 = rand.nextInt(10) * 10;						
		return d10 + defender.defenseScore();
	}
}

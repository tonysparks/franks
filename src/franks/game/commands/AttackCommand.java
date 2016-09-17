/*
 * see license.txt 
 */
package franks.game.commands;

import java.util.Optional;

import franks.game.Game;
import franks.game.PreconditionResponse;
import franks.game.Randomizer;
import franks.game.TerrainData.TerrainTileData;
import franks.game.commands.CommandQueue.CommandRequest;
import franks.game.entity.Entity;
import franks.game.entity.Entity.State;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.map.MapTile;
import franks.sfx.Sounds;
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
		super(CommandType.Attack,  cost, attacker);
		
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
		if(!attacker.canDo(getType())) {
			response.addFailure("This entity can not attack");
		}
		
		Optional<Entity> target = request.targetEntity; 
				//game.getEntityOverPos(request.);
		if(!target.isPresent() || target.get().isDead()) {
			response.addFailure("No enemy target");
		}
		else {
			Entity enemy = target.get();
			
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
//		MapTile tile = game.getTileOverPos(request.cursorTilePos);
		MapTile tile = game.getTile(request.cursorTilePos);
		Entity attacker = getEntity();
		Entity enemy = request.targetEntity.get();
		return new CommandAction(request) {
			
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
				int attackPercentage = calculateAttackPercentage(attacker);
				int defensePercentage = calculateDefencePercentage(enemy);
				
				if(attackPercentage >= defensePercentage) {
					enemy.damage();
					Sounds.playGlobalSound(Sounds.meleeHit);
				}
				
				
				attacker.setCurrentState(State.IDLE);
				return super.end();
			}
			
			@Override
			public void update(TimeStep timeStep) {		
				timer.update(timeStep);
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

	public int calculateCost(MapTile tile) {
		int numberOfTilesAway = getEntity().distanceFrom(tile);
		if(numberOfTilesAway <= attackDistance) {
			return getActionCost();
		}
		return -1;
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
		
		return d10 + calculateStrictAttackPercentage(attacker);
	}
	
	public int calculateStrictAttackPercentage(Entity attacker) {		
		int attackBonus = 0;
		MapTile tile = attacker.getTileOn();
		if(tile!=null) {
			TerrainTileData terrain = tile.geTerrainTileData();
			if(terrain!=null) {
				attackBonus = terrain.attackBonus;
			}
		}
		
		return hitPercentage + attackBonus;
	}
	
	public int calculateDefencePercentage(Entity defender) {
		Randomizer rand = game.getRandomizer();
		int d10 = rand.nextInt(10) * 10;
		
		return d10 + calculateStrictDefencePercentage(defender);
	}
	
	public int calculateStrictDefencePercentage(Entity defender) {
		int defenseBonus = 0;
		MapTile tile = defender.getTileOn();
		if(tile!=null) {
			TerrainTileData terrain = tile.geTerrainTileData();
			if(terrain!=null) {
				defenseBonus= terrain.defenseBonus;
			}
		}
		
		return defender.calculateDefenseScore() + defenseBonus;
	}
}

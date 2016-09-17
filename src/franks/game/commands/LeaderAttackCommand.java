/*
 * see license.txt 
 */
package franks.game.commands;

import java.util.Optional;

import franks.game.Game;
import franks.game.PreconditionResponse;
import franks.game.battle.Battle;
import franks.game.battle.BattleGame;
import franks.game.commands.CommandQueue.CommandRequest;
import franks.game.entity.Entity;
import franks.game.entity.meta.LeaderEntity;
import franks.game.meta.MetaGame;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.map.MapTile;
import franks.screens.BattleScreen;
import franks.util.TimeStep;

/**
 * @author Tony
 *
 */
public class LeaderAttackCommand extends Command {
	private int attackDistance;
	private LeaderEntity leaderAttacker;
	
	/**
	 * @param name
	 * @param movementCost
	 */
	public LeaderAttackCommand(Game game, LeaderEntity attacker, int cost, int attackDistance) {
		super(CommandType.Attack,  cost, attacker);
		
		this.leaderAttacker = attacker;
		this.attackDistance = attackDistance;		
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
		LeaderEntity enemy = (LeaderEntity)request.targetEntity.get();
		return new CommandAction(request) {
					
			@Override
			public CommandAction start() {
				MetaGame meta = (MetaGame) game;
				BattleGame battleGame = meta.getBattleGame();
				battleGame.enterBattle(new Battle(leaderAttacker, enemy));
				
				game.getApp().pushScreen(new BattleScreen(game.getApp(), game.getState(), battleGame));
				
				return this;
			}
			
			@Override
			public CommandAction end() {
				return super.end();
			}
			
			@Override
			public void update(TimeStep timeStep) {						
			}
			
			@Override
			public void render(Canvas canvas, Camera camera, float alpha) {
			}
			
			@Override
			public CompletionState getCurrentState() {
				return CompletionState.Success;
			}
			
			@Override
			public void cancel() {				
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
}

/*
 * see license.txt 
 */
package franks.game.commands;

import franks.game.Game;
import franks.game.battle.Battle;
import franks.game.battle.BattleGame;
import franks.game.commands.CommandQueue.CommandRequest;
import franks.game.entity.meta.LeaderEntity;
import franks.game.meta.MetaGame;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.screens.BattleScreen;
import franks.util.TimeStep;

/**
 * @author Tony
 *
 */
public class LeaderAttackCommand extends AttackCommand {	
	private LeaderEntity leaderAttacker;
	
	/**
	 * @param name
	 * @param movementCost
	 */
	public LeaderAttackCommand(Game game, LeaderEntity attacker, int cost, int attackDistance) {
		super(game, attacker, cost, attackDistance, 0);
		
		this.leaderAttacker = attacker;
		this.attackDistance = attackDistance;		
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
}

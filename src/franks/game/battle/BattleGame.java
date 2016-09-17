/*
 * see license.txt 
 */
package franks.game.battle;

import franks.FranksGame;
import franks.game.Game;
import franks.game.GameState;
import franks.game.Hud;
import franks.game.Player;
import franks.game.Turn;
import franks.game.World;
import franks.game.ai.BattleAISystem;
import franks.game.entity.meta.LeaderEntity;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.util.Command;
import franks.util.Console;
import franks.util.TimeStep;

/**
 * Battle Mini-Game
 * 
 * @author Tony
 *
 */
public class BattleGame extends Game {  

	private Hud hud;			
	private BattleAISystem ai;
	
	private LeaderEntity attacker;
	private LeaderEntity defender;
	
	/**
	 * 
	 */
	public BattleGame(FranksGame app, GameState state, Camera camera) {
		super(app, state, camera);
		
		this.hud = new Hud(this);	
	}
	
	public void enterBattle(Battle battle) {
		this.attacker = battle.getAttacker();
		this.defender = battle.getDefender();
		
		
		Player playersTurn = battle.getAttacker().getPlayer();
		this.currentTurn = new Turn(this, playersTurn, 0);
		this.ai = new BattleAISystem(this, getState().getAIPlayer());
		
		// temp
		getApp().getConsole().addCommand(new Command("reload") {
			
			@Override
			public void execute(Console console, String... args) {
				world = createWorld(getState());
				prepareEntities();
			}
		});		
		
		prepareEntities();
	}
	
	private void prepareEntities() {	
		this.entities.clear();
		this.entities.addAll(this.attacker.getEntities());
		this.entities.addAll(this.defender.getEntities());
		
		boolean topPosition = getRandomizer().nextBoolean();
		this.attacker.enterBattle(world, topPosition);
		this.defender.enterBattle(world, !topPosition);
	}


	
	@Override
	protected World createWorld(GameState state) {
		World world = new World(this, camera, "frank_map01");
		return world;
	}
	
	@Override
	public void update(TimeStep timeStep) {				
		super.update(timeStep);
		
		this.ai.update(timeStep);
		this.hud.update(timeStep);
	}

	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {
		this.world.render(canvas, camera, alpha);
		
		this.hud.renderUnderEntities(canvas, camera, alpha);
		this.entities.render(canvas, camera, alpha);
		this.world.renderOverEntities(canvas, camera, alpha);
		
		this.hud.render(canvas, camera, alpha);
	}
	

	public LeaderEntity getOtherLeader(Player player) {
		if(this.attacker.getPlayer() == player) {
			return this.defender;
		}
		return this.attacker;
	}
	
	
	/**
	 * @return the attacker
	 */
	public LeaderEntity getAttacker() {
		return attacker;
	}
	
	/**
	 * @return the defender
	 */
	public LeaderEntity getDefender() {
		return defender;
	}
}

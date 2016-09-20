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
import franks.game.commands.Command.CommandType;
import franks.game.commands.CommandQueue.CommandRequest;
import franks.game.entity.Entity;
import franks.game.entity.EntityList;
import franks.game.entity.meta.LeaderEntity;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.map.IsometricMap;
import franks.math.Vector2f;
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

	public static enum BattleState {
		InProgress,
		Decided,
		Completed,
	}
	
	private Hud hud;			
	private BattleAISystem ai;
	
	private LeaderEntity attacker;
	private LeaderEntity defender;
	
	private BattleState battleState;
	private LeaderEntity victor;
	
	
	/**
	 * 
	 */
	public BattleGame(FranksGame app, GameState state, Camera camera) {
		super(app, state, camera);
		
		this.hud = new Hud(this);	
		this.battleState = BattleState.Completed;
	}
	
	public void enterBattle(Battle battle) {
		this.battleState = BattleState.InProgress;
		this.victor = null;
		
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
	
	/* (non-Javadoc)
	 * @see franks.game.Game#enter()
	 */
	@Override
	public void enter() {
		super.enter();
		IsometricMap map = getMap();
		centerCameraAround(new Vector2f(map.getMapWidth()/2f, map.getMapHeight()/2f));		
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
//		World world = new World(this, camera, "frank_map01");
		World world = new World(this, camera, "battle01");
		return world;
	}
	
	@Override
	public void update(TimeStep timeStep) {				
		super.update(timeStep);
		
		this.ai.update(timeStep);
		this.hud.update(timeStep);
		
		checkVictoryConditions();
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
	
	public LeaderEntity getLeader(Player player) {
		if(this.attacker.getPlayer() == player) {
			return this.attacker;
		}
		return this.defender;
	}
	
	
	public void retreat() {
		doRetreat(getLocalPlayer());
	}
	
	public void aiRetreat() {
		doRetreat(getAIPlayer());
	}
	
	private void doRetreat(Player retreater) {
		if(this.currentTurn.isPlayersTurn(retreater)) {
			retreatPenalty(retreater);
		}
	}
	
	private void retreatPenalty(Player retreater) {
		LeaderEntity victors = getOtherLeader(retreater);
		LeaderEntity losers = getLeader(retreater);
		for(Entity ent : victors.getEntities()) {
			for(Entity loser : losers.getEntities()) {
				if(ent.inAttackRange(loser)) {
					ent.queueAction(new CommandRequest(this, CommandType.Attack, ent, loser));
				}
			}
		}
		
		this.battleState = BattleState.Decided;
		this.victor = victors;
	}
	
	private void checkVictoryConditions() {
		if(this.battleState == BattleState.InProgress) {
			EntityList attackerArmy = this.attacker.getEntities();
			if(attackerArmy.size() <= 0) {
				this.battleState = BattleState.Decided;
				this.victor = this.defender;
			}
			
			EntityList defendingArmy = this.defender.getEntities();
			if(defendingArmy.size() <= 0) {
				this.battleState = BattleState.Decided;
				this.victor = this.attacker;
			}
			
		}
		
		
		if(this.battleState == BattleState.Decided) {
			boolean isCompleted = this.attacker.getEntities().commandsCompleted() &&
					              this.defender.getEntities().commandsCompleted();
			
			if(isCompleted) {
				this.battleState = BattleState.Completed;
				
				this.attacker.leaveBattle(this.victor==this.attacker);
				this.defender.leaveBattle(this.victor==this.defender);				
			}
		}
	}
	
	/**
	 * @return the victor
	 */
	public LeaderEntity getVictor() {
		return victor;
	}
	
	/**
	 * @return the battleState
	 */
	public BattleState getBattleState() {
		return battleState;
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

/*
 * see license.txt 
 */
package franks.game.battle;

import java.util.HashMap;

import franks.FranksGame;
import franks.game.Game;
import franks.game.GameState;
import franks.game.Hud;
import franks.game.Player;
import franks.game.Turn;
import franks.game.World;
import franks.game.ai.AIBattleSystem;
import franks.game.entity.Entity;
import franks.game.entity.Entity.Type;
import franks.game.entity.EntityData;
import franks.game.entity.EntityData.GraphicData;
import franks.game.entity.EntityGroupData;
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

	public static final int MAX_ENTITIES = 256;
	
	private Hud hud;			
	private AIBattleSystem ai;
	
	private LeaderEntity attacker;
	private LeaderEntity defender;
	
	/**
	 * 
	 */
	public BattleGame(FranksGame app, GameState state, Camera camera, Battle battle) {
		super(app, state, camera);
		
		this.hud = new Hud(this);	
		
		
		EntityData redData = new EntityData();		
		redData.type = Type.GENERAL;
		redData.graphics = new GraphicData();
		redData.graphics.sectionStates = new HashMap<>();
		LeaderEntity redLeader = new LeaderEntity(0, this, state.getAIPlayer().getTeam(), redData);
		
		EntityData greenData = new EntityData();
		greenData.type = Type.GENERAL;
		greenData.graphics = new GraphicData();
		greenData.graphics.sectionStates = new HashMap<>();
		LeaderEntity greenLeader = new LeaderEntity(1, this, state.getLocalPlayer().getTeam(), greenData);
		
		battle = new Battle(redLeader, greenLeader);
		
		this.attacker = battle.getAttacker();
		this.defender = battle.getDefender();
		
		EntityGroupData redGroupData = loadGroupData("assets/red.json");
		redLeader.getEntities().addAll(redGroupData.buildEntities(redTeam, this));
		redLeader.shufflePosition(getRandomizer());
		
		EntityGroupData greenGroupData = loadGroupData("assets/green.json");
		greenLeader.getEntities().addAll(greenGroupData.buildEntities(greenTeam, this));
		greenLeader.shufflePosition(getRandomizer());
		
		Player playersTurn = battle.getAttacker().getPlayer();
		
		this.currentTurn = new Turn(this, playersTurn, 0);
		this.ai = new AIBattleSystem(this, state.getAIPlayer());
		
		// temp
		app.getConsole().addCommand(new Command("reload") {
			
			@Override
			public void execute(Console console, String... args) {
				world = createWorld(state);
				prepareEntities();
			}
		});		
		
		prepareEntities();
		
	}
	
	private void prepareEntities() {
		entities.clear();
		
		for(Entity entity : attacker.getEntities()) {
			entities.addEntity(entity);
		}
		
		for(Entity entity : defender.getEntities()) {
			entities.addEntity(entity);
		}
		

		boolean topPosition = getRandomizer().nextBoolean();
		attacker.enterBattle(world, topPosition);
		defender.enterBattle(world, !topPosition);
	}


	
	@Override
	protected World createWorld(GameState state) {
		World world = new World(this, camera);
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

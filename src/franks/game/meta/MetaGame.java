/*
 * see license.txt 
 */
package franks.game.meta;

import java.util.List;

import franks.FranksGame;
import franks.game.Army;
import franks.game.Game;
import franks.game.GameState;
import franks.game.Turn;
import franks.game.World;
import franks.game.ai.MetaAISystem;
import franks.game.battle.BattleGame;
import franks.game.entity.Entity;
import franks.game.entity.meta.LeaderEntity;
import franks.game.meta.StageData.ArmyData;
import franks.game.meta.StageData.GeneralInstanceData;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.map.MapTile.Visibility;
import franks.util.TimeStep;

/**
 * @author Tony
 *
 */
public class MetaGame extends Game {

	private MetaHud hud;
	
	private BattleGame battleGame;
	private MetaAISystem aiSystem;
	
	private FogOfWar fow;
	
	/**
	 * 
	 */
	public MetaGame(FranksGame app, GameState state, Camera camera) {
		super(app, state, camera);
		
		this.battleGame = new BattleGame(app, state, camera);
		
		this.currentTurn = new Turn(this, state.getLocalPlayer(), 1);
		this.hud = new MetaHud(this);
	
		this.aiSystem = new MetaAISystem(this, state.getAIPlayer());
		this.fow = new FogOfWar(this); 
		
		
		StageData stage = this.loadData("assets/stage01.json", StageData.class);
		
		buildArmy(stage.greenArmy, state.getGreenPlayer().getTeam());
		buildArmy(stage.redArmy, state.getRedPlayer().getTeam());		
	}
	
	private void buildArmy(ArmyData armyData, Army army) {
		for(GeneralInstanceData general : armyData.generals) {
			LeaderEntity leader = (LeaderEntity)buildEntity(army, general);
			army.addLeader(leader);
			
			if(general.holding != null) {
				List<Entity> entities = general.holding.buildEntities(leader.getEntities(), army, this.battleGame);
				for(Entity ent : entities) {
					leader.addEntity(ent);
				}
			}
		}
	}

	/**
	 * @return the battleGame
	 */
	public BattleGame getBattleGame() {
		return battleGame;
	}

	@Override
	protected World createWorld(GameState state) {	
		World world = new World(this, camera, "franks_stage_01");	
		world.setVisibility(Visibility.BLACKED_OUT);
		return world;
	}
	
	@Override
	public void update(TimeStep timeStep) {
		this.aiSystem.update(timeStep);		
		super.update(timeStep);
		
		this.fow.update(timeStep);
		
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
}

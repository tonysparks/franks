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
import franks.game.battle.BattleGame;
import franks.game.entity.Entity;
import franks.game.entity.EntityGroupData.EntityInstanceData;
import franks.game.meta.StageData.ArmyData;
import franks.game.meta.StageData.GeneralInstanceData;
import franks.game.net.NetEntity;
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
    private FogOfWar fow;
   
    
    /**
     * 
     */
    public MetaGame(FranksGame app, GameState state, Camera camera) {
        super(app, state, camera);
        
        this.battleGame = new BattleGame(app, state, camera);
        
        this.currentTurn = new Turn(this, state.getLocalPlayer(), 1);
        this.hud = new MetaHud(this);
            
        this.fow = new FogOfWar(this); 
        
        
        StageData stage = this.loadData("assets/stage01.json", StageData.class);
        
        buildArmy(stage.greenArmy, state.getGreenPlayer().getTeam());
        buildArmy(stage.redArmy, state.getRedPlayer().getTeam());        
    }
    
    private void buildArmy(ArmyData armyData, Army army) {
        if(armyData.generals != null) {
            for(GeneralInstanceData general : armyData.generals) {
                Entity leader = buildEntity(army, general);
                army.addLeader(leader);
                
                if(general.holding != null) {
                    List<Entity> entities = general.holding.buildEntities(leader.getHeldEntities(), army, this.battleGame);
                    for(Entity ent : entities) {
                        leader.addHeldEntity(ent);
                    }
                }
            }
        }
        
        // TODO: should this be generic Entity's or should we
        // explicitly call out each entityType?
        if(armyData.workers != null) {
            for(EntityInstanceData entity : armyData.workers) {
                Entity worker = buildEntity(army, entity);
                army.addWorker(worker);
            }
        }
    }
    
    public Entity buildLeaderEntity(Army army, NetEntity net) {
        Entity leader = buildEntity(army, net);        
        for(NetEntity netEntity : net.entities) {
            Entity ent = this.battleGame.buildEntity(leader.getHeldEntities(), army, netEntity);
            leader.addHeldEntity(ent);
        }
        return leader;
    }
    
    /**
     * @return the battleGame
     */
    public BattleGame getBattleGame() {
        return battleGame;
    }

    @Override
    protected World createWorld(GameState state) {    
        World world = new World(state, this, "franks_stage_03");    
        world.setVisibility(Visibility.BLACKED_OUT);
        return world;
    }
    
    @Override
    public void update(TimeStep timeStep) {        
        super.update(timeStep);
        
        this.fow.update(timeStep);    
        this.hud.update(timeStep);
    }
    
    @Override
    public void render(Canvas canvas, Camera camera, float alpha) {
        this.world.render(canvas, camera, alpha);
        
        this.hud.renderUnderEntities(canvas, camera, alpha);
        this.world.getMap().mapRenderFoW(canvas, camera, alpha);
        this.entities.render(canvas, camera, alpha);
        this.world.renderOverEntities(canvas, camera, alpha);
        this.hud.render(canvas, camera, alpha);
    }
    
}

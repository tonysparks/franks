/*
 * see license.txt 
 */
package franks.game.actions;

import franks.game.Game;
import franks.game.battle.Battle;
import franks.game.battle.BattleGame;
import franks.game.entity.Entity;
import franks.game.entity.EntityData.AttackActionData;
import franks.game.events.BattleEvent;
import franks.game.meta.MetaGame;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.screens.BattleScreen;
import franks.util.TimeStep;

/**
 * @author Tony
 *
 */
public class LeaderAttackAction extends AttackAction {    
    private Entity leaderAttacker;
    
    /**
     * @param name
     * @param movementCost
     */
    public LeaderAttackAction(Game game, Entity attacker, AttackActionData data) {
        super(game, attacker, data);
        
        this.leaderAttacker = attacker;
        this.attackDistance = data.attackRange;        
    }

    @Override
    protected ExecutedAction doActionImpl(Game game, Command command) {
        Entity enemy = command.targetEntity.get();
        return new ExecutedAction(command) {
                    
            @Override
            public ExecutedAction start() {
                
                
                return this;
            }
            
            @Override
            public ExecutedAction end() {
                MetaGame meta = (MetaGame) game;
                BattleGame battleGame = meta.getBattleGame();                
                Battle battle = new Battle(leaderAttacker, enemy);
                
                game.dispatchEvent(new BattleEvent(this, battle));
                
                battleGame.enterBattle(battle);
                game.getApp().pushScreen(new BattleScreen(game.getApp(), game.getState(), battleGame));
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

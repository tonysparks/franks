/*
 * see license.txt 
 */
package franks.game.actions;

import franks.game.Game;
import franks.game.entity.Entity;
import franks.game.entity.Entity.State;
import franks.game.entity.EntityData.AttackActionData;
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
public class BattleAttackAction extends AttackAction {

    /**
     * @param name
     * @param movementCost
     */
    public BattleAttackAction(Game game, Entity attacker, AttackActionData data) {
        super(game, attacker, data);
    }

    @Override
    protected ExecutedAction doActionImpl(Game game, Command command) {
        MapTile tile = game.getTile(command.cursorTilePos);
        Entity attacker = getEntity();
        Entity enemy = command.targetEntity.get();
        return new ExecutedAction(command) {
            
            Timer timer = new Timer(false, attacker.getData().getAnimationTime(State.ATTACKING));
                        
            @Override
            public ExecutedAction start() {
                timer.start();
                
                if(tile != null) {
                    attacker.lookAt(tile);
                }
                
                attacker.setCurrentState(State.ATTACKING);
                return this;
            }
            
            @Override
            public ExecutedAction end() {
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
}

/*
 * see license.txt 
 */
package franks.game.actions;

import franks.game.Game;
import franks.game.PreconditionResponse;
import franks.game.entity.Direction;
import franks.game.entity.Entity;
import franks.game.entity.Entity.State;
import franks.game.entity.EntityData.BuildActionData;
import franks.game.entity.EntityGroupData.EntityInstanceData;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.map.MapTile;
import franks.math.Vector2f;
import franks.sfx.Sounds;
import franks.util.TimeStep;
import franks.util.Timer;

/**
 * Builds a building
 * 
 * @author Tony
 *
 */
public class BuildAction extends Action {

    private int numberOfTurnsToComplete;
    
    /**
     * @param actionCost
     * @param entity
     */
    public BuildAction(BuildActionData data, Entity entity) {
        super(ActionType.Build, data.cost, entity);
        this.numberOfTurnsToComplete = data.numberOfTurns;
    }

    @Override
    public PreconditionResponse checkPreconditions(Game game, Command request) {
        PreconditionResponse response = new PreconditionResponse();
        
        if(!getEntity().canDo(getType())) {
            response.addFailure("This entity can not build");
        }
        
        // TODO: Verify width/height of the building fits!
        
        checkCost(response, game);
        return response;
    }


    @Override
    protected ExecutedAction doActionImpl(final Game game, Command command) {        
        MapTile tile = game.getTile(command.cursorTilePos);
        return new ExecutedAction(command) {
            
            Timer timer = new Timer(false, getEntity().getData().getAnimationTime(State.BUILDING));
            int startingTurn = game.getCurrentTurn().getNumber();
            boolean isCancelled = false;
            
            @Override
            public boolean spansTurns() {
                return true;
            }
            
            @Override
            public ExecutedAction start() {
                timer.start();
                Sounds.playGlobalSound(Sounds.build);
                
                if(tile != null) {
                    getEntity().lookAt(tile);
                }
                getEntity().setCurrentState(State.BUILDING);
                
                return this;
            }
            
            
            @Override
            public ExecutedAction end() {
                // TODO: Create the building
                EntityInstanceData buildingData = new EntityInstanceData();
                buildingData.dataFile = getEntity().getTeam().getName().toLowerCase() + "_town_center.json";
                buildingData.direction = Direction.SOUTH;
                buildingData.x = tile.getXIndex();
                buildingData.y = tile.getYIndex();
                
                game.buildEntity(getEntity().getTeam(), buildingData);
                
                getEntity().setCurrentState(State.IDLE);
                return super.end();
            }
            
            @Override
            public void update(TimeStep timeStep) {     
                timer.update(timeStep);             
            }
            
            @Override
            public void render(Canvas canvas, Camera camera, float alpha) {
                Vector2f pos = camera.getRenderPosition(alpha);
                
                game.getWorld().getMap().renderIsoRect(canvas, 
                                                       tile.getIsoX()-pos.x, 
                                                       tile.getIsoY()-pos.y, 
                                                       96, 
                                                       96, 0xffffffff);
            }
            
            @Override
            public CompletionState getCurrentState() {
                if(isCancelled) {
                    return CompletionState.Cancelled;
                }
                
                int currentTurn = game.getCurrentTurn().getNumber();
                if( (currentTurn-startingTurn) > numberOfTurnsToComplete) {
                    return CompletionState.Success;
                }
                
                return CompletionState.InProgress;
            }
            
            @Override
            public void cancel() {
                timer.setEndTime(0);
                isCancelled = true;
            }
        };
    }

}

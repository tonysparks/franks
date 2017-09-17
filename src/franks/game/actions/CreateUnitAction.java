/*
 * see license.txt 
 */
package franks.game.actions;

import franks.game.Game;
import franks.game.PreconditionResponse;
import franks.game.Resources;
import franks.game.entity.Direction;
import franks.game.entity.Entity;
import franks.game.entity.EntityData.BuildData;
import franks.game.entity.EntityGroupData.EntityInstanceData;
import franks.game.entity.EntityState;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.sfx.Sounds;
import franks.util.TimeStep;

/**
 * Creates an {@link Entity}
 * 
 * @author Tony
 *
 */
public class CreateUnitAction extends Action {

    private BuildData data;
        
    /**
     * @param data
     * @param entity
     */
    public CreateUnitAction(BuildData data, Entity entity) {
        super(data.actionType, data.actionPoints, entity);
        
        this.data = data;
    }
    
    @Override
    public String getDisplayName() {
        return this.data.displayName;
    }

    @Override
    public PreconditionResponse checkPreconditions(Game game, Command request) {
        PreconditionResponse response = new PreconditionResponse();
        
        if(!getEntity().canDo(getType())) {
            response.addFailure("This entity can not create entities");
        }
        
        Resources resources = getEntity().getResources();                   
        if(!resources.hasGoldAmount(data.resources.gold)) {
            response.addFailure("Does not have enough gold");
        }
        if(!resources.hasFoodAmount(data.resources.food)) {
            response.addFailure("Does not have enough food");
        }
        if(!resources.hasMaterialAmount(data.resources.material)) {
            response.addFailure("Does not have enough material");
        }
        
        checkCost(response, game);
        return response;
    }


    @Override
    protected ExecutedAction doActionImpl(final Game game, Command command) {                
        return new ExecutedAction(command) {            
            int startingTurn = game.getCurrentTurn().getNumber();
            boolean isCancelled = false;
            
            @Override
            public boolean spansTurns() {
                return true;
            }
            
            @Override
            public ExecutedAction start() {
                Sounds.playGlobalSound(Sounds.build);
                getEntity().setCurrentState(EntityState.BUILDING);               
                return this;
            }
            
            
            @Override
            public ExecutedAction end() {
                // Creates the new Entity
                EntityInstanceData entityData = new EntityInstanceData();
                entityData.dataFile = getEntity().getEntityDataFileName(data.entityType);
                entityData.direction = Direction.SOUTH;
                                
                Entity newEntity = game.buildEntity(getEntity().getTeam(), entityData);
                
                getEntity().addHeldEntity(newEntity);
                getEntity().setCurrentState(EntityState.IDLE);
                
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
                if(isCancelled) {
                    return CompletionState.Cancelled;
                }
                
                int currentTurn = game.getCurrentTurn().getNumber();
                if( (currentTurn-startingTurn) > data.numberOfTurns) {
                    return CompletionState.Success;
                }
                
                return CompletionState.InProgress;
            }
            
            @Override
            public void cancel() {
                isCancelled = true;
            }
        };
    }

}

/*
 * see license.txt 
 */
package franks.game.commands;

import franks.game.Game;
import franks.game.commands.CommandQueue.CommandRequest;
import franks.game.entity.Direction;
import franks.game.entity.Entity;
import franks.game.entity.Entity.State;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.math.Vector2f;
import franks.util.TimeStep;

/**
 * @author Tony
 *
 */
public class LeaderMovementCommand extends MovementCommand {

	/**
	 * @param name
	 * @param movementCost
	 */
	public LeaderMovementCommand(Game game, Entity entity, int movementSpeed) {
		super(game, entity, movementSpeed);

	}

	@Override
	protected CommandAction doActionImpl(Game game, CommandRequest request) {
		Entity entity = getEntity();				
		return new CommandAction(request) {
			boolean isCancelled;
			boolean atDestination;
			
			@Override
			public CommandAction start() {
				entity.setCurrentState(State.WALKING);
				return this;
			}
			
			@Override
			public CommandAction end() {
				entity.setCurrentState(State.IDLE);				
				return super.end();
			}
			 
			@Override
			public CompletionState getCurrentState() {
				return atDestination ? CompletionState.Success :
					isCancelled ? CompletionState.Cancelled : CompletionState.InProgress;
			}
			
			@Override
			public void cancel() {
				this.isCancelled = true;
				planner.clearPath();
			}
			
			@Override
			public void update(TimeStep timeStep) {
				Vector2f waypoint = planner.nextWaypoint(entity);
				if(waypoint==null) { 
					atDestination = true;
					return;
				}

				Vector2f pos = entity.getPos();
				Vector2f vel = new Vector2f();
				Vector2f.Vector2fNormalize(waypoint, vel);
				entity.setCurrentDirection(Direction.getDirection(vel));				
				
				float dt = (float)timeStep.asFraction();			
				float deltaX = (vel.x * movementSpeed * dt);
				float deltaY = (vel.y * movementSpeed * dt);
				
				float newX = pos.x + deltaX;
				float newY = pos.y + deltaY;
				
				entity.moveTo(newX, newY);
				
				if(planner.atDestination()) {
					if(Vector2f.Vector2fApproxEquals(entity.getCenterPos(), planner.getDestination(), 16f)) {						
						atDestination = true;
					}
				}
//				else {
//					entity.visitTiles(game.getMap());
//				}
			}
			
			@Override
			public void render(Canvas canvas, Camera camera, float alpha) {				
			}
		};
	}

}

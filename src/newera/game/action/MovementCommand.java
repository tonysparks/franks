/*
 * see license.txt 
 */
package newera.game.action;

import newera.game.Command;
import newera.game.CommandAction;
import newera.game.CommandQueue.CommandRequest;
import newera.game.Game;
import newera.game.PathPlanner;
import newera.game.PreconditionResponse;
import newera.game.entity.Entity;
import newera.gfx.Camera;
import newera.gfx.Canvas;
import newera.map.MapTile;
import newera.math.Vector2f;
import newera.util.TimeStep;

/**
 * @author Tony
 *
 */
public class MovementCommand extends Command {

	private PathPlanner<Void> planner;
	private Entity entity;
	
	/**
	 * @param name
	 * @param movementCost
	 */
	public MovementCommand(Game game, Entity entity) {
		super("moveTo", -1);		
		this.entity = entity;
		this.planner = new PathPlanner<>(game, game.getWorld().getGraph(), entity);
	}

	/* (non-Javadoc)
	 * @see newera.game.Action#checkPreconditions(newera.game.Game)
	 */
	@Override
	public PreconditionResponse checkPreconditions(Game game, CommandRequest request) {
		PreconditionResponse response = new PreconditionResponse();
		
		Vector2f dst = game.getWorld().snapMapTilePos(game.getMouseWorldPos());
		if(dst!=null) {
			this.planner.findPath(entity.getCenterPos(), dst);		
			setMovementCost(this.planner.getPath().size());
		}
		else {
			response.addFailure("Invalid destination");
		}
		
		if(!this.planner.hasPath()) {
			response.addFailure("Invalid destination");
		}
		else {
			checkMovement(response, game);
		}
		
		return response;
	}

	/* (non-Javadoc)
	 * @see newera.game.Action#doAction(newera.game.Game)
	 */
	@Override
	public CommandAction doAction(Game game, CommandRequest request) {
		game.getMoveMeter().decrementMovement(getMovementCost());
		return new CommandAction() {
			boolean isCancelled;
			boolean atDestination;
			
			@Override
			public CompletionState getCurrentState() {
				return atDestination ? CompletionState.Success :
					isCancelled ? CompletionState.Cancelled : CompletionState.InProgress;
			}
			
			@Override
			public void cancel() {
				this.isCancelled = true;
			}
			
			@Override
			public void update(TimeStep timeStep) {
				Vector2f waypoint = planner.nextWaypoint(entity);
				Vector2f pos = entity.getPos();
				Vector2f vel = new Vector2f();
				Vector2f.Vector2fNormalize(waypoint, vel);
				
				int movementSpeed = 80;
				
				
				double dt = timeStep.asFraction();
				int newX = (int)Math.round(pos.x + vel.x * movementSpeed * dt);
				int newY = (int)Math.round(pos.y + vel.y * movementSpeed * dt);
				entity.moveTo(newX, newY);
				
				if(planner.atDestination()) {
					if(Vector2f.Vector2fApproxEquals(entity.getCenterPos(), planner.getDestination(), 32f)) {
						//entity.moveToCenter(planner.getDestination());
						MapTile tile = game.getWorld().getMapTileByWorldPos(planner.getDestination());
						if(tile!=null) {
							entity.moveTo(tile.getX(), tile.getY());
						}
						
						atDestination = true;
					}
				}
			}
			
			/* (non-Javadoc)
			 * @see newera.gfx.Renderable#render(newera.gfx.Canvas, newera.gfx.Camera, float)
			 */
			@Override
			public void render(Canvas canvas, Camera camera, float alpha) {
				planner.getPath().forEach(node -> {
					MapTile tile = node.getValue();
					canvas.drawRect(tile.getRenderX(), tile.getRenderY(), tile.getWidth(), tile.getHeight(), 0xffffffff);
				});
			}
		};
	}

}

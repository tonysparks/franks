/*
 * see license.txt 
 */
package franks.game.action;

import franks.game.Command;
import franks.game.CommandAction;
import franks.game.Game;
import franks.game.PathPlanner;
import franks.game.PreconditionResponse;
import franks.game.CommandQueue.CommandRequest;
import franks.game.entity.Entity;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.map.MapTile;
import franks.math.Vector2f;
import franks.util.TimeStep;

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
				
				int movementSpeed = 160;
				
				
//				double dt = timeStep.asFraction();
//				int newX = (int)Math.round(pos.x + vel.x * movementSpeed * dt);
//				int newY = (int)Math.round(pos.y + vel.y * movementSpeed * dt);
				
				float dt = (float)timeStep.asFraction();			
				float deltaX = (vel.x * movementSpeed * dt);
				float deltaY = (vel.y * movementSpeed * dt);
				
				float newX = pos.x + deltaX;
				float newY = pos.y + deltaY;
				
				entity.moveTo(newX, newY);
				
				if(planner.atDestination()) {
					if(Vector2f.Vector2fApproxEquals(entity.getCenterPos(), planner.getDestination(), 16f)) {
						//entity.moveToCenter(planner.getDestination());
						MapTile tile = game.getWorld().getMapTileByWorldPos(planner.getDestination());
						if(tile!=null) {
							//entity.moveTo(tile.getX(), tile.getY());
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
					game.getWorld().getMap().renderIsoRect(canvas, tile.getRenderX(), tile.getRenderY(), tile.getWidth(), tile.getHeight(), 0xffffffff);
				});
			}
		};
	}

}

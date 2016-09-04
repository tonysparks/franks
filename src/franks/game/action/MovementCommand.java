/*
 * see license.txt 
 */
package franks.game.action;

import java.util.List;

import franks.game.Game;
import franks.game.PathPlanner;
import franks.game.PreconditionResponse;
import franks.game.TerrainData.TerrainTileData;
import franks.game.commands.Command;
import franks.game.commands.CommandAction;
import franks.game.commands.CommandQueue.CommandRequest;
import franks.game.entity.Direction;
import franks.game.entity.Entity;
import franks.game.entity.Entity.State;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.graph.GraphNode;
import franks.map.MapTile;
import franks.math.Vector2f;
import franks.util.TimeStep;

/**
 * @author Tony
 *
 */
public class MovementCommand extends Command {

	private PathPlanner<Void> planner, costPlanner;
	private int movementSpeed;
//	private Game game;
	/**
	 * @param name
	 * @param movementCost
	 */
	public MovementCommand(Game game, Entity entity, int movementSpeed) {
		super(CommandType.Move, -1, entity);
//		this.game = game;
		
		this.movementSpeed = movementSpeed;
		this.planner = new PathPlanner<>(game, game.getWorld().getGraph(), entity);
		this.costPlanner = new PathPlanner<>(game, game.getWorld().getGraph(), entity);
	}

	
	public int calculateCost(Vector2f destination) {
		return calculateCost(costPlanner, destination);
	}
	
	private int calculateCost(PathPlanner<Void> planner, Vector2f destination) {
		if(destination != null) {
			Vector2f dst = destination; 
			if(dst!=null) {
				planner.findPath(getEntity().getCenterPos(), dst);				
				List<GraphNode<MapTile, Void>>  path = planner.getPath();
				int sumCost = 0;
				int unitMoveCost = getEntity().movementBaseCost();
				for(int i = 0; i < path.size(); i++) {
					MapTile tile = path.get(i).getValue();
					TerrainTileData terrain = tile.geTerrainTileData();
					if(terrain != null) {
						sumCost += terrain.movementBonus;
					}
					sumCost += unitMoveCost;
				}
				
				return sumCost;
			}
		}
		
		return -1;
	}
	
	/* (non-Javadoc)
	 * @see newera.game.Action#checkPreconditions(newera.game.Game)
	 */
	@Override
	public PreconditionResponse checkPreconditions(Game game, CommandRequest request) {
		PreconditionResponse response = new PreconditionResponse();
				
		int cost = calculateCost(planner, request.cursorTilePos);
		if(cost < 0) {
			response.addFailure("Invalid destination");
		}
		else {
			setActionCost(cost);
		}
		
		
		if(!this.planner.hasPath()) {
			response.addFailure("Invalid destination");
		}
		else {
			checkCost(response, game);
		}
		
		return response;
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
				entity.setToDesiredDirection();
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
			}
			
			/* (non-Javadoc)
			 * @see newera.gfx.Renderable#render(newera.gfx.Canvas, newera.gfx.Camera, float)
			 */
			@Override
			public void render(Canvas canvas, Camera camera, float alpha) {
//				Vector2f c = camera.getRenderPosition(alpha);
//				planner.getPath().forEach(node -> {
//					MapTile tile = node.getValue();
//					game.getWorld().getMap().renderIsoRect(canvas, tile.getIsoX()-c.x, tile.getIsoY()-c.y, tile.getWidth(), tile.getHeight(), 0xffffffff);
//					
//				});
				
			}
		};
	}

}

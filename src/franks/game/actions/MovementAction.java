/*
 * see license.txt 
 */
package franks.game.actions;

import java.util.List;

import franks.game.Game;
import franks.game.PathPlanner;
import franks.game.PreconditionResponse;
import franks.game.TerrainData.TerrainTileData;
import franks.game.entity.Direction;
import franks.game.entity.Entity;
import franks.game.entity.Entity.State;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.graph.GraphNode;
import franks.map.MapTile;
import franks.math.Vector2f;
import franks.sfx.Sounds;
import franks.util.TimeStep;
import franks.util.Timer;

/**
 * @author Tony
 *
 */
public class MovementAction extends Action {

	protected PathPlanner<Void> planner, costPlanner;
	protected int movementSpeed;
	protected Game game;
	/**
	 * @param name
	 * @param movementCost
	 */
	public MovementAction(Game game, Entity entity, int movementSpeed) {
		super(ActionType.Move, -1, entity);
		this.game = game;
		
		this.movementSpeed = movementSpeed;
		this.planner = new PathPlanner<>(game, game.getWorld().getGraph(), entity);
		this.costPlanner = new PathPlanner<>(game, game.getWorld().getGraph(), entity);
	}

	
	public int calculateCost(Vector2f tilePosDestination) {
		return calculateCost(costPlanner, tilePosDestination);
	}
	
	private int calculateCost(PathPlanner<Void> planner, Vector2f tilePosDestination) {
		if(tilePosDestination != null) {
			Vector2f dst = tilePosDestination; 
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
	public PreconditionResponse checkPreconditions(Game game, Command command) {
		PreconditionResponse response = new PreconditionResponse();
				
		int cost = calculateCost(planner, command.cursorTilePos);
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
	protected ExecutedAction doActionImpl(Game game, Command command) {
		Entity entity = getEntity();				
		return new ExecutedAction(command) {
			boolean isCancelled;
			boolean atDestination;
			
			Timer footsteps = new Timer(true, 400);
			
			@Override
			public ExecutedAction start() {
				entity.setCurrentState(State.WALKING);
				Sounds.playGlobalSound(Sounds.ruffle);
				return this;
			}
			
			@Override
			public ExecutedAction end() {
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
				footsteps.update(timeStep);
				
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
				
				if(footsteps.isOnFirstTime()) {
					Sounds.playGlobalSound(Sounds.grassWalk, 0.32f);
				}
				
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

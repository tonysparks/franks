/*
 * see license.txt 
 */
package newera.game.action;

import newera.game.Command;
import newera.game.CommandAction;
import newera.game.Game;
import newera.game.PreconditionResponse;
import newera.game.CommandQueue.CommandRequest;
import newera.game.entity.Entity;
import newera.gfx.Camera;
import newera.gfx.Canvas;
import newera.gfx.Colors;
import newera.math.Vector2f;
import newera.util.TimeStep;
import newera.util.Timer;

/**
 * @author Tony
 *
 */
public class CollectResourceCommand2 extends Command {

	private Entity collector;
	private String resourceName;
	
	/**
	 * @param name
	 * @param movementCost
	 */
	public CollectResourceCommand2(String resourceName, Entity collector) {
		super("collect" + resourceName, 1);
		this.resourceName = resourceName;	
		this.collector = collector;
	}
	
	/**
	 * @return the resourceName
	 */
	public String getResourceName() {
		return resourceName;
	}

	/* (non-Javadoc)
	 * @see newera.game.Command#checkPreconditions(newera.game.Game)
	 */
	@Override
	public PreconditionResponse checkPreconditions(Game game, CommandRequest request) {
		PreconditionResponse response = new PreconditionResponse();
		 
		if(!request.targetEntity.isPresent()) {
			response.addFailure("No resource to collect from");
		}
		else {
			Entity resource = request.targetEntity.get();
			if(!resource.hasAttribute(resourceName)) {
				response.addFailure(resource.getType() + " doesn't have any " + resourceName);
			}
			
			
			if((collector.getResourceCollectionPower(resourceName) <= 0)) {
				response.addFailure("The selected entity is unable to collect the " + resourceName);	
			}			
			
			checkMovement(response, game);
			
			if(!collector.isWithinRange(resource)) {
				response.addFailure("The selected entity must be within range to collect the " + resourceName);
			}
			
		}
		return response;
	}

	/* (non-Javadoc)
	 * @see newera.game.Command#doAction(newera.game.Game)
	 */
	@Override
	public CommandAction doAction(Game game, CommandRequest request) {
		game.getMoveMeter().decrementMovement(getMovementCost());
		return new CommandAction() {
			boolean isCancelled = false;
			boolean isCompleted = false;
			int deltaAmount = -1;
			Timer timer = new Timer(false, 600);
			Entity resource = request.targetEntity.get();
			Vector2f effectPos = new Vector2f(resource.getCenterPos());
			int alphaColor = 255;
			
			@Override
			public void cancel() {
				this.isCancelled = true;
			}
			
			@Override
			public CompletionState getCurrentState() {
				if(isCancelled) {
					return CompletionState.Cancelled;
				}
				
				return isCompleted ? CompletionState.Success : 
					CompletionState.InProgress;
			}
			
			@Override
			public void update(TimeStep timeStep) {
				this.timer.update(timeStep);
				if(!isCancelled) {
					if(deltaAmount == -1) {
						int total = resource.attributeAsInt(resourceName);
						Entity collector = request.selectedEntity.get();
						int power = collector.getResourceCollectionPower(resourceName);
						deltaAmount = Math.min(total, power);						
						total -= deltaAmount;
						resource.attribute(resourceName, total);
						
						resource.checkStatus();
						
						// add to our resource bank
						game.getResources().addResource(resourceName, deltaAmount);
					}
					
					if(this.timer.isOnFirstTime()) {
						this.isCompleted = true;
					}
					this.alphaColor -= 12;
					this.effectPos.y -= 2.5f;
				}
			}
			
			@Override
			public void render(Canvas canvas, Camera camera, float alpha) {				
				//RenderFont.drawShadedString(canvas, "" + deltaAmount, effectPos.x, effectPos.y, Colors.setAlpha(0xfffffff, Math.max(alphaColor, 0)));
				canvas.drawString("" + deltaAmount, effectPos.x, effectPos.y, Colors.setAlpha(0xfffffff, Math.max(alphaColor, 0)));
			}
		};
	}

}

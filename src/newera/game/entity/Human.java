/*
 * see license.txt 
 */
package newera.game.entity;

import newera.game.Game;
import newera.game.Randomizer;
import newera.game.action.CollectResourceCommand2;
import newera.game.action.MovementCommand;
import newera.gfx.Art;
import newera.gfx.Camera;
import newera.gfx.Canvas;
import newera.math.Vector2f;

/**
 * @author Tony
 *
 */
public class Human extends Entity {

	/**
	 * @param type
	 */
	public Human(Game game) {
		super(game, Type.HUMAN, new Vector2f(), 32, 32);
		attribute("health", 100);
		attribute("happiness", 80);
		attribute("resourceCollectionPowerWood", 5);
		attribute("resourceCollectionPowerStone", 2);
		attribute("resourceCollectionPowerFood", 4);
		attribute("consumeFoodRate", 2);
		attribute("food", 5);
		
		addAvailableAction(new MovementCommand(game, this));
		addAvailableAction(new CollectResourceCommand2("Wood", this));
		addAvailableAction(new CollectResourceCommand2("Stone", this));
		addAvailableAction(new CollectResourceCommand2("Food", this));
	}

	public double percentage(String attr, int max) {
		return (double)attributeAsInt(attr) / (double)max;
	}
	public boolean inRange(double value, double min, double max) {
		return value >= min && value < max;
	}
	
	public void calculateStatus() {
		Randomizer rand = game.getRandomizer();
		double fedPercentage = percentage("food", 5);
		if(fedPercentage < 0.5f) {
			deltaAttribute("happiness", -rand.nextInt(5, 1.0f-fedPercentage));
		}
		
		deltaAttribute("food", -attributeAsInt("consumeFoodRate"));
		if(attributeAsInt("food") <= 0) {
			deltaAttribute("health", -rand.nextInt(3));
		}
		
		if(attributeAsInt("health") <= 0) {
			kill();
		}
	}
	
	@Override
	protected void doRender(float dx, float dy, Canvas canvas, Camera camera, float alpha) {
		if(isSelected()) {
			canvas.fillCircle(16, dx, dy, 0xcfffffff);
		}
		canvas.drawImage(Art.soldier, dx, dy, null);
	}
	
	
}

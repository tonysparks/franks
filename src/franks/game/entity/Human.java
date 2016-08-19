/*
 * see license.txt 
 */
package franks.game.entity;

import franks.game.Game;
import franks.game.Randomizer;
import franks.game.action.CollectResourceCommand2;
import franks.game.action.MovementCommand;
import franks.gfx.Art;
import franks.math.Vector2f;

/**
 * @author Tony
 *
 */
public class Human extends TempEntity {

	/**
	 * @param type
	 */
	public Human(Game game) {
		super(game, Type.HUMAN, Art.soldier, new Vector2f(), 32, 32);
		attribute("health", 100);
		attribute("happiness", 80);
		attribute("resourceCollectionPowerWood", 5);
		attribute("resourceCollectionPowerStone", 2);
		attribute("resourceCollectionPowerFood", 4);
		attribute("consumeFoodRate", 2);
		attribute("food", 5);
		
		addAvailableAction(new MovementCommand(game, this, 100));
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
	
}

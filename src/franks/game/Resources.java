/*
 * see license.txt 
 */
package franks.game;

import franks.game.entity.Entity;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.gfx.RenderFont;
import franks.gfx.Renderable;
import franks.util.Cons;
import franks.util.TimeStep;

/**
 * @author Tony
 *
 */
public class Resources implements Renderable {

	private int wood;
	private int stone;
	private int gold;
	private int food;
	
	/**
	 * 
	 */
	public Resources() {		
	}

	public void addResource(String resourceName, int delta) {
		switch(resourceName.toLowerCase()) {
			case "wood":
				wood += delta;
				break;
			case "stone":
				stone += delta;
				break;
			case "gold":
				gold += delta;
				break;
			case "food":
				food += delta;
				break;
			default: Cons.println("***ERROR: Unknown resource: '" + resourceName + "'");
		}
	}
	
	public void feed(Entity ent) {
		int foodRequirements = ent.attributeAsInt("consumeFoodRate");
		int delta = Math.min(food, foodRequirements);
		food -= delta;
		ent.deltaAttribute("food", delta);		
	}
	
	@Override
	public void update(TimeStep timeStep) {
		
	}
	
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {
		int textColor = 0xff00ff00;
		int y = 690;
		RenderFont.drawShadedString(canvas, "Food: " + this.food, 10, y, textColor);
		RenderFont.drawShadedString(canvas, "Wood: " + this.wood, 10, y+=15, textColor);
		RenderFont.drawShadedString(canvas, "Stone: " + this.stone, 10, y+=15, textColor);
		RenderFont.drawShadedString(canvas, "Gold: " + this.gold, 10, y+=15, textColor);
	}
}

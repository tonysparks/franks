/*
 * see license.txt 
 */
package newera.game;

import newera.game.entity.Entity;
import newera.gfx.Camera;
import newera.gfx.Canvas;
import newera.gfx.RenderFont;
import newera.gfx.Renderable;
import newera.util.Cons;
import newera.util.TimeStep;

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
		
		RenderFont.drawShadedString(canvas, "Food: " + this.food, 10, 540, textColor);
		RenderFont.drawShadedString(canvas, "Wood: " + this.wood, 10, 555, textColor);
		RenderFont.drawShadedString(canvas, "Stone: " + this.stone, 10, 570, textColor);
		RenderFont.drawShadedString(canvas, "Gold: " + this.gold, 10, 585, textColor);
	}
}

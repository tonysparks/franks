/*
 * see license.txt 
 */
package franks.game.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import franks.game.Game;
import franks.game.entity.EntityData.GraphicData;
import franks.gfx.AnimatedImage;
import franks.gfx.AnimationFrame;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.gfx.FramedAnimation;
import franks.gfx.Renderable;
import franks.gfx.TextureUtil;
import franks.math.Vector2f;
import franks.util.TimeStep;
import franks.util.Updatable;

/**
 * @author Tony
 *
 */
public class EntityModel implements Renderable {

	private static class Model implements Updatable {
		AnimatedImage animations;
		float offsetX;
		float offsetY;

		public Model(AnimatedImage animations, float offsetX, float offsetY) {
			this.animations = animations;
			this.offsetX = offsetX;
			this.offsetY = offsetY;
		}
		
		@Override
		public void update(TimeStep timeStep) {
			this.animations.update(timeStep);		
		}				
	}
	
	private Entity entity;
	private Model[][] animations;
	private Sprite sprite;
		
	/**
	 * @param game
	 * @param entity
	 * @param graphics
	 */
	public EntityModel(Game game, Entity entity, GraphicData graphics) {
		this.entity = entity;
		
		this.animations = new Model[Entity.State.values().length][];
		this.sprite = new Sprite();
		
		
		graphics.sectionStates.forEach( (k,v) -> {
			TextureRegion tex = game.getTextureCache().getTexture(v.filePath);			
			tex = TextureUtil.subImage(tex, v.x, v.y, v.getWidth(tex), v.getHeight(tex));			
			int numberOfDirections = Direction.values().length;
			int rowHeight = tex.getRegionHeight() / numberOfDirections; 
			
			animations[k.ordinal()] = new Model[numberOfDirections];
			for(int dirIndex = 0; dirIndex < numberOfDirections; dirIndex++) {				
				TextureRegion subTex = TextureUtil.subImageRegion(tex, 0, dirIndex * rowHeight, v.getWidth(tex), rowHeight);
				TextureRegion[] frames = TextureUtil.splitImageRegion(subTex, 1, v.numberOfFrames);				
				AnimationFrame[] aFrames = new AnimationFrame[frames.length];
				for(int i = 0; i < aFrames.length; i++) {
					frames[i].flip(v.flipX, v.flipY);
					aFrames[i] = new AnimationFrame(v.frameTime, i);
				}
				
				AnimatedImage image = new AnimatedImage(frames, new FramedAnimation(aFrames)).loop(v.loop);
				animations[k.ordinal()][v.directions[dirIndex].ordinal()] = new Model(image, v.offsetX, v.offsetY);
			}
		});
	}

	
	private Model getCurrentModel() {
		return this.animations[entity.getCurrentState().ordinal()][entity.getCurrentDirection().ordinal()];
	}
	
	/**
	 * Resets the animation to the beginning frame
	 */
	public void resetAnimation() {
		getCurrentModel().animations.getAnimation().reset();
	}
	
	@Override
	public void update(TimeStep timeStep) {
		getCurrentModel().update(timeStep);
	}
	
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {
		Model model = getCurrentModel();
		TextureRegion tex = model.animations.getCurrentImage();
		
		
		float dx = -1;
		float dy = -1;
		
		Vector2f renderPos = entity.getRenderPosition(camera, alpha);
		dx = renderPos.x + model.offsetX;
		dy = renderPos.y + model.offsetY;
		
		this.sprite.setRegion(tex);
		this.sprite.setSize(tex.getRegionWidth(), tex.getRegionHeight());
		this.sprite.setPosition(dx, dy);
		
		if(entity.getGame().hasSelectedEntity()) {
		
			if(entity.isSelected() || entity.isHoveredOver()) {
				
				//this.sprite.setColor(0.71f, 0.95f, 0.71f, 0.99f);
				this.sprite.setColor(Color.WHITE);
			}
			else {
				this.sprite.setColor(entity.getTeam().getColor());
				this.sprite.setAlpha(0.49f);
			}
		}
		else {
			this.sprite.setColor(Color.WHITE);
		}
		
		canvas.drawRawSprite(this.sprite);
		//canvas.drawImage(tex, dx, dy, 0xffffffff);
	}
}

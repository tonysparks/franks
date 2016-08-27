/*
 * see license.txt 
 */
package franks.game.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import franks.game.Game;
import franks.game.World;
import franks.game.entity.Entity.Direction;
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
	
	private Game game;
	private Entity entity;
	
	private Model[][] animations;
	private Vector2f renderPos;
		
	/**
	 * 
	 */
	public EntityModel(Game game, Entity entity, GraphicData graphics) {
		this.game = game;		
		this.entity = entity;
		
		this.renderPos = new Vector2f();
		this.animations = new Model[Entity.State.values().length][];
		
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

	public void resetAnimation() {
		this.animations[entity.getCurrentState().ordinal()][entity.getCurrentDirection().ordinal()].animations.getAnimation().reset();
	}
	
	@Override
	public void update(TimeStep timeStep) {
		this.animations[entity.getCurrentState().ordinal()][entity.getCurrentDirection().ordinal()].update(timeStep);
	}
	
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {
		Model model = this.animations[entity.getCurrentState().ordinal()][entity.getCurrentDirection().ordinal()];
		TextureRegion tex = model.animations.getCurrentImage();
		
		Vector2f cameraPos = camera.getRenderPosition(alpha);

		World world = game.getWorld();
		
		float dx = -1;
		float dy = -1;
		
		Vector2f pos = entity.getPos();
		
		float tileX = (pos.x / world.getRegionWidth());
		float tileY = (pos.y / world.getRegionHeight());
		world.getMap().isoIndexToScreen(tileX, tileY, renderPos);
		Vector2f.Vector2fSubtract(renderPos, cameraPos, renderPos);

		dx = renderPos.x + model.offsetX;
		dy = renderPos.y + model.offsetY;
		dx -= 32;
		dy -= 32;
		
		canvas.drawImage(tex, dx, dy, 0xffffffff);
//		canvas.drawString(dx+","+dy, dx, dy + 70, 0xffffffff);
//		canvas.drawString(sx+","+sy, dx, dy + 90, 0xffffffff);
//		if(tile!=null) {
//			canvas.drawString(tile.getXIndex()+","+tile.getYIndex(), dx, dy + 110, 0xffffffff);	
//		}
	}
}

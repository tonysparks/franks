/*
 * see license.txt 
 */
package franks.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import franks.game.TextureCache;
import franks.game.World;
import franks.gfx.AnimatedImage;
import franks.gfx.AnimationFrame;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.gfx.FramedAnimation;
import franks.gfx.Model;
import franks.gfx.Renderable;
import franks.gfx.TextureUtil;
import franks.map.MapObjectData.FrameData;
import franks.map.MapObjectData.SectionData;
import franks.math.Rectangle;
import franks.math.Vector2f;
import franks.util.TimeStep;

/**
 * @author Tony
 *
 */
public class MapObject implements Renderable {

	private Vector2f pos;
	private Vector2f renderPos;
	private Vector2f tilePos;
	private Model model;
	private World world;
	private Rectangle bounds;
	
	private boolean renderOver;
	
	/**
	 * 
	 */
	public MapObject(World world, TextureCache textureCache, Vector2f pos, MapObjectData data) {
		this.world = world;
		
		this.pos = pos;
		this.bounds = new Rectangle(data.width, data.height);
		this.bounds.setLocation(pos);
		
		this.renderPos = new Vector2f();
		this.tilePos = new Vector2f();
		
		if(data.graphics.section != null) {
			SectionData section = data.graphics.section;
			TextureRegion tex = textureCache.getTexture(section.filePath);
			tex = TextureUtil.subImage(tex, section.x, section.y, section.getWidth(tex), section.getHeight(tex));
			TextureRegion[] frames = TextureUtil.splitImageRegion(tex, section.rows, section.cols);
			AnimationFrame[] animations = new AnimationFrame[frames.length];
			for(int i = 0; i < animations.length; i++) {
				frames[i].flip(section.flipX, section.flipY);
				animations[i] = new AnimationFrame(section.frameTime, i);
			}
			
			AnimatedImage image = new AnimatedImage(frames, new FramedAnimation(animations));
			this.model = new Model(image, section.offsetX, section.offsetY);
			this.model.loop(data.graphics.section.loop);
			
		}
		else if(data.graphics.framed != null) {
			AnimationFrame[] animations = new AnimationFrame[data.graphics.framed.frames.size()];
			TextureRegion[] frames = new TextureRegion[animations.length];
			
			int i = 0;
			for(FrameData frame : data.graphics.framed.frames) {
				
				TextureRegion tex = frame.getMask() != 0 ? textureCache.getTexture(frame.filePath, frame.getMask()) :
													       textureCache.getTexture(frame.filePath);
				
				tex = TextureUtil.subImage(tex, frame.x, frame.y, frame.getWidth(tex), frame.getHeight(tex));
				tex.flip(frame.flipX, frame.flipY);
				
				frames[i] = tex;				
				animations[i] = new AnimationFrame(frame.frameTime, i);
				
				i++;
			}
			
			AnimatedImage image = new AnimatedImage(frames, new FramedAnimation(animations));
			this.model = new Model(image, data.graphics.framed.offsetX, data.graphics.framed.offsetY);
			this.model.loop(data.graphics.framed.loop);
		}
		
		this.renderOver = data.renderOver;
		
//		this.model = new 
	}
	
	public boolean renderOverEntities() {
		return this.renderOver;
	}
	
	public Vector2f getTilePos() {		
		float tileX = (pos.x / world.getRegionWidth());
		float tileY = (pos.y / world.getRegionHeight());
		tilePos.set(tileX, tileY);
		return tilePos;
	}
	
	public Vector2f getRenderPosition(Camera camera, float alpha) {
		Vector2f cameraPos = camera.getRenderPosition(alpha);
				
		Vector2f tilePos = getTilePos();
		
		world.getScreenPosByMapTileIndex(tilePos, renderPos);		
		Vector2f.Vector2fSubtract(renderPos, cameraPos, renderPos);
		
//		renderPos.x -= 32;
//		renderPos.y -= 32;
		
		return renderPos;
	}

	/* (non-Javadoc)
	 * @see franks.gfx.Renderable#update(franks.util.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		this.model.update(timeStep);
	}
	
	/* (non-Javadoc)
	 * @see franks.gfx.Renderable#render(franks.gfx.Canvas, franks.gfx.Camera, float)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {
		Rectangle cameraViewPort = camera.getWorldViewPort();
		//if(cameraViewPort.intersects(bounds)) 
		{
		
			TextureRegion tex = model.animations.getCurrentImage();
			float dx = -1;
			float dy = -1;
			
			Vector2f renderPos = getRenderPosition(camera, alpha);
			dx = renderPos.x + model.offsetX;
			dy = renderPos.y + model.offsetY;
			
			canvas.drawScaledImage(tex, dx, dy, bounds.width, bounds.height, 0xffffffff);
		}
	}
}

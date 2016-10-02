/*
 * see license.txt 
 */
package franks.gfx;

import java.lang.reflect.Field;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import franks.util.Cons;

/**
 * Art assets
 * 
 * 
 * @author Tony
 *
 */
public class Art {
	/**
	 * Simple black image used to overlay
	 */
	public static final TextureRegion BLACK_IMAGE = new TextureRegion();
	
	
	/*----------------------------------------------------------------
	 * Art for the Game
     *----------------------------------------------------------------*/
	 
	
	public static  TextureRegion normalCursorImg = null;
	public static  TextureRegion attackCursorImg = null;		

	public static TextureRegion blackedOutTile = null;
	public static TextureRegion fadedOutTile = null;
	
	/**
	 * Reloads the graphics
	 */
	public static void reload() {
		destroy();
		load();
	}
	
	
	/**
	 * Loads the graphics
	 */
	public static void load() {		
		{
			Pixmap map = TextureUtil.createPixmap(128, 128);
			map.setColor(Color.BLACK);
			map.fillRectangle(0, 0, map.getWidth(), map.getHeight());
			BLACK_IMAGE.setTexture(new Texture(map));
		}
		
		normalCursorImg = loadImage("./assets/gfx/cursors/default_cursor.png");
		attackCursorImg = loadImage("./assets/gfx/cursors/attack_cursor.png");
		
		TextureRegion tex = loadImage("./assets/gfx/tiles/fog_tiles.png");
		TextureRegion tiles[] = TextureUtil.splitImage(tex, 1, 2);
		blackedOutTile = tiles[0];
		fadedOutTile = tiles[1];		
	}

	
	/**
	 * Releases all the textures
	 */
	public static void destroy() {
		try {
			
			/*
			 * Iterate through all of the static fields,
			 * free the Sprite's, TextureRegion's, and Model's
			 */			
			Field[] fields = Art.class.getFields();
			for(Field field : fields) {
				field.setAccessible(true);
				
				Class<?> type = field.getType();
				Object value = field.get(null);
				
				if(value != null) {
					if(type.equals(Sprite.class)) {
						free( (Sprite)value );
					}
					else if(type.equals(TextureRegion.class)) {
						free( (TextureRegion)value );
					}
					else if(type.equals(TextureRegion[].class)) {
						free( (TextureRegion[])value );
					}
					else if(type.equals(Model.class)) {
						free( (Model)value );
					}
				}
			}
		}
		catch(Exception e) {
			Cons.println("Problem freeing the textures: " + e);
		}
	}
	
	
	/**
	 * Frees the texture
	 * 
	 * @param region
	 */
	public static void free(TextureRegion region) {
		if(region != null) {
			region.getTexture().dispose();
		}
	}
	
	
	/**
	 * Frees the textures
	 * 
	 * @param region
	 */
	public static void free(TextureRegion[] region) {
		if(region != null) {
			for(TextureRegion r : region)
				free(r);
		}
	}
	
	
	/**
	 * Frees the memory associated with the model
	 * 
	 * @param model
	 */
	public static void free(Model model) {
		if(model!=null) {
			model.destroy();
		}
	}
	
	
	
	/**
	 * Loads an image from the file system
	 * 
	 * @param image
	 * @return the texture
	 */
	public static TextureRegion loadImage(String image) {
		try {
			return TextureUtil.loadImage(image);
		} 
		catch (Exception e) {
			Cons.println("*** A problem occured loading an image: " + e);
		}
		
		return new TextureRegion(TextureUtil.createImage(10, 10));
	}
	
	
	/**
	 * Loads a pixel map from the file system.
	 * 
	 * @param image
	 * @return the Pixmap
	 */
	public static Pixmap loadPixmap(String image) {
		try {
			return TextureUtil.loadPixmap(image);
		} 
		catch (Exception e) {
			Cons.println("*** A problem occured loading an image: " + e);
		}
		
		return new Pixmap(10, 10, Format.RGBA8888);
	}
	
	
	
	/**
	 * Loads the image from the file system, with a supplied color mask
	 * @param image
	 * @param mask the color to change to transparent
	 * @return the texture
	 */
	public static TextureRegion loadImage(String image, int mask) {
		try {
			Pixmap map = TextureUtil.loadPixmap(image);
			TextureRegion region = TextureUtil.tex(TextureUtil.applyMask(map, new Color( (mask<<8) | 0xff)));
			map.dispose();
			
			return region;
		} 
		catch (Exception e) {
			Cons.println("*** A problem occured loading an image: " + e);
		}
		
		return new TextureRegion(TextureUtil.createImage(10, 10));
	}
	
	
	/**
	 * Model consists of multiple frames
	 * 
	 * @author Tony
	 *
	 */
	public static class Model {
		private TextureRegion[] frames;
		public Model(TextureRegion image, int width, int height, int row, int col) {			
			this.frames = TextureUtil.splitImage(image, width,  height, row, col);
		}
		
		/**
		 * @return the frames
		 */
		public TextureRegion[] getFrames() {
			return frames;
		}
		
		/**
		 * @param i
		 * @return the frame at the supplied index
		 */
		public TextureRegion getFrame(int i) {
			return frames[i];
		}
		
		
		/**
		 * Destroys all images 
		 */
		public void destroy() {
			if(frames!=null) {
				for(int i = 0; i < frames.length;i++) {
					frames[i].getTexture().dispose();
				}
			}
		}
	}
	
		
	/**
	 * Creates a new {@link Animation}
	 * @param obj
	 * @return
	 */
	public static Animation newAnimation(int[] frameTimes) {
		
		AnimationFrame[] frames = new AnimationFrame[frameTimes.length];
		
		int frameNumber = 0;
		for(; frameNumber < frameTimes.length; frameNumber++) {
			frames[frameNumber] = new AnimationFrame(frameTimes[frameNumber], frameNumber);
		}

		Animation animation = new FramedAnimation(frames);
		return animation;
	}
	
	public static AnimatedImage newAnimatedImage(int[] frameTimes, TextureRegion[] frames) {		
		Animation animation = newAnimation(frameTimes);						
		return new AnimatedImage(frames, animation);
	}

	public static AnimatedImage newAnimatedSplitImage(int[] frameTimes, TextureRegion image, int row, int col) {				
		Animation animation = newAnimation(frameTimes);		
		TextureRegion[] images = TextureUtil.splitImage(image, row, col);
		
		return new AnimatedImage(images, animation);
	}
	
	public static AnimatedImage newAnimatedSplitImage(int[] frameTimes, Pixmap image, int row, int col, Integer mask) {		
		if(mask!=null) {
			Pixmap oldimage = image;
			image = TextureUtil.applyMask(image, new Color(mask));
			oldimage.dispose();
		}
		
		Animation animation = newAnimation(frameTimes);		
		TextureRegion[] images = TextureUtil.splitImage(TextureUtil.tex(image), row, col);
		
		return new AnimatedImage(images, animation);
	}
	
	
}

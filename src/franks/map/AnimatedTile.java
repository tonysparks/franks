/*
 * see license.txt 
 */
package franks.map;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import franks.gfx.AnimatedImage;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.util.TimeStep;

/**
 * An animated tile
 * 
 * @author Tony
 *
 */
public class AnimatedTile extends ImageTile {

    private AnimatedImage image;
    private Sprite sprite;
    /**
     * @param image
     * @param width
     * @param height
     */
    public AnimatedTile(AnimatedImage image, int layer, int width, int height) {
        super(null, layer, width, height);
        this.image = image;
        this.image.loop(true);
        
        this.sprite = new Sprite(image.getCurrentImage());
    }
    
    /**
     * @return the animated image
     */
    public AnimatedImage getAnimatedImage() {
        return image;
    }
    
    /* (non-Javadoc)
     * @see seventh.map.Tile#update(seventh.shared.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {
        image.update(timeStep);
    }

    /* (non-Javadoc)
     * @see seventh.map.Tile#render(seventh.client.gfx.Canvas, seventh.client.gfx.Camera, long)
     */
    @Override
    public void render(Canvas canvas, Camera camera, float alpha) {    
        TextureRegion tex = image.getCurrentImage();
        this.sprite.setRegion(tex);
        this.sprite.setPosition(getRenderX(), getRenderY());
        this.sprite.setSize(getWidth(), getHeight());
        this.sprite.setColor(0.0f, 0.5f, 0.5f, 1f);
        canvas.drawRawSprite(sprite);
        
        //0xFFFFFF00
        //canvas.drawScaledImage(tex, getRenderX(), getRenderY(), getWidth(), getHeight(), null);
//        canvas.drawImage(tex, getRenderX(), getRenderY(), 0xFFFFFFFF);
    }
}

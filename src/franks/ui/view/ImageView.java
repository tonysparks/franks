/*
 * see license.txt 
 */
package franks.ui.view;

import com.badlogic.gdx.graphics.g2d.Sprite;

import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.gfx.Renderable;
import franks.util.TimeStep;

/**
 * @author Tony
 *
 */
public class ImageView implements Renderable {

    private Sprite sprite;

    public ImageView(Sprite sprite) {
        this.sprite = sprite;
    }

    @Override
    public void update(TimeStep timeStep) {
    }

    @Override
    public void render(Canvas canvas, Camera camera, float alpha) {
        canvas.drawRawSprite(sprite);
    }

}

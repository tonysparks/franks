/*
 * see license.txt 
 */
package franks.ui.view;

import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.gfx.Colors;
import franks.gfx.Renderable;
import franks.math.Rectangle;
import franks.ui.Button;
import franks.ui.Slider;
import franks.util.TimeStep;

/**
 * @author Tony
 *
 */
public class SliderView implements Renderable {

    private ButtonView handleView;
    private Slider slider;
    
    /**
     * 
     */
    public SliderView(Slider slider) {
        this.slider = slider;
        this.handleView = new ButtonView(slider.getHandle());
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Renderable#update(seventh.shared.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {
        this.handleView.update(timeStep);
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Renderable#render(seventh.client.gfx.Canvas, seventh.client.gfx.Camera, long)
     */
    @Override
    public void render(Canvas canvas, Camera camera, float alpha) {
        Rectangle bounds = slider.getBounds();        
        canvas.fillRect(bounds.x, bounds.y, bounds.width, bounds.height, 0xff383e18);
        canvas.drawRect(bounds.x, bounds.y, bounds.width, bounds.height, 0xff000000);
//        if(slider.isHovering()) {
//            canvas.drawRect(bounds.x, bounds.y, bounds.width, bounds.height, Colors.setAlpha(slider.getForegroundColor(), 100));    
//        }
        
        this.handleView.render(canvas, camera, alpha);
        Button handle = slider.getHandle();
        bounds = handle.getScreenBounds();
        canvas.fillRect(bounds.x, bounds.y, bounds.width, bounds.height, 0xff383e18);
        
        int a = 100;
        if(handle.isHovering()) {
            a = 240;
        }
        
        canvas.fillRect(bounds.x, bounds.y, bounds.width, bounds.height, Colors.setAlpha(slider.getForegroundColor(), a));
        canvas.drawRect(bounds.x, bounds.y, bounds.width, bounds.height, 0xff000000);
        
    }

}

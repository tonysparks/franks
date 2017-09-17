/*
 * see license.txt 
 */
package franks.gfx;

import franks.util.TimeStep;
import franks.util.Updatable;

/**
 * @author Tony
 *
 */
public class Model implements Updatable {

    public AnimatedImage animations;
    public float offsetX;
    public float offsetY;

    public Model(AnimatedImage animations, float offsetX, float offsetY) {
        this.animations = animations;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
    
    public Model loop(boolean loop) {
        this.animations.loop(loop);
        return this;
    }
    
    @Override
    public void update(TimeStep timeStep) {
        this.animations.update(timeStep);        
    }

}

/*
 * see license.txt 
 */
package franks.game.entity;

import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;


/**
 * Defines the graphics of an {@link Entity}
 * 
 * @author Tony
 *
 */
public class GraphicData {
    
    public static class SectionData {
        public String filePath;
        public int x;
        public int y;
        
        public int width;
        public int height;
        
        public int numberOfFrames;
        public int frameTime;
        
        public boolean flipX;
        public boolean flipY;
        
        public float offsetX;
        public float offsetY;
        
        public boolean loop=true;
        
        public Direction[] directions;
        
        public int getWidth(TextureRegion tex) {
            if(width <= 0) {
                return tex.getRegionWidth();
            }
            return width;
        }
        
        public int getHeight(TextureRegion tex) {
            if(height <= 0) {
                return tex.getRegionHeight();
            }
            return height;
        }
    }
    
    /**
     * Animation frame data
     * 
     * @author Tony
     *
     */
    public static class FrameData {
        public String filePath;
        public int x;
        public int y;
        
        public int width;
        public int height;
        public int frameTime;
        
        public boolean flipX;
        public boolean flipY;
        
        public int getWidth(TextureRegion tex) {
            if(width <= 0) {
                return tex.getRegionWidth();
            }
            return width;
        }
        
        public int getHeight(TextureRegion tex) {
            if(height <= 0) {
                return tex.getRegionHeight();
            }
            return height;
        }
    }
    
    public static class StateData {
        public EntityState entityState;
        public Map<Direction, List<FrameData>> animation;
    }
    
    public static class HudDisplayImage {
        public String filePath;
        public int x;
        public int y;
        
        public int width;
        public int height;
                
        public boolean flipX;
        public boolean flipY;        
    }
    
    public HudDisplayImage hudDisplay;
    
    public Map<EntityState, StateData> entityStates;
    public Map<EntityState, SectionData> sectionStates;
}
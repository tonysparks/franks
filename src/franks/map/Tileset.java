/*
 *    leola-live 
 *  see license.txt
 */
package franks.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import franks.gfx.AnimatedImage;
import franks.gfx.Art;
import franks.gfx.TextureUtil;
import franks.map.MapTile.SurfaceType;

/**
 * @author Tony
 *
 */
public class Tileset {

    private TextureRegion[] image;
    private int startId;
    private java.util.Map<String, JsonElement> props;
    
    public Tileset(int startId, TextureRegion[] image, java.util.Map<String, JsonElement> props) {
        this.startId = startId;
        this.image = image;
        this.props = props;
    }
    
    /**
     * @return the startId
     */
    public int getStartId() {
        return startId;
    }
    
    /**
     * @param tileid
     * @return the {@link SurfaceType}
     */
    public SurfaceType getSurfaceType(int tileid) {
        if(props != null) {            
            String id = Integer.toString(toIndex(tileid));        
            JsonElement p = props.get(id);
            if(p != null && p.isJsonObject()) {
                JsonElement e = p.getAsJsonObject().get("surface");
                if(e!=null) {
                    String s = e.getAsString();
                    if(s!=null) {
                        return SurfaceType.fromString(s.toString());
                    }
                }
            }
        }
        return SurfaceType.UNKNOWN;
    }
    
    /**
     * @param tileid
     * @return true if this tile id is an animation
     */
    public boolean isAnimatedImage(int tileid) {
        if(props != null) {
            String id = Integer.toString(toIndex(tileid));
            JsonElement p = props.get(id);
            if(p!=null && p.isJsonObject()) {
                JsonElement animation = p.getAsJsonObject().get("animation");
                if(animation != null) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * @param tileid
     * @return the {@link AnimatedImage} for this tile id
     */
    public AnimatedImage getAnimatedImage(int tileid) {
        if(props != null) {
            String id = Integer.toString(toIndex(tileid));            
            JsonElement prop = props.get(id);
            if(prop != null && prop.isJsonObject()) {
                JsonObject p = prop.getAsJsonObject();
                JsonElement animation = p.get("animation");
                if(animation != null) {
                    TextureRegion tex = Art.loadImage(animation.toString());
                    int rowNum = tex.getRegionHeight() / 32;
                    int colNum = tex.getRegionWidth() / 32;
                    
                    JsonElement rows = p.get("rows");
                    JsonElement cols = p.get("cols");
                    
                    if(rows!=null && rows.isJsonPrimitive()) {
                        rowNum = rows.getAsInt();
                    }
                    
                    if(cols !=null && cols.isJsonPrimitive()) {
                        colNum = cols.getAsInt();
                    }
                    
                    int frameTime = 800;
                    JsonElement fps = p.get("fps");
                    if(fps!=null && fps.isJsonPrimitive()) {
                        frameTime = fps.getAsInt();
                    }
                    
                    int numberOfFrames = rowNum * colNum;
                    int[] frames = new int[numberOfFrames];
                    for(int i = 0; i < numberOfFrames; i++) {
                        frames[i] = frameTime;
                    }
                    
                    return new AnimatedImage(TextureUtil.splitImage(tex, rowNum, colNum), Art.newAnimation(frames));
                }
            }
        }
        
        return null;
    }
    
    private int toIndex(int id) {
        return id - startId;
    }
    
    /**
     * @param id
     * @return
     */
    public TextureRegion getTile(int id) {
        int index = toIndex(id);
        if ( index < 0 || index >= image.length ) {
            return null;
        }
        
        return image[index];
    }
    
    public Integer getTileId(int id) {
        int index = toIndex(id);        
        return index + 1;
    }
}

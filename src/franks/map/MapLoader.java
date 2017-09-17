/*
 *    leola-live 
 *  see license.txt
 */
package franks.map;

import java.util.List;

import com.google.gson.JsonElement;

/**
 * @author Tony
 *
 */
public interface MapLoader {

    public static class TiledTilesetData {
        public int columns;
        public int firstgid;
        public String image;
        public int imageheight;
        public int imagewidth;
        public int margin;
        public String name;
        public int spacing;
        public int tilecount;
        public int tileheight;
        public int tilewidth;
        
        public java.util.Map<String, JsonElement> tileproperties;
        
    }
    
    public static class TiledLayer {
        public int[] data;
        public int height;
        public String name;
        public float opacity;
        public String type;
        public boolean visible;
        public int width;
        public int x;
        public int y;
        
        public java.util.Map<String, JsonElement> properties;
    }
    
    public static class TiledData {
        public int width;
        public int height;
        public int tilewidth;
        public int tileheight;
        public int nextobjectid;
        public String orientation;
        public String renderorder;
        public int version;
        
        public List<TiledLayer> layers;
        public List<TiledTilesetData> tilesets;
    }
    
    /**
     * Loads a {@link Map}
     * 
     * @param map
     * @param loadAssets
     * @return
     * @throws Exception
     */
    public Map loadMap(TiledData map, boolean loadAssets) throws Exception;
}

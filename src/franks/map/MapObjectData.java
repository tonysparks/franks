/*
 * see license.txt 
 */
package franks.map;

import java.util.List;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import franks.math.Vector2f;

/**
 * @author Tony
 *
 */
public class MapObjectData {
	
	public static class FrameData {
		public String filePath;
		public JsonElement mask;
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
		
		public int getMask() {
			if(mask!=null) {
				if(mask.isJsonPrimitive()) {
					JsonPrimitive p = mask.getAsJsonPrimitive();
					if(p.isString()) {
						return Integer.parseInt(p.getAsString(), 16);
					}
					return p.getAsInt();
				}
			}
			return 0;
		}
	}
	
	public static class FramedData {
		public List<FrameData> frames;
		public float offsetX;
		public float offsetY;
		public boolean loop=true;
	}
	
	public static class SectionData {
		public String filePath;
		public int x;
		public int y;
		
		public int width;
		public int height;
		
		public int rows;
		public int cols;
		
		public int frameTime;
		
		public boolean flipX;
		public boolean flipY;
		
		public float offsetX;
		public float offsetY;
		
		public boolean loop=true;
				
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
	
	public static class GraphicData {		
		SectionData section;
		FramedData framed;
	}
	
	public int width;
	public int height;
	public boolean renderOver;
	public GraphicData graphics;
	
	public List<Vector2f> locations;
}

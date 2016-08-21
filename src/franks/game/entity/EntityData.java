/*
 * see license.txt 
 */
package franks.game.entity;

import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import franks.game.entity.Entity.Direction;

/**
 * @author Tony
 *
 */
public class EntityData {

	public static class FrameData {
		public String filePath;
		public int x;
		public int y;
		
		public int width;
		public int height;
		public int frameTime;
		
		public boolean flipX;
		public boolean flipY;
	}
	
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
	
	public static class StateData {
		public Entity.State state;
		public Map<Entity.Direction, List<FrameData>> animation;
	}
	
	public static class GraphicData {
		public Map<Entity.State, StateData> states;
		public Map<Entity.State, SectionData> sectionStates;
	}
	
	public static class ActionData {
		public String action;
		public Map<String, Object> params;
	
		public Double getNumber(String key, Double defaultValue) {
			return EntityData.getNumber(params, key, defaultValue);
		}
		
		public String getStr(String key, String defaultValue) {
			return EntityData.getStr(params, key, defaultValue);
		}

	}
	
	public Entity.Type type;
	public String name;
	public Map<String, Object> attributes;
	public int width, height;
	
	public List<ActionData> availableActions;
	
	
	public GraphicData graphics;
	
	
	public EntityData() {
	}
	
	public Double getNumber(String key, Double defaultValue) {
		return EntityData.getNumber(attributes, key, defaultValue);
	}
	
	public String getStr(String key, String defaultValue) {
		return EntityData.getStr(attributes, key, defaultValue);
	}
	
	public static Double getNumber(Map<String, Object> params, String key, Double defaultValue) {
		if(params.containsKey(key)) {
			Object v = params.get(key);
			if(v instanceof Double) {
				return (Double)v;
			}
		}
		return defaultValue;
	}
	
	public static String getStr(Map<String, Object> params, String key, String defaultValue) {
		if(params.containsKey(key)) {
			return params.get(key).toString();
		}
		return defaultValue;
	}

}

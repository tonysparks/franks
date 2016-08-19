/*
 * see license.txt 
 */
package franks.game.entity;

import java.util.List;
import java.util.Map;

import franks.game.entity.Entity.Direction;
import franks.math.Vector2f;

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
		
		public Direction[] directions;
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
	}
	
	public Entity.Type type;
	public Map<String, Object> attributes;
	public int width, height;
	
	public List<ActionData> availableActions;
	
	
	public GraphicData graphics;
	
	
	public EntityData() {
	}

}

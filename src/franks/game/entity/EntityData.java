/*
 * see license.txt 
 */
package franks.game.entity;

import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import franks.game.entity.Entity.State;

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
		public Map<Direction, List<FrameData>> animation;
	}
	
	public static class GraphicData {
		public Map<Entity.State, StateData> states;
		public Map<Entity.State, SectionData> sectionStates;
	}
	
	public static class AttackActionData {
		public int cost;
		public int hitPercentage;
		public int attackRange;
	}
	
	public static class MoveActionData {
		public int cost;
		public int movementSpeed;		
	}
	
	public static class DieActionData {		
	}
	
	public static class DefenseData {
		public int defensePercentage;
		public int groupBonusPercentage;
	}
	
	public Entity.Type type;
	public String name;	
	public Map<String, EntityAttribute> attributes;
	public int width, height;
	public int movements;		
	public int visibilityRange;
	
	public DefenseData defense;
	
	public AttackActionData attackAction;
	public MoveActionData moveAction;
	public DieActionData dieAction;

	public String dataFile;
	
	public GraphicData graphics;
	
	
	public EntityData() {
	}
	
	
	public EntityAttribute getAttribute(String name) {
		if(this.attributes == null) {
			return null;
		}
		EntityAttribute att = this.attributes.get(name);
		if(att!=null) {
			att.setName(name);
		}
		
		return att;
	}
	
	public long getAnimationTime(State state) {
		if( graphics.sectionStates != null) {
			SectionData states = graphics.sectionStates.get(state);
			if(states!=null) {
				return states.frameTime * states.numberOfFrames;
			}
		}
		
		if(graphics.states != null) {
			StateData stateData = graphics.states.get(state);
			if (stateData!=null) {
				List<FrameData> frames = stateData.animation.get(Direction.SOUTH);
				if(frames != null) {
					long animationTime = 0;
					for(FrameData frame : frames) {
						animationTime += frame.frameTime;
					}
					return animationTime;
				}
			}
		}
		
		return 0;
	}

}

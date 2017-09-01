/*
 * see license.txt 
 */
package franks.game.entity;

import java.util.HashMap;
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
	
	public static class BuildActionData {
	    public int cost;
	    public int numberOfTurns;
	}
	
	public Entity.Type type;
	public String name;	
	public Map<String, EntityAttribute> attributes;
	public int width, height;
		
	public DefenseData defense;
	
	public AttackActionData attackAction;
	public MoveActionData moveAction;
	public DieActionData dieAction;
	public BuildActionData buildAction;
	
	public String dataFile;
	
	public GraphicData graphics;
	
	
	public EntityData() {
	}
	
	public EntityData clone() {
		EntityData clone = new EntityData();
		clone.type = this.type;
		clone.name = this.name;
		clone.attributes = attributes();
		clone.width = this.width;
		clone.height = this.height;
		clone.defense = this.defense;
		clone.attackAction = this.attackAction;
		clone.moveAction = this.moveAction;
		clone.dieAction = this.dieAction;
		clone.dataFile = this.dataFile;
		clone.graphics = this.graphics;
		
		return clone;
	}
	
	public void postBattle(boolean isVictorious) {
		if(this.attributes!=null) {
			this.attributes.forEach( (k,v) -> v.postBattle(isVictorious));
		}
	}
	
	/**
	 * Clones the attributes so that it can be used by another {@link Entity}
	 * @return the cloned attributes
	 */
	private Map<String, EntityAttribute> attributes() {
		Map<String, EntityAttribute> cloned = new HashMap<>();
		if(attributes!=null) {
			attributes.forEach( (k,v) -> {
				cloned.put(k, v.clone());
			});
		}
		
		return cloned;
	}
	
	public EntityAttribute getActionPoints() {
		EntityAttribute attr = getAttribute("actionPoints");
		if(attr==null) {
			return new EntityAttribute("actionPoints", 0, 0);
		}
		return attr;
	}
	
	public EntityAttribute getHealth() {
		EntityAttribute attr = getAttribute("health");
		if(attr==null) {
			return new EntityAttribute("health", 1, 5);
		}
		return attr;
	}
	
	public EntityAttribute getVisibilityRange() {
		EntityAttribute attr = getAttribute("visibilityRange");
		if(attr==null) {
			return new EntityAttribute("visibilityRange", 8, 8);
		}
		return attr;
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

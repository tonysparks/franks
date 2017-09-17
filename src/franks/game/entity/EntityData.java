/*
 * see license.txt 
 */
package franks.game.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import franks.game.actions.ActionType;
import franks.game.entity.GraphicData.FrameData;
import franks.game.entity.GraphicData.SectionData;
import franks.game.entity.GraphicData.StateData;

/**
 * Defines {@link Entity} properties
 * 
 * @author Tony
 *
 */
public class EntityData {


    public static class AttackActionData {
        public int actionPoints;
        public int hitPercentage;
        public int attackRange;
    }
    
    public static class MoveActionData {
        public int actionPoints;
        public int movementSpeed;        
    }
    
    public static class DieActionData {                
    }
    
    public static class DefenseData {
        public int defensePercentage;
        public int groupBonusPercentage;
    }
    
    public static class BuildData {
        
        public ActionType actionType;
        public String entityType;
        public String displayName;
        
        public int actionPoints;
        public int numberOfTurns;
        
        public ResourceData resources;        
    }
    
    public static class BuildActionData {
        public List<BuildData> availableBuildings;
    }
    
    public static class CreateUnitActionData {
        public List<BuildData> availableUnits;
    }
    
    public static class ResourceData {
        public int gold;
        public int food;
        public int material;
    }
    
    public EntityType entityType;
    public String name;    
    public Map<String, EntityAttribute> attributes;
    public int width, height;
        
    public DefenseData defense;
    
    public AttackActionData attackAction;
    public MoveActionData moveAction;
    public DieActionData dieAction;
    public BuildActionData buildAction;
    public CreateUnitActionData createUnitAction;
        
    public String dataFile;
    
    public GraphicData graphics;
    
    public ResourceData startingResources;
    
    public EntityData() {
    }
    
    public EntityData clone() {
        EntityData clone = new EntityData();
        clone.entityType = this.entityType;
        clone.name = this.name;
        clone.attributes = attributes();
        clone.width = this.width;
        clone.height = this.height;
        clone.defense = this.defense;
        clone.startingResources = this.startingResources;
        
        clone.attackAction = this.attackAction;
        clone.moveAction = this.moveAction;
        clone.dieAction = this.dieAction;
        clone.buildAction = this.buildAction;
        clone.createUnitAction = this.createUnitAction;
        
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
    
    public long getAnimationTime(EntityState entityState) {
        if( graphics.sectionStates != null) {
            SectionData states = graphics.sectionStates.get(entityState);
            if(states!=null) {
                return states.frameTime * states.numberOfFrames;
            }
        }
        
        if(graphics.entityStates != null) {
            StateData stateData = graphics.entityStates.get(entityState);
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

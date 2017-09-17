/*
 * see license.txt 
 */
package franks.game.actions;


/**
 * EntityType of action
 * 
 * @author Tony
 *
 */
public enum ActionType {
    Move,
    Attack,
    Die,
    
    BuildTownCenter,
    BuildRoad,
        
    CreateDwarf,
    CreateArcher,
    CreateKnight,
    CreateGeneral,
    CreateWorker,
    
    ;        
    
    /**
     * Determines if this Action is building
     * 
     * 
     * @return true if this is a building action
     */
    public boolean isBuildAction() {
        return this.name().toLowerCase().startsWith("build");
    }

    public boolean isCreateAction() {
        return this.name().toLowerCase().startsWith("create");
    }
}
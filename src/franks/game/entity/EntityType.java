/*
 * see license.txt 
 */
package franks.game.entity;

/**
 * EntityType of entity this is
 * 
 * @author Tony
 *
 */
public enum EntityType {
    /**
     * Army General
     * Leads Units across world map
     */
    GENERAL,
    
    /**
     * Merchant
     * Trades with other Town Centers
     */
    MERCHANT,
    
    /**
     * Worker
     * Builds TownCenters/Roads and
     * acquires Resources
     */
    WORKER,
            
    /**
     * Infantry Battle Unit
     */
    INFANTRY,
            
    
    /**
     * Town Center
     */
    TOWN_CENTER,
    
    ;
    
    public boolean isAttackable() {
        return true;
    }
    
    public boolean isBattleUnit() {
        return !isWorldUnit();
    }
    
    public boolean isWorldUnit() {
        switch(this) {                         
            case INFANTRY:
                return false;
            case GENERAL:
            case MERCHANT:
            case TOWN_CENTER:
            case WORKER:
                return true;
            default:
                return false;
        }
               
    }
}
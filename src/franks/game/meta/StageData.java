/*
 * see license.txt 
 */
package franks.game.meta;

import java.util.List;

import franks.game.entity.EntityGroupData;
import franks.game.entity.EntityGroupData.EntityInstanceData;

/**
 * @author Tony
 *
 */
public class StageData {

    public static class GeneralInstanceData extends EntityInstanceData {
        public EntityGroupData holding;
    }
    
    public static class ArmyData {
        List<GeneralInstanceData> generals;
        List<EntityInstanceData>  workers;
    }
    
    public ArmyData redArmy;
    public ArmyData greenArmy;

}

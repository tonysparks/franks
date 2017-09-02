/*
 * see license.txt 
 */
package franks.game;

/**
 * @author Tony
 *
 */
public class Ids {

    private boolean[] ids;
    
    /**
     * 
     */
    public Ids(int maxIds) {
        this.ids = new boolean[maxIds];
    }

    public boolean validId(int id) {
        if(id>=0 && id < ids.length) {
            return ids[id];
        }
        return false;
    }
    
    public int getNextId() {
        for(int i = 0; i < ids.length; i++) {
            if(!ids[i]) {
                ids[i] = true;
                return i;
            }
        }
        
        throw new IllegalArgumentException("Hit max entity count");
    }
    
    public void reclaimId(int id) {
        if(id>=0 && id < ids.length) {
            ids[id] = false;
        }
    }

}

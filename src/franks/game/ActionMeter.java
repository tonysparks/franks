/*
 * see license.txt 
 */
package franks.game;

/**
 * Meter for managing movement costs
 * 
 * @author Tony
 *
 */
public class ActionMeter {

    private int actionCostAmount;
    
    /**
     * 
     */
    public ActionMeter(int startingAmount) {
        this.actionCostAmount = startingAmount;
    }
    
    public void reset(int amount) {
        this.actionCostAmount = amount;
    }
    
    /**
     * @return the movementAmount
     */
    public int remaining() {
        return actionCostAmount;
    }
    
    public boolean hasEnough(int amount) {
        return actionCostAmount >= amount;
    }

    public int decrementBy(int amountDelta) {
        actionCostAmount -= amountDelta;
        if(actionCostAmount<0) {
            throw new IllegalArgumentException();
        }
        return actionCostAmount;
    }
    
    public int increaseBy(int amountDelta) {
        this.actionCostAmount += amountDelta;
        return this.actionCostAmount;
    }
}

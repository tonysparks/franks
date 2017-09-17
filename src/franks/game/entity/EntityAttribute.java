/*
 * see license.txt 
 */
package franks.game.entity;

/**
 * @author Tony
 *
 */
public class EntityAttribute {

    private String name;
    private int currentValue;
    private int maxValue;
    private int absoluteMax;
    
    private int xpGainFromVictory;
    private int xpGainFromDefeat;
    
    private int numberOfVictoriesBeforeXPGain;
    private int numberOfDefeatsBeforeXPGain;
        
    private int regenVictoryAmount;
    private int regenDefeatAmount;
    
    // transient
    private int numberOfVictories;
    private int numberOfDefeates;
    
    public EntityAttribute() {    
    }

    public EntityAttribute(String name, int currentValue, int maxValue) {
        this.name = name;
        this.currentValue = currentValue;
        this.maxValue = maxValue;
        
        this.numberOfDefeatsBeforeXPGain = 1;
        this.numberOfVictoriesBeforeXPGain = 1;
        this.regenDefeatAmount = currentValue;
        this.regenVictoryAmount = currentValue;
    }
    
    public EntityAttribute clone() {
        EntityAttribute a = new EntityAttribute();
        
        a.absoluteMax = this.absoluteMax;
        a.currentValue = this.currentValue;
        a.maxValue = this.maxValue;
        a.name = this.name;
        //a.numberOfDefeates = this.numberOfDefeates;
        a.numberOfDefeatsBeforeXPGain = this.numberOfDefeatsBeforeXPGain;
        //a.numberOfVictories = this.numberOfVictories;
        a.numberOfVictoriesBeforeXPGain = this.numberOfVictoriesBeforeXPGain;
        a.regenDefeatAmount = this.regenDefeatAmount;
        a.regenVictoryAmount = this.regenVictoryAmount;
        a.xpGainFromDefeat = this.xpGainFromDefeat;
        a.xpGainFromVictory = this.xpGainFromVictory;
        return a;
    }
    
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * @return the currentValue
     */
    public int getCurrentValue() {
        return currentValue;
    }
    
    public int delta(int delta) {
        this.currentValue += delta;
        return this.currentValue;
    }
    
    public void setCurrentValue(int value) {
        this.currentValue = value;
    }
    
    /**
     * @return the maxValue
     */
    public int getMaxValue() {
        return maxValue;
    }
    
    public void postBattle(boolean isVictor) {
        if(isVictor) {
            this.numberOfVictories++;
        }
        else {
            this.numberOfDefeates++;
        }                
        
        if(this.numberOfDefeates > 0) {
            if((this.numberOfDefeatsBeforeXPGain % this.numberOfDefeates) == 0) {
                this.maxValue += this.xpGainFromDefeat;
                if(this.maxValue > this.absoluteMax) {
                    this.maxValue = this.absoluteMax;
                }
            }
        }
        
        if(this.numberOfVictories > 0) {
            if((this.numberOfVictoriesBeforeXPGain % this.numberOfVictories) == 0) {
                this.maxValue += this.xpGainFromVictory;
                if(this.maxValue > this.absoluteMax) {
                    this.maxValue = this.absoluteMax;
                }
            }
        }
        
        this.currentValue += isVictor ? this.regenVictoryAmount : this.regenDefeatAmount;
        if(this.currentValue > this.maxValue) {
            this.currentValue = this.maxValue;
        }
    }
}


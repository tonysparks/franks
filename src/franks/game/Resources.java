/*
 * see license.txt 
 */
package franks.game;

import franks.game.entity.EntityData.ResourceData;

/**
 * Container for managing resources
 * 
 * @author Tony
 *
 */
public class Resources {

    private int gold;
    private int food;
    private int material;
    
    /**
     * @param resources
     */
    public Resources(ResourceData resources) {
        this(resources.gold, resources.food, resources.material);
    }
    
    /**
     * @param gold
     * @param food
     * @param material
     */
    public Resources(int gold, int food, int material) {
        super();
        this.gold = gold;
        this.food = food;
        this.material = material;
    }
    
    public Resources addGold(int delta) {
        this.gold += delta;
        return this;
    }
    
    public Resources addFood(int delta) {
        this.food += delta;
        return this;
    }
    
    public Resources addMaterial(int delta) {
        this.material += delta;
        return this;
    }
    
    public Resources takeGold(int delta) {
        return addGold(-delta);
    }
    
    public Resources takeFood(int delta) {
        return addFood(-delta);
    }
    
    public Resources takeMaterial(int delta) {
        return addMaterial(-delta);
    }
    
    public Resources add(Resources amount) {
        addGold(amount.gold);
        addFood(amount.food);
        addMaterial(amount.material);
        return this;
    }
    
    public Resources sub(Resources amount) {
        takeGold(amount.gold);
        takeFood(amount.food);
        takeMaterial(amount.material);
        return this;
    }
    
    /**
     * Moves the amount of resources over to the dest resources
     * 
     * @param amount
     * @param dest
     * @return true if successful (atomic action)
     */
    public boolean moveTo(Resources amount, Resources dest) {
        if(hasAmount(amount)) {
            sub(amount);
            dest.add(amount);
            return true;
        }
        
        return false;
    }
    
    public boolean hasGoldAmount(int amount) {
        return this.gold >= amount;
    }
    public boolean hasFoodAmount(int amount) {
        return this.food >= amount;
    }
    public boolean hasMaterialAmount(int amount) {
        return this.material >= amount;
    }
    
    /**
     * If there are enough resources to fulfill the {@link Resources}
     * 
     * @param amount
     * @return true if there are enough resources
     */
    public boolean hasAmount(Resources amount) {
        return hasAmount(amount.gold, amount.food, amount.material);
    }
    
    public boolean hasAmount(int gold, int food, int material) {
        return hasGoldAmount(gold) && 
               hasFoodAmount(food) && 
               hasMaterialAmount(material);
    }

    /**
     * @return the gold
     */
    public int getGold() {
        return gold;
    }

    /**
     * @param gold the gold to set
     */
    public void setGold(int gold) {
        this.gold = gold;
    }

    /**
     * @return the food
     */
    public int getFood() {
        return food;
    }

    /**
     * @param food the food to set
     */
    public void setFood(int food) {
        this.food = food;
    }

    /**
     * @return the material
     */
    public int getMaterial() {
        return material;
    }

    /**
     * @param material the material to set
     */
    public void setMaterial(int material) {
        this.material = material;
    }
}

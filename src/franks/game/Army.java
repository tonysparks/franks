/*
 * see license.txt 
 */
package franks.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;

import franks.game.entity.Entity;
import franks.game.net.NetArmy;

/**
 * @author Tony
 *
 */
public class Army {

    public static enum ArmyName {
        Red,
        Green,
        ;
    }
    
    private String name;
    private Player player;
    private List<Entity> leaders;
    private List<Entity> workers;
    
    private NetArmy net;
    private Color color;
    
    /**
     * 
     */
    public Army(String name, Color color) {        
        this.name = name;
        this.color = color;
        
        this.leaders = new ArrayList<>();
        this.workers = new ArrayList<>();
        
        this.net = new NetArmy();
        this.net.name = name;
    }
    
    /**
     * @param player the player to set
     */
    public void setPlayer(Player player) {
        this.player = player;
    }
    
    /**
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }
    
    public int armySize() {
        return this.leaders.size();
    }
    
    /**
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    public void addLeader(Entity entity) {
        this.leaders.add(entity);
    }
    
    public void addWorker(Entity entity) {
        this.workers.add(entity);
    }
    
    public void addLeaders(List<Entity> entities) {
        this.leaders.addAll(entities);
    }
    
    public void addWorkers(List<Entity> entities) {
        this.workers.addAll(entities);
    }
    
    public void removeAllLeaders() {
        this.leaders.clear();
    }
    
    public void removeAllWorkers() {
        this.workers.clear();
    }
    
    public void removeMember(Entity entity) {
        this.leaders.remove(entity);
        this.workers.remove(entity);
    }
    
    public boolean isMember(Entity entity) {
        return entity.getTeam() == this;
    }
    
    /**
     * @return the leaders
     */
    public List<Entity> getLeaders() {
        return leaders;
    }
    
    /**
     * @return the workers
     */
    public List<Entity> getWorkers() {
        return workers;
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    public NetArmy getNetArmy() {
        return net;
    }
}

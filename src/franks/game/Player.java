/*
 * see license.txt 
 */
package franks.game;

import java.util.ArrayList;
import java.util.List;

import franks.game.entity.Entity;
import franks.game.entity.meta.LeaderEntity;
import franks.game.net.NetPlayer;

/**
 * @author Tony
 *
 */
public class Player {

    private String name;
    private Army army;
        
    private NetPlayer net;
    
    public Player(String name) {
        this.name = name;        
        this.net = new NetPlayer();
        this.net.name = name;        
    }
    
    /**
     * @param army the army to set
     */
    public void setTeam(Army army) {
        this.army = army;
        this.army.setPlayer(this);
    }
    
    public boolean isLocalPlayer() {
        return false;
    }
    
    public boolean owns(Entity entity) {
        return this.army.isMember(entity);
    }
    
    /**
     * @param entities the entities to set
     */
    public void setEntities(List<LeaderEntity> entities) {
        this.army.removeAllLeaders();
        this.army.addLeaders(entities);
    }
    
    public void addEntities(List<LeaderEntity> entities) {
        this.army.addLeaders(entities);
    }
    
    public void addEntity(LeaderEntity ent) {
        this.army.addLeader(ent);
    }
        
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * @return the army
     */
    public Army getTeam() {
        return army;
    }

    public NetPlayer getNetPlayer() {
        net.entities = new ArrayList<>();
        for(LeaderEntity ent : this.army.getLeaders()) {
            net.entities.add(ent.getNetLeaderEntity());
        }
        return net;
    }
    
    public void syncFrom(NetPlayer net) {
        name = net.name;        
    }
}

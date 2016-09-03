/*
 * see license.txt 
 */
package franks.game;

import java.util.ArrayList;
import java.util.List;

import franks.game.entity.Entity;
import franks.game.net.NetPlayer;

/**
 * @author Tony
 *
 */
public class Player {

	private String name;
	private Team team;
	private List<Entity> entities;	
	
	private NetPlayer net;
	
	public Player(String name, Team team) {
		this.name = name;
		this.team = team;
		this.net = new NetPlayer();
		this.net.name = name;		
	}
	
	public boolean owns(Entity entity) {
		for(int i = 0; i < entities.size(); i++) {
			Entity ent = entities.get(i);
			if(ent.getId() == entity.getId()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param entities the entities to set
	 */
	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}
	
	public void addEntities(List<Entity> entities) {
		this.entities.addAll(entities);
	}
	
	public void addEntity(Entity ent) {
		this.entities.add(ent);
	}
	
	/**
	 * @return the entities
	 */
	public List<Entity> getEntities() {
		return entities;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the team
	 */
	public Team getTeam() {
		return team;
	}

	public NetPlayer getNetPlayer() {
		net.entities = new ArrayList<>();
		for(Entity ent : entities) {
			net.entities.add(ent.getNetEntity());
		}
		return net;
	}
	
	public void syncFrom(NetPlayer net) {
		name = net.name;		
	}
}

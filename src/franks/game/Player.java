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
		
	private NetPlayer net;
	
	public Player(String name, Team team) {
		this.name = name;
		this.team = team;
		this.net = new NetPlayer();
		this.net.name = name;		
	}
	
	public boolean owns(Entity entity) {
		return this.team.isMember(entity);
	}
	
	/**
	 * @param entities the entities to set
	 */
	public void setEntities(List<Entity> entities) {
		this.team.removeAllMembers();
		this.team.addMembers(entities);
	}
	
	public void addEntities(List<Entity> entities) {
		this.team.addMembers(entities);
	}
	
	public void addEntity(Entity ent) {
		this.team.addMember(ent);
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
		for(Entity ent : this.team.getMembers()) {
			net.entities.add(ent.getNetEntity());
		}
		return net;
	}
	
	public void syncFrom(NetPlayer net) {
		name = net.name;		
	}
}

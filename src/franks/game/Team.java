/*
 * see license.txt 
 */
package franks.game;

import java.util.ArrayList;
import java.util.List;

import franks.game.entity.Entity;

/**
 * @author Tony
 *
 */
public class Team {

	private String name;
	private List<Entity> members;
	
	/**
	 * 
	 */
	public Team(String name) {
		this.name = name;
		this.members = new ArrayList<>();
	}

	public void addMember(Entity entity) {
		this.members.add(entity);
	}
	
	public void removeMember(Entity entity) {
		this.members.remove(entity);
	}
	
	/**
	 * @return the members
	 */
	public List<Entity> getMembers() {
		return members;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}

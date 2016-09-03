/*
 * see license.txt 
 */
package franks.game;

import java.util.ArrayList;
import java.util.List;

import franks.game.entity.Entity;
import franks.game.net.NetTeam;

/**
 * @author Tony
 *
 */
public class Team {

	public static enum TeamName {
		Red,
		Green,
		;
	}
	
	private String name;
	private List<Entity> members;
	private NetTeam net;
	/**
	 * 
	 */
	public Team(String name) {
		this.name = name;
		this.members = new ArrayList<>();
		this.net = new NetTeam();
		this.net.name = name;
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

	public NetTeam getNetTeam() {
		return net;
	}
}

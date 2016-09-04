/*
 * see license.txt 
 */
package franks.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;

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
	private Color color;
	
	/**
	 * 
	 */
	public Team(String name, Color color) {
		this.name = name;
		this.color = color;
		
		this.members = new ArrayList<>();
		this.net = new NetTeam();
		this.net.name = name;
	}
	
	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
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

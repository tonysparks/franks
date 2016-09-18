/*
 * see license.txt 
 */
package franks.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;

import franks.game.entity.Entity;
import franks.game.entity.meta.LeaderEntity;
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
	private List<LeaderEntity> leaders;
	private NetArmy net;
	private Color color;
	
	/**
	 * 
	 */
	public Army(String name, Color color) {		
		this.name = name;
		this.color = color;
		
		this.leaders = new ArrayList<>();
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

	public void addLeader(LeaderEntity entity) {
		this.leaders.add(entity);
	}
	
	public void addLeaders(List<LeaderEntity> entities) {
		this.leaders.addAll(entities);
	}
	
	public void removeAllLeaders() {
		this.leaders.clear();
	}
	
	public void removeMember(Entity entity) {
		this.leaders.remove(entity);
	}
	
	public boolean isMember(Entity entity) {
		return entity.getTeam() == this;
	}
	
	/**
	 * @return the leaders
	 */
	public List<LeaderEntity> getLeaders() {
		return leaders;
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

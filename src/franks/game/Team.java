/*
 * see license.txt 
 */
package franks.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;

import franks.game.entity.Entity;
import franks.game.net.NetTeam;
import franks.math.Rectangle;
import franks.math.Vector2f;

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
	
	public int teamSize() {
		return this.members.size();
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
	
	public void addMembers(List<Entity> entities) {
		this.members.addAll(entities);
	}
	
	public void removeAllMembers() {
		this.members.clear();
	}
	
	public void removeMember(Entity entity) {
		this.members.remove(entity);
	}
	
	public boolean isMember(Entity entity) {
		for(int i = 0; i < members.size(); i++) {
			Entity ent = members.get(i);
			if(ent.getId() == entity.getId()) {
				return true;
			}
		}
		return false;
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
	
	public void shufflePosition(Randomizer rand) {
		int minX = 0;
		int maxX = 0;
		int minY = 0;
		int maxY = 0;
		
		for(Entity ent : this.members) {
			Rectangle bounds = ent.getTileBounds();
			if(bounds.x < minX) {
				minX = bounds.x;
			}
			if(bounds.x > maxX) {
				maxX = bounds.x;
			}
			if(bounds.y < minY) {
				minY = bounds.y;
			}
			if(bounds.y > maxY) {
				maxY = bounds.y;
			}
		}
		
		int size = teamSize();
		for(int i = 0; i < size; i++) {
			int left = rand.nextInt(size);
			int right = rand.nextInt(size);
			if(left!=right) {
				Entity leftEnt = this.members.get(left);
				Entity rightEnt = this.members.get(right);
				Vector2f t = leftEnt.getPos().createClone();
				leftEnt.moveTo(rightEnt.getPos());
				rightEnt.moveTo(t);
			}
		}
	}
}

/*
 * see license.txt 
 */
package newera.game;

import java.util.concurrent.atomic.AtomicInteger;

import newera.game.entity.Entity.Type;

/**
 * @author Tony
 *
 */
public class HealthMeter {

	private int health;
	private int happiness;
	
	/**
	 * 
	 */
	public HealthMeter() {
		this.health = 100;
		this.happiness = 50;
	}
	
	public boolean isDead() {
		return this.health <= 0;
	}
	
	/**
	 * @return the happiness
	 */
	public int getHappiness() {
		return happiness;
	}
	
	/**
	 * @return the health
	 */
	public int getHealth() {
		return health;
	}
	
	public void calculate(Game game) {
		calculateHealth(game);
		calculateHappiness(game);
	}
	
	public void calculateHealth(Game game) {
		AtomicInteger sum = new AtomicInteger(0);
		AtomicInteger count = new AtomicInteger(0);
		game.foreachEntity(ent -> {
			if(ent.getType() == Type.HUMAN) {
				sum.addAndGet(ent.attributeAsInt("health"));
				count.incrementAndGet();
			}
		});
		
		if(count.get() > 0) {
			this.health += sum.get() / count.get();
			
			if(this.happiness>=50) {
				this.health += game.getRandomizer().nextInt(5);
			}
			else {
				this.health -= game.getRandomizer().nextInt(5);
			}
			
		}
		
		this.health = Math.max(0, health);
		this.health = Math.min(health, 100);
	}
	
	public void calculateHappiness(Game game) {
		AtomicInteger sum = new AtomicInteger(0);
		AtomicInteger count = new AtomicInteger(0);
		game.foreachEntity(ent -> {
			if(ent.getType() == Type.HUMAN) {
				sum.addAndGet(ent.attributeAsInt("happiness"));
				count.incrementAndGet();
			}
		});
		
		if(count.get() > 0) {
			this.happiness += sum.get() / count.get();
		}
		
		this.happiness = Math.max(0, this.happiness);
	}

}

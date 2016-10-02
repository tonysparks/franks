/*
 * see license.txt 
 */
package franks.game.ai.evaluators;

import java.util.ArrayList;
import java.util.List;

import franks.game.Game;
import franks.game.Randomizer;
import franks.game.ai.MetaEvaluator;
import franks.game.commands.Command.CommandType;
import franks.game.commands.CommandQueue.CommandRequest;
import franks.game.entity.Entity;
import franks.game.entity.meta.LeaderEntity;
import franks.game.meta.MetaGame;
import franks.map.IsometricMap;
import franks.map.MapTile;
import franks.math.Rectangle;
import franks.math.Vector2f;

/**
 * Determines if it's optimal for a unit to attack
 * 
 * @author Tony
 *
 */
public class MovementMetaEvaluator implements MetaEvaluator {

	private Entity selectedEntity;
	private MapTile destination;
	private List<MapTile> tiles = new ArrayList<>();
		
	@Override
	public double calculateScore(LeaderEntity entity, MetaGame game) {
		double bestScore = 0;
		this.selectedEntity = entity;
		this.destination = null;
				
		List<LeaderEntity> enemies = game.getOtherTeam(entity.getTeam()).getLeaders();
		
		List<MapTile> walkableTiles = getWalkableTiles(entity, game);		
		for(MapTile tile : walkableTiles) {
			double score = scoreTile(tile, game, enemies);
			
			if(score > bestScore) {
				bestScore = score;
				this.destination = tile;
			}
		}
				
		System.out.println("Movement Score: " + bestScore);
		return bestScore;
	}
	
	private List<MapTile> getWalkableTiles(Entity entity, Game game) {
		IsometricMap map = game.getMap();
		
		List<MapTile> walkableTiles = new ArrayList<>();
		
		int availablePoints = entity.getMeter().remaining();
		Vector2f tilePos = new Vector2f();
		Rectangle bounds = new Rectangle(availablePoints*map.getTileWidth(), availablePoints*map.getTileHeight());	
		bounds.centerAround(entity.getScreenPosition());
		map.getTilesInRect(0, bounds, tiles);
		
//		for(int y = 0; y < map.getTileWorldHeight(); y++) {
//			for(int x = 0; x < map.getTileWorldWidth(); x++) {
		//MapTile tile = map.getTile(0, x, y);
				
		for(MapTile tile : tiles) {
			if(tile!=null) {
				if(game.getEntityOnTile(tile) == null) {						
					if(map.getCollidableTile(tile.getXIndex(), tile.getYIndex())==null) {
						
						tilePos.set(tile.getX(), tile.getY());
						
						int movementCost = entity.calculateMovementCost(tilePos);
						if(movementCost > 0 && movementCost <= availablePoints) {							
							walkableTiles.add(tile);
						}
					}
				}
			}
		}
		
		return walkableTiles;
	}
	
	private double scoreTile(MapTile tile, Game game, List<LeaderEntity> enemies) {
		Randomizer rand = game.getRandomizer();
		IsometricMap map = game.getMap();
		
		double bestScore = 0;
		for(Entity enemy : enemies) {
			double score=0;
			if(enemy.isAlive()) {
				int startingPoints = enemy.startingActionPoints();				
				int attackCost = enemy.calculateAttackCost(tile);
				
				int distanceToEnemy = enemy.distanceFrom(tile);
				//System.out.println(distanceToEnemy);
				double distanceBonus = Math.max(map.getTileWorldWidth() - distanceToEnemy, 0) / 100.0;								
				score = distanceBonus * rand.getRandomRange(0.5, 0.75);
				
				if(attackCost > startingPoints) {
					score += rand.getRandomRange(0.2, 0.43);
				}
				else if(attackCost > startingPoints/2) {
					score += rand.getRandomRange(0.05, 0.15);
				}				
			}
			
			if(score > bestScore) {
				bestScore = score;
			}
			
		}
		
		
		return bestScore;
	}
		
	/* (non-Javadoc)
	 * @see franks.game.ai.Evaluator#getCommandRequest(franks.game.Game)
	 */
	@Override
	public CommandRequest getCommandRequest(Game game) {
		return new CommandRequest(game, CommandType.Move, this.selectedEntity, null, new Vector2f(this.destination.getX(), this.destination.getY()));
	}
}

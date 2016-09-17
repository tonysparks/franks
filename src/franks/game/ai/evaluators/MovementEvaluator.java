/*
 * see license.txt 
 */
package franks.game.ai.evaluators;

import java.util.ArrayList;
import java.util.List;

import franks.game.Game;
import franks.game.Randomizer;
import franks.game.ai.BattleEvaluator;
import franks.game.battle.BattleGame;
import franks.game.commands.Command.CommandType;
import franks.game.commands.CommandQueue.CommandRequest;
import franks.game.entity.Direction;
import franks.game.entity.Entity;
import franks.game.entity.EntityList;
import franks.map.IsometricMap;
import franks.map.MapTile;
import franks.math.Vector2f;

/**
 * Determines if it's optimal for a unit to attack
 * 
 * @author Tony
 *
 */
public class MovementEvaluator implements BattleEvaluator {

	private Entity selectedEntity;
	private MapTile destination;
			
	/* (non-Javadoc)
	 * @see franks.game.ai.Evaluator#calculateScore(franks.game.Game)
	 */
	@Override
	public double calculateScore(Entity entity, BattleGame game) {
		double bestScore = 0;
		this.selectedEntity = entity;
		this.destination = null;
				
		EntityList enemies = game.getOtherLeader(entity.getPlayer()).getEntities();
		
		List<MapTile> walkableTiles = getWalkableTiles(entity, game);		
		for(MapTile tile : walkableTiles) {
			double score = scoreTile(tile, game, enemies);
			
			if(score > bestScore) {
				bestScore = score;
				this.destination = tile;
			}
		}
				
		
		return bestScore;
	}
	
	private List<MapTile> getWalkableTiles(Entity entity, Game game) {
		
		List<MapTile> walkableTiles = new ArrayList<>();
		
		int availablePoints = entity.getMeter().remaining();
		Vector2f tilePos = new Vector2f();
		
		IsometricMap map = game.getMap();
		for(int y = 0; y < map.getTileWorldHeight(); y++) {
			for(int x = 0; x < map.getTileWorldWidth(); x++) {
				MapTile tile = map.getTile(0, x, y);
				if(tile!=null) {
					if(game.getEntityOnTile(tile) == null) {
						if(map.getCollidableTile(x, y)==null) {
							
							tilePos.set(tile.getX(), tile.getY());
							
							int movementCost = entity.calculateMovementCost(tilePos);
							if(movementCost > 0 && movementCost <= availablePoints) {							
								walkableTiles.add(tile);
							}
						}
					}
				}
			}
		}
		
		return walkableTiles;
	}
	
	private double scoreTile(MapTile tile, Game game, EntityList enemies) {
		Randomizer rand = game.getRandomizer();
		IsometricMap map = game.getMap();
		
		double bestScore = 0;
		for(Entity enemy : enemies) {
			//Entity enemy = enemies.get(i);
						
			double score=0;
			if(enemy.isAlive()) {
				int startingPoints = enemy.startingActionPoints();
				int defensePercentage = enemy.calculateStrictDefensePercentage();
				int attackCost = enemy.calculateAttackCost(tile);
				
				int distanceToEnemy = enemy.distanceFrom(tile);
				
				double distanceBonus = (100d - (double)defensePercentage) / 100d;
				distanceBonus /= Math.max( (double) distanceToEnemy, 1);
				
				score = distanceBonus * rand.getRandomRange(0.5, 0.75);
				
				if(attackCost > startingPoints) {
					score += rand.getRandomRange(0.1, 0.3);
				}
				else if(attackCost > startingPoints/2) {
					score += rand.getRandomRange(0.05, 0.15);
				}				
			}
			
			score += getNeighborTileScore(map, tile, game, Direction.NORTH);
			score += getNeighborTileScore(map, tile, game, Direction.EAST);
			score += getNeighborTileScore(map, tile, game, Direction.SOUTH);
			score += getNeighborTileScore(map, tile, game, Direction.WEST);			
			
			if(score > bestScore) {
				bestScore = score;
			}
			
		}
		
		
		return bestScore;
	}
	
	private double getNeighborTileScore(IsometricMap map, MapTile tile, Game game, Direction dir) {
		Randomizer rand = game.getRandomizer();
		double score = 0;
		
		int tileX = tile.getX() + dir.getX();
		int tileY = tile.getY() + dir.getY();
		
		if(!map.checkTileBounds(tileX, tileY)) {
			
			MapTile n = map.getTile(0, tileX, tileY);
			if(n!=null) {
				Entity ent = game.getEntityOnTile(n);
				if(ent != null && ent.isTeammate(selectedEntity)) {
					score += rand.getRandomRange(0.25, 0.5);
				}
			}
		}
		
		return score;
	}
	
	/* (non-Javadoc)
	 * @see franks.game.ai.Evaluator#getCommandRequest(franks.game.Game)
	 */
	@Override
	public CommandRequest getCommandRequest(Game game) {
		return new CommandRequest(game, CommandType.Move, this.selectedEntity, null, new Vector2f(this.destination.getX(), this.destination.getY()));
	}
}

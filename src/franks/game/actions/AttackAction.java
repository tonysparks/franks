/*
 * see license.txt 
 */
package franks.game.actions;

import java.util.Optional;

import franks.game.Game;
import franks.game.PreconditionResponse;
import franks.game.Randomizer;
import franks.game.TerrainData.TerrainTileData;
import franks.game.entity.Entity;
import franks.game.entity.EntityData.AttackActionData;
import franks.map.MapTile;

/**
 * @author Tony
 *
 */
public abstract class AttackAction extends Action {

    protected int hitPercentage;
    protected int attackDistance;
    
    protected Game game;
        
    /**
     * @param game
     * @param attacker
     * @param cost
     * @param attackDistance
     * @param hitPercentage
     */
    public AttackAction(Game game, Entity attacker, AttackActionData data) {
        super(ActionType.Attack,  data.cost, attacker);
        
        this.game = game;
        this.attackDistance = data.attackRange;
        this.hitPercentage = data.hitPercentage;
        
    }


    /* (non-Javadoc)
     * @see franks.game.Command#checkPreconditions(franks.game.Game, franks.game.CommandQueue.CommandRequest)
     */
    @Override
    public PreconditionResponse checkPreconditions(Game game, Command command) {
        PreconditionResponse response = new PreconditionResponse();
        
        Entity attacker = getEntity();
        if(!attacker.canDo(getType())) {
            response.addFailure("This entity can not attack");
        }
        
        Optional<Entity> target = command.targetEntity; 
        if(!target.isPresent() || target.get().isDead()) {
            response.addFailure("No enemy target");
        }
        else {
            Entity enemy = target.get();
            
            if(enemy.isTeammate(attacker)) {
                response.addFailure("Can't attack team member");
            }
            
            int numberOfTilesAway = attacker.distanceFrom(enemy);
            if(numberOfTilesAway > attackDistance) {
                response.addFailure("Enemy target is too far away");
            }            
        }
        
        checkCost(response, game);
        return response;
    }

    
    public int calculateCost(MapTile tile) {
        int numberOfTilesAway = getEntity().distanceFrom(tile);
        if(numberOfTilesAway <= attackDistance) {
            return getActionCost();
        }
        return -1;
    }
    
    public int calculateCost(Entity enemy) {
        int numberOfTilesAway = getEntity().distanceFrom(enemy);
        if(numberOfTilesAway <= attackDistance) {
            return getActionCost();
        }
        return -1;
    }    
    
    public int calculateAttackPercentage(Entity attacker) {
        Randomizer rand = game.getRandomizer();
        int d10 = rand.nextInt(10) * 10;                    
        
        return d10 + calculateStrictAttackPercentage(attacker);
    }
    
    public int calculateStrictAttackPercentage(Entity attacker) {        
        int attackBonus = 0;
        MapTile tile = attacker.getTileOn();
        if(tile!=null) {
            TerrainTileData terrain = tile.geTerrainTileData();
            if(terrain!=null) {
                attackBonus = terrain.attackBonus;
            }
        }
        
        return hitPercentage + attackBonus;
    }
    
    public int calculateDefencePercentage(Entity defender) {
        Randomizer rand = game.getRandomizer();
        int d10 = rand.nextInt(10) * 10;
        
        return d10 + calculateStrictDefencePercentage(defender);
    }
    
    public int calculateStrictDefencePercentage(Entity defender) {
        int defenseBonus = 0;
        MapTile tile = defender.getTileOn();
        if(tile!=null) {
            TerrainTileData terrain = tile.geTerrainTileData();
            if(terrain!=null) {
                defenseBonus= terrain.defenseBonus;
            }
        }
        
        return defender.calculateDefenseScore() + defenseBonus;
    }
}

/*
 * see license.txt 
 */
package franks.game.net;

import franks.game.entity.Direction;
import franks.game.entity.EntityType;
import franks.game.entity.EntityState;
import franks.math.Vector2f;

/**
 * @author Tony
 *
 */
public class NetEntityPartial {

    public int id;
    public EntityType entityType;
    public String name;
    public Vector2f pos;
    public int health;
    public EntityState currentState;
    public Direction currentDirection;
    public int actionPointsAmount;
}

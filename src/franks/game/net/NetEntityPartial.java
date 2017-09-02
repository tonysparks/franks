/*
 * see license.txt 
 */
package franks.game.net;

import franks.game.entity.Direction;
import franks.game.entity.Entity.State;
import franks.game.entity.Entity.Type;
import franks.math.Vector2f;

/**
 * @author Tony
 *
 */
public class NetEntityPartial {

    public int id;
    public Type type;
    public String name;
    public Vector2f pos;
    public int health;
    public State currentState;
    public Direction currentDirection;
    public int actionPointsAmount;
}

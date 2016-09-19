/*
 * see license.txt
 */
package franks.gfx;

import franks.util.TimeStep;

/**
 * Handles Keyboard game input
 * 
 * @author Tony
 *
 */
public class KeyboardGameController extends Inputs implements GameController {
	
    /*
     * (non-Javadoc)
     * @see seventh.client.GameController#pollInputs(seventh.shared.TimeStep, seventh.client.KeyMap, seventh.client.gfx.Cursor, int)
     */
    @Override
    public int pollInputs(TimeStep timeStep, KeyMap keyMap, Cursor cursor, int inputKeys) {

        
        if(isKeyDown(keyMap.getUpKey())) {
            inputKeys |= KeyActions.UP.getMask();
        }
        else if(isKeyDown(keyMap.getDownKey())) {
            inputKeys |= KeyActions.DOWN.getMask();
        }
        
        if(isKeyDown(keyMap.getLeftKey())) {
            inputKeys |= KeyActions.LEFT.getMask();
        }
        else if(isKeyDown(keyMap.getRightKey())) {
            inputKeys |= KeyActions.RIGHT.getMask();
        }
                
        return inputKeys;
    }
}

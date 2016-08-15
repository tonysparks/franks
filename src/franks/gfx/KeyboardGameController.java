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
        if(isKeyDown(keyMap.getWalkKey())) {
            inputKeys |= KeyActions.WALK.getMask();
        }
        
        if(isKeyDown(keyMap.getCrouchKey())) {
            inputKeys |= KeyActions.CROUCH.getMask();
        }
        
        if(isKeyDown(keyMap.getSprintKey())) {
            inputKeys |= KeyActions.SPRINT.getMask();
        }
        
        if(isKeyDown(keyMap.getUseKey())) {
            inputKeys |= KeyActions.USE.getMask();
        }
        
        if(isKeyDown(keyMap.getDropWeaponKey())) {
            inputKeys |= KeyActions.DROP_WEAPON.getMask();
        }
        
        if(isKeyDown(keyMap.getMeleeAttackKey())) {
            inputKeys |= KeyActions.MELEE_ATTACK.getMask();
        }
        
        if(isKeyDown(keyMap.getReloadKey()) ) {
            inputKeys |= KeyActions.RELOAD.getMask();
        }
        
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
        
        if(isButtonDown(keyMap.getFireKey()) || isKeyDown(keyMap.getFireKey()) ) {
            inputKeys |= KeyActions.FIRE.getMask();
        }
        
        if(isButtonDown(keyMap.getThrowGrenadeKey()) || isKeyDown(keyMap.getThrowGrenadeKey())) {
            inputKeys |= KeyActions.THROW_GRENADE.getMask();
        }
        
        return inputKeys;
    }
}

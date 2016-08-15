/*
 * see license.txt
 */
package franks.gfx;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;

import franks.util.TimeStep;

/**
 * Handles Joystick inputs and maps them to game actions
 * 
 * @author Tony
 *
 */
public class JoystickGameController extends ControllerInput implements GameController {
    
    private boolean[] isButtonReleased;
    
    public JoystickGameController() {
        this.isButtonReleased = new boolean[64];
    }
    
    @Override
    public boolean buttonDown(Controller controller, int button) {
        boolean result = super.buttonDown(controller, button);
        if(button >-1 && button < this.isButtonReleased.length)
            this.isButtonReleased[button] = false;
        return result;
    }
    
    @Override
    public boolean buttonUp(Controller controller, int button) {
        boolean result = super.buttonUp(controller, button);
        
        if(button >-1 && button < this.isButtonReleased.length)
            this.isButtonReleased[button] = true;
        return result;
    }
    
    public boolean isButtonReleased(ControllerButtons button) {
        switch(button) {
            case NORTH_DPAD_BTN:
                return !isPovDirectionDown(PovDirection.north);
            case NE_DPAD_BTN:
                return !isPovDirectionDown(PovDirection.northEast);
            case EAST_DPAD_BTN:
                return !isPovDirectionDown(PovDirection.east);
            case SE_DPAD_BTN:
                return !isPovDirectionDown(PovDirection.southEast);
            case SOUTH_DPAD_BTN:
                return !isPovDirectionDown(PovDirection.south);
            case SW_DPAD_BTN:
                return !isPovDirectionDown(PovDirection.southWest);
            case WEST_DPAD_BTN:
                return !isPovDirectionDown(PovDirection.west);
            case NW_DPAD_BTN:
                return !isPovDirectionDown(PovDirection.northWest);
            
            case LEFT_TRIGGER_BTN:
                return !isLeftTriggerDown();
            case RIGHT_TRIGGER_BTN:
                return !isRightTriggerDown();
            
            case LEFT_BUMPER_BTN:
                return this.isButtonReleased[4];
            case RIGHT_BUMPER_BTN:
                return this.isButtonReleased[5];

            case LEFT_JOYSTICK_BTN:
                return this.isButtonReleased[8];
            case RIGHT_JOYSTICK_BTN:
                return this.isButtonReleased[9];
            
            case START_BTN:
                return this.isButtonReleased[7];
            case SELECT_BTN:
                return this.isButtonReleased[6];
              
            
            case A_BTN:
                return this.isButtonReleased[0];
            case B_BTN:
                return this.isButtonReleased[1];
            case X_BTN:
                return this.isButtonReleased[2];
            case Y_BTN:
                return this.isButtonReleased[3];                        
            default:
                return false;
        }
    }
    
    private void flushButtonReleaseState() {
        for(int i = 0; i < this.isButtonReleased.length; i++) {
            this.isButtonReleased[i] = false;
        }
    }
    
    private int handleDPad(int inputKeys) {
        if (isPovDirectionDown(PovDirection.north)) {
            inputKeys |= KeyActions.UP.getMask();
        }
        else if (isPovDirectionDown(PovDirection.northEast)) {
            inputKeys |= KeyActions.UP.getMask();
            inputKeys |= KeyActions.RIGHT.getMask();
        }

        else if (isPovDirectionDown(PovDirection.northWest)) {
            inputKeys |= KeyActions.UP.getMask();
            inputKeys |= KeyActions.LEFT.getMask();
        }

        else if (isPovDirectionDown(PovDirection.south)) {
            inputKeys |= KeyActions.DOWN.getMask();
        }
        else if (isPovDirectionDown(PovDirection.southEast)) {
            inputKeys |= KeyActions.DOWN.getMask();
            inputKeys |= KeyActions.RIGHT.getMask();
        }
        else if (isPovDirectionDown(PovDirection.southWest)) {
            inputKeys |= KeyActions.DOWN.getMask();
            inputKeys |= KeyActions.LEFT.getMask();
        }
        else if (isPovDirectionDown(PovDirection.east)) {
            inputKeys |= KeyActions.RIGHT.getMask();
        }
        else if (isPovDirectionDown(PovDirection.west)) {
            inputKeys |= KeyActions.LEFT.getMask();
        }
        
        return inputKeys;
    }
    
    private int handleMovement(KeyMap keyMap, Cursor cursor, int inputKeys) {
        if(keyMap.isSouthPaw()) {
            if (isYAxisMovedDownOnRightJoystick()) {
                inputKeys |= KeyActions.DOWN.getMask();
            }
            if (isYAxisMovedUpOnRightJoystick()) {
                inputKeys |= KeyActions.UP.getMask();
            }

            if (isXAxisMovedRightOnRightJoystick()) {
                inputKeys |= KeyActions.RIGHT.getMask();
            }
            if (isXAxisMovedLeftOnRightJoystick()) {
                inputKeys |= KeyActions.LEFT.getMask();
            }

            if (isXAxisOnLeftJoystickMoved() || isYAxisOnLeftJoystickMoved()) {
                float dx = getLeftJoystickXAxis();
                float dy = getLeftJoystickYAxis();
                
                if(keyMap.isJoystickInverted()) {
                    cursor.moveByDelta(-dx, -dy);
                }
                else {
                    cursor.moveByDelta(dx, dy);
                }
            }
        }
        else {
            if (isYAxisMovedDownOnLeftJoystick()) {
                inputKeys |= KeyActions.DOWN.getMask();
            }
            if (isYAxisMovedUpOnLeftJoystick()) {
                inputKeys |= KeyActions.UP.getMask();
            }

            if (isXAxisMovedRightOnLeftJoystick()) {
                inputKeys |= KeyActions.RIGHT.getMask();
            }
            if (isXAxisMovedLeftOnLeftJoystick()) {
                inputKeys |= KeyActions.LEFT.getMask();
            }

            if (isXAxisOnRightJoystickMoved() || isYAxisOnRightJoystickMoved()) {
                float dx = getRightJoystickXAxis();
                float dy = getRightJoystickYAxis();
                
                if(keyMap.isJoystickInverted()) {
                    cursor.moveByDelta(-dx, -dy);
                }
                else {
                    cursor.moveByDelta(dx, dy);
                }
            }
        }
        
        inputKeys = handleDPad(inputKeys);
        
        return inputKeys;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see seventh.client.GameController#pollInputs(seventh.shared.TimeStep,
     * seventh.client.KeyMap, seventh.client.gfx.Cursor, int)
     */
    @Override
    public int pollInputs(TimeStep timeStep, KeyMap keyMap, Cursor cursor, int inputKeys) {

        if (isConnected()) {
            inputKeys = handleMovement(keyMap, cursor, inputKeys);
            
            if (isButtonDown(keyMap.getFireBtn())) {
                inputKeys |= KeyActions.FIRE.getMask();
            }
            if (isButtonDown(keyMap.getThrowGrenadeBtn())) {
                inputKeys |= KeyActions.THROW_GRENADE.getMask();
            }
            if (isButtonDown(keyMap.getReloadBtn())) {
                inputKeys |= KeyActions.RELOAD.getMask();
            }
            
            // TODO: Make configurable
            if (isButtonReleased(ControllerButtons.Y_BTN)) {
                inputKeys |= KeyActions.WEAPON_SWITCH_UP.getMask();
            }
            
            if (isButtonDown(keyMap.getMeleeAttackBtn())) {
                inputKeys |= KeyActions.MELEE_ATTACK.getMask();
            }
            
            if (isButtonDown(keyMap.getCrouchBtn())) {
                inputKeys |= KeyActions.CROUCH.getMask();
            }            
            
            if (isButtonDown(keyMap.getSprintBtn())) {
                inputKeys |= KeyActions.SPRINT.getMask();
            }
            
            if (isButtonDown(keyMap.getWalkBtn())) {
                inputKeys |= KeyActions.WALK.getMask();
            }
            
            if (isButtonDown(keyMap.getDropWeaponBtn())) {
                inputKeys |= KeyActions.DROP_WEAPON.getMask();
            }
            
            if (isButtonDown(keyMap.getUseBtn())) {
                inputKeys |= KeyActions.USE.getMask();
            }
        }

        flushButtonReleaseState();
        
        return inputKeys;
    }

}

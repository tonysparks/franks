/*
 * see license.txt 
 */
package franks.util;


/**
 * A very simple finite gameState machine
 * 
 * @author Tony
 *
 */
public class StateMachine<T extends State> {

    /**
     * Listens to gameState transitions
     * 
     * @author Tony
     *
     * @param <T>
     */
    public static interface StateMachineListener<T> {
        public void onEnterState(T state);
        public void onExitState(T state);
    }
    
    private T currentState;
    private StateMachineListener<T> listener;
    
    /**    
     */
    public StateMachine() {
        this.currentState = null;
    }

    /**
     * @param gameState the gameState to set
     */
    public void setListener(StateMachineListener<T> listener) {
        this.listener = listener;
    }
    
    /**
     * @return the gameState
     */
    public StateMachineListener<T> getListener() {
        return listener;
    }
    
    /**
     * Updates
     * 
     * @param timeStep
     */
    public void update(TimeStep timeStep) {
        if(this.currentState!=null) {
            this.currentState.update(timeStep);
        }
    }
    
    /**
     * Changes to the new {@link State}
     * @param newState
     */
    public void changeState(T newState) {                
        if(this.currentState != null) {
            this.currentState.exit();
            if(this.listener != null) {
                this.listener.onExitState(this.currentState);
            }
        }
        
        if(newState!=null) {
            newState.enter();
            if(this.listener != null) {
                this.listener.onEnterState(newState);
            }
        }
        
        this.currentState = newState;
    }
    
    /**
     * @return the currentState
     */
    public T getCurrentState() {
        return currentState;
    }
    
}

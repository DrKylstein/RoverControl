/**
 * 
 */
package com.example.rovercontrol;

/**
 * @author kyle
 *
 */
public interface State<T> {
	/**
	 * Called when the StateMachine switches to this state.
	 */
	void onEnter(T owner);
	/**
	 * Called when the StateMachine switches to a different state.
	 */
	void onExit(T owner);
	/**
	 * Runs the operation for this State.
	 * @param dtNanos time since the last call in nanoseconds, 
	 * use this to adjust real-time calculations appropriately.
	 */
	void update(long dtNanos, T owner);
	
	String getName();
}

/**
 * 
 */
package com.example.rovercontrol;

/**
 * @author kyle
 *
 */
public class StateMachine {
	private State _currentState;
	private State _globalState;
	
	public void changeState(State newState) {
		if(_currentState != null) {
			_currentState.onExit();
		}
		_currentState = newState;
		_currentState.onEnter();
	}
	
	public void changeGlobalState(State newState) {
		if(_globalState != null) {
			_globalState.onExit();
		}
		_globalState = newState;
		_globalState.onEnter();
	}
	/**
	 * Executes the current state code.
	 * @param dtNanos Time since last call in nanoseconds, used to adjust real-time calculations.
	 */
	public void update(long dtNanos) {
		System.out.println("rover_debug StateMachine.update");
		if(_globalState != null) {
			_globalState.update(dtNanos, this);
		}
		if(_currentState != null) {
			_currentState.update(dtNanos, this);
		}
	}
}

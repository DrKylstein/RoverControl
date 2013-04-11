/**
 * 
 */
package com.example.rovercontrol.control;


/**
 * @author kyle
 *
 */
public class StateMachine<T> {
	private State<T> _currentState;
	private T _owner;
	
	public StateMachine(T owner) {
		_owner = owner;
	}
	
	public void changeState(State<T> newState) {
		if(_currentState != null) {
			_currentState.onExit(_owner);
		}
		_currentState = newState;
		_currentState.onEnter(_owner);
	}
	public String getStateName() {
		return _currentState.getName();
	}
	/**
	 * Executes the current state code.
	 * @param dtNanos Time since last call in nanoseconds, used to adjust real-time calculations.
	 */
	public void update(long dtNanos) {
		System.out.println("rover_debug StateMachine.update");
		if(_currentState != null) {
			_currentState.update(dtNanos, _owner);
		}
	}
}

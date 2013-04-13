/**
 * 
 */
package com.example.rovercontrol.mission;

import com.example.rovercontrol.Robot;
import com.example.rovercontrol.control.State;

/**
 * @author kyle
 *
 */
public class RetrievePuckState implements State<Robot> {
	
	/* (non-Javadoc)
	 * @see com.example.rovercontrol.State#onEnter()
	 */
	@Override
	public void onEnter(Robot robot) {

	}

	/* (non-Javadoc)
	 * @see com.example.rovercontrol.State#onExit()
	 */
	@Override
	public void onExit(Robot robot) {

	}

	/* (non-Javadoc)
	 * @see com.example.rovercontrol.State#update(long, com.example.rovercontrol.StateMachine)
	 */
	@Override
	public void update(long dtNanos, Robot robot) {
		if(robot.irSensor.voltage() > 0.8) {
			if(robot.grabber.grab()) {
				// TODO state to transition to
				robot.stateMachine.changeState(null);
			}
		}
	}
	@Override
	public String getName() {
		return "Retrieve Puck";
	}
}

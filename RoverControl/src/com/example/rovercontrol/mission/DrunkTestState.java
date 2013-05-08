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
public class DrunkTestState implements State<Robot> {
	
	//private final long SECOND = 1000000000;
	
	/* (non-Javadoc)
	 * @see com.example.rovercontrol.State#onEnter()
	 */
	@Override
	public void onEnter(Robot robot) {
		robot.vision.startCapture();
	}

	/* (non-Javadoc)
	 * @see com.example.rovercontrol.State#onExit()
	 */
	@Override
	public void onExit(Robot robot) {
		robot.vision.stopCapture();
	}

	/* (non-Javadoc)
	 * @see com.example.rovercontrol.State#update(long, com.example.rovercontrol.StateMachine)
	 */
	@Override
	public void update(long dtNanos, Robot robot) {
		robot.motion.setSpeed(0.5);
		robot.motion.setRotationSpeed(0.0);
	}

	/* (non-Javadoc)
	 * @see com.example.rovercontrol.State#getName()
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Sir, have you been drinking?";
	}

}

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
public class MotionTestState implements State<Robot> {
	
	@Override
	public void onEnter(Robot robot) {
		
	}

	@Override
	public void onExit(Robot robot) {
		
	}

	@Override
	public void update(long dtNanos, Robot robot) {
		robot.motion.setSpeed(0.5);
		robot.motion.setRotationSpeed(0.0);
		if(robot.vision.servicesAvailable() && robot.vision.cameraAvailable()) {
			robot.vision.grabToLog();
		}
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "MotionTestState";
	}

}

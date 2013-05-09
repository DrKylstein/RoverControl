package com.example.rovercontrol.mission;

import com.example.rovercontrol.Robot;
import com.example.rovercontrol.control.State;

public class VisionTestState implements State<Robot> {

	@Override
	public void onEnter(Robot robot) {
		robot.vision.startCapture();
	}

	@Override
	public void onExit(Robot robot) {
		robot.vision.stopCapture();
	}

	@Override
	public void update(long dtNanos, Robot robot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		return "Vision Test";
	}

}

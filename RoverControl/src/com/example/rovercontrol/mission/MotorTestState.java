package com.example.rovercontrol.mission;

import com.example.rovercontrol.Robot;
import com.example.rovercontrol.control.State;

public class MotorTestState implements State<Robot> {

	@Override
	public void onEnter(Robot owner) {
		// TODO Auto-generated method stub
		owner.motion.stopPID();
	}

	@Override
	public void onExit(Robot owner) {
		// TODO Auto-generated method stub
		owner.motion.startPID();
	}

	@Override
	public void update(long dtNanos, Robot owner) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "MotorTestState";
	}

}

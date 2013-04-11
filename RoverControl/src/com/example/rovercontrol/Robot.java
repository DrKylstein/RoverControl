package com.example.rovercontrol;

import com.example.rovercontrol.control.StateMachine;
import com.example.rovercontrol.io.RobotMotion;

public class Robot {
	public RobotMotion motion;
	public StateMachine<Robot> stateMachine;
	private long _lastNanoTime;
	
	public Robot(RobotMotion motion_) {
		motion = motion_;
		stateMachine = new StateMachine<Robot>(this);
	}
	
	public void update() {
		long currentTime = System.nanoTime();
		stateMachine.update(currentTime - _lastNanoTime);
		_lastNanoTime = currentTime;
	}
}

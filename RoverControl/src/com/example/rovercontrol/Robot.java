package com.example.rovercontrol;

import ioio.lib.api.IOIO;

import com.example.rovercontrol.control.StateMachine;
import com.example.rovercontrol.io.GrabberPiston;
import com.example.rovercontrol.io.IRSensor;
import com.example.rovercontrol.io.RobotMotion;

public class Robot {
	public RobotMotion motion;
	public IRSensor irSensor;
	public GrabberPiston grabber;
	public StateMachine<Robot> stateMachine;
	private long _lastNanoTime;
	private final int PISTON_PIN = 12;
	private final int IR_PIN = 40;
	//private final int _TX_PIN = 14;

	
	public Robot(RobotMotion motion_) {
		irSensor = new IRSensor(IR_PIN);
		grabber = new GrabberPiston(PISTON_PIN);
		motion = motion_;
		stateMachine = new StateMachine<Robot>(this);
	}
	
	public void resetHardware(IOIO ioio) {
		irSensor.reset(ioio);
	}
	
	public void update() {
		long currentTime = System.nanoTime();
		stateMachine.update(currentTime - _lastNanoTime);
		_lastNanoTime = currentTime;
	}
}

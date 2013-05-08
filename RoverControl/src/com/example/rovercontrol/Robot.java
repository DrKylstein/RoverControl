package com.example.rovercontrol;

import ioio.lib.api.IOIO;

import com.example.rovercontrol.control.StateMachine;
import com.example.rovercontrol.io.GrabberPiston;
import com.example.rovercontrol.io.IRSensor;
import com.example.rovercontrol.io.RobotMotion;
import com.example.rovercontrol.io.RobotOrientation;
import com.example.rovercontrol.io.RobotVision;
import com.example.rovercontrol.io.UDPClient;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;

public class Robot {
	public RobotMotion motion;
	public IRSensor irSensor;
	public GrabberPiston grabber;
	public StateMachine<Robot> stateMachine;
	public RobotOrientation orientation;
	public UDPClient udpClient;
	public RobotVision vision;
	
	private long _lastNanoTime;
	private final int PISTON_PIN = 12;
	private final int IR_PIN = 40;	
	private final int UDP_PORT = 4444;
	private final String HOST_NAME = "192.168.43.190";
	
	//private final int _TX_PIN = 14;

	
	public Robot(RobotMotion motion_, RobotOrientation orientation_, RobotVision vision_) {
		udpClient = new UDPClient(UDP_PORT, HOST_NAME);
		irSensor = new IRSensor(IR_PIN);
		grabber = new GrabberPiston(PISTON_PIN);
		motion = motion_;
		stateMachine = new StateMachine<Robot>(this);
		orientation = orientation_;
		_looper = new _RobotLooper();
		vision = vision_;
	}
	
	public void resetHardware(IOIO ioio) {
		irSensor.reset(ioio);
	}
	
	public void update() {
		long currentTime = System.nanoTime();
		stateMachine.update(currentTime - _lastNanoTime);
		_lastNanoTime = currentTime;
	}
	
	public void start() {
		new Thread(_looper).start();
	}
	
	private class _RobotLooper implements Runnable {

		@Override
		public void run() {
			while(true) {
				update();
			}
		}
	}
	_RobotLooper _looper;
}
